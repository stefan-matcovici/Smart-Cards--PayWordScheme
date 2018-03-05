package com.company;

import com.company.models.Payment;
import com.company.models.SignedCommit;
import com.company.models.UserPaymentDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Seller {

    private static final int SELLER_PORT = 6791;
    private static final int SELLER_BROKER_SERVER_PORT = 6790;
    private final ObjectMapper objectMapper;
    private ServerSocket sellerServerSocket;
    private Map<Socket, UserPaymentDetails> lastUserPaymentDetails = new ConcurrentHashMap<>();
    private Map<Socket, BufferedReader> userReaders = new ConcurrentHashMap<>();

    public Seller() throws IOException {
        sellerServerSocket = new ServerSocket(SELLER_PORT);
        objectMapper = new ObjectMapper();
    }

    public Socket receiveCommitFromUser() throws Exception {
        Socket userConnectionSocket = sellerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        userReaders.put(userConnectionSocket, inFromUser);

        SignedCommit signedCommit = objectMapper.readValue(inFromUser.readLine(), SignedCommit.class);

        signedCommit.verifySignature();
        signedCommit.getPlainCommit().getSignedCertificateFromBrokerToUser().verifySignature();

        UserPaymentDetails userPaymentDetails = new UserPaymentDetails();
        userPaymentDetails.setPaymentIndex(0);
        userPaymentDetails.setLastDigest(signedCommit.getPlainCommit().getHashChainRoot());
        userPaymentDetails.setCommit(signedCommit.getPlainCommit());

        lastUserPaymentDetails.put(userConnectionSocket, userPaymentDetails);
        return userConnectionSocket;
    }

    public void receivePaymentsFromUser(Socket userSocket) throws Exception {
        BufferedReader reader = userReaders.get(userSocket);
        Thread thread = new Thread(() -> {
            Payment payment;
            try {

                String content = reader.readLine();
                while (content != null) {
                    payment = objectMapper.readValue(content, Payment.class);
                    UserPaymentDetails userPaymentDetails = lastUserPaymentDetails.get(userSocket);
                    userPaymentDetails.processPayment(payment);
                    content = reader.readLine();
                }

                userSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public void sendCommit() throws IOException {
        Socket sellerSocketToBroker = new Socket("localhost", SELLER_BROKER_SERVER_PORT);
        DataOutputStream outToBroker = new DataOutputStream(sellerSocketToBroker.getOutputStream());

        lastUserPaymentDetails.forEach((key, value) -> {
            try {
                outToBroker.write(objectMapper.writeValueAsBytes(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        outToBroker.close();
        sellerSocketToBroker.close();
    }

    public Map<Socket, UserPaymentDetails> getLastUserPaymentDetails() {
        return lastUserPaymentDetails;
    }
}
