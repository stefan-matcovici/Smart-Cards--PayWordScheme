package com.company;

import com.company.models.SignedCommit;
import com.company.models.UserPaymentDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Seller {

    private static final int SELLER_PORT = 6790;
    private final ObjectMapper objectMapper;
    private ServerSocket sellerServerSocket;
    private Map<Integer, UserPaymentDetails> lastUserPaymentDetails = new HashMap<>();

    public Seller() throws IOException {
        sellerServerSocket = new ServerSocket(SELLER_PORT);
        objectMapper = new ObjectMapper();
    }

    public void receiveCommitFromUser() throws Exception {
        Socket userConnectionSocket = sellerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        SignedCommit signedCommit = objectMapper.readValue(inFromUser.readLine(), SignedCommit.class);

        signedCommit.verifySignature();
        signedCommit.getPlainCommit().getSignedCertificateFromBrokerToUser().verifySignature();

        UserPaymentDetails userPaymentDetails = new UserPaymentDetails();
        userPaymentDetails.setPaymentIndex(0);
        userPaymentDetails.setLastDigest(signedCommit.getPlainCommit().getHashChainRoot());

        lastUserPaymentDetails.put(userConnectionSocket.getPort(), userPaymentDetails);
    }
}
