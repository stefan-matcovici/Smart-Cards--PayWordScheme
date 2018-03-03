package com.company;

import com.company.models.Payment;
import com.company.models.SignedCommit;
import com.company.models.UserPaymentDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Seller {

    private static final int SELLER_PORT = 6790;
    private final ObjectMapper objectMapper;
    private ServerSocket sellerServerSocket;
    private Map<Socket, UserPaymentDetails> lastUserPaymentDetails = new ConcurrentHashMap<>();
    private BufferedReader inFromUser;

    public Seller() throws IOException {
        sellerServerSocket = new ServerSocket(SELLER_PORT);
        objectMapper = new ObjectMapper();
    }

    public Socket receiveCommitFromUser() throws Exception {
        Socket userConnectionSocket = sellerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

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
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

        System.out.println(inFromUser.readLine());
//        Payment payment = objectMapper.readValue(inFromUser.readLine(), Payment.class);
//        UserPaymentDetails userPaymentDetails = lastUserPaymentDetails.get(userSocket);
//        userPaymentDetails.processPayment(payment);
    }

    public Map<Socket, UserPaymentDetails> getLastUserPaymentDetails() {
        return lastUserPaymentDetails;
    }
}
