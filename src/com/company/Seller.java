package com.company;

import com.company.models.Commit;
import com.company.models.HashChainCommit;
import com.company.models.Payment;
import com.company.models.PaymentWithDifferentValues;
import com.company.models.SignedCommit;
import com.company.models.UserPaymentDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        System.out.println("Receiving commits from users...");

        Socket userConnectionSocket = sellerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        userReaders.put(userConnectionSocket, inFromUser);

        SignedCommit signedCommit = objectMapper.readValue(inFromUser.readLine(), SignedCommit.class);

        System.out.printf("Received the following signed commit from the user <%s>...\n\n", signedCommit);

        System.out.printf("Verifying the signature from the signed commit <%s>...\n\n", signedCommit);
        signedCommit.verifySignature();

        System.out.printf("Verifying the certificate between the broker and the user <%s>...\n\n", signedCommit.getPlainCommit().getSignedCertificateFromBrokerToUser());
        signedCommit.getPlainCommit().getSignedCertificateFromBrokerToUser().verifySignature();

        Commit commit = signedCommit.getPlainCommit();

        UserPaymentDetails userPaymentDetails = new UserPaymentDetails();

        List<Payment> payments = new ArrayList<>();
        for (HashChainCommit hashChainCommit: commit.getHashChainCommits()) {
            Payment payment = new Payment();
            payment.setCurrentDigest(hashChainCommit.getHashChainRoot());
            payment.setCurrentPaymentIndex(0);
            payment.setPaymentValue(hashChainCommit.getValue());

            payments.add(payment);
        }

        PaymentWithDifferentValues paymentWithDifferentValues = new PaymentWithDifferentValues();
        paymentWithDifferentValues.setPaymentsWithDifferentValues(payments);

        userPaymentDetails.setPayments(paymentWithDifferentValues);
        userPaymentDetails.setCommit(commit);

        System.out.printf("Associating a UserPaymentDetails <%s> to the user socket <%s>.\n\n", userPaymentDetails, userConnectionSocket);
        lastUserPaymentDetails.put(userConnectionSocket, userPaymentDetails);

        return userConnectionSocket;
    }

    public void receivePaymentsFromUser(Socket userSocket) throws Exception {
        BufferedReader reader = userReaders.get(userSocket);
        Thread thread = new Thread(() -> {
            PaymentWithDifferentValues payment;
            try {
                System.out.printf("Starting to receive payments for user with the socket <%s>...\n\n", userSocket);

                String content = reader.readLine();
                while (content != null) {
                    payment = objectMapper.readValue(content, PaymentWithDifferentValues.class);

                    System.out.printf("Received the following payment <%s> through user socket <%s>...\n\n", payment, userSocket);

                    UserPaymentDetails userPaymentDetails = lastUserPaymentDetails.get(userSocket);

                    System.out.printf("The user with the socket <%s> has the last userPaymentDetails <%s>\n\n", userSocket, userPaymentDetails);

                    System.out.printf("Processing the payment <%s> for the user with the socket <%s>.\n\n", payment, userSocket);

                    userPaymentDetails.processPaymentWithDifferentValues(payment);

                    System.out.printf("The new processed userPaymentDetails is now <%s> for the user with the socket <%s>.\n\n", userPaymentDetails, userSocket);

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

        System.out.printf("Sending the user payments <%s> to the broker...\n\n", lastUserPaymentDetails);

        lastUserPaymentDetails.forEach((key, value) -> {
            try {
                outToBroker.write(objectMapper.writeValueAsBytes(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("Sent the user payments <%s> to the broker...\n\n", lastUserPaymentDetails);

        outToBroker.close();
        sellerSocketToBroker.close();
    }

    public Map<Socket, UserPaymentDetails> getLastUserPaymentDetails() {
        return lastUserPaymentDetails;
    }
}
