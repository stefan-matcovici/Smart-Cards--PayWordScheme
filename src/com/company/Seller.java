package com.company;

import com.company.models.SignedCommit;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class Seller {

    private static final int SELLER_PORT = 6790;
    private final ObjectMapper objectMapper;
    private ServerSocket sellerServerSocket;

    public Seller() throws IOException, NoSuchAlgorithmException {
        sellerServerSocket = new ServerSocket(SELLER_PORT);
        objectMapper = new ObjectMapper();
    }

    public void receiveCommitFromUser() throws Exception {
        Socket userConnectionSocket = sellerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        SignedCommit signedCommit = objectMapper.readValue(inFromUser.readLine(), SignedCommit.class);

        System.out.println(signedCommit.verifySignature());
        System.out.println(signedCommit.getPlainCommit().getSignedCertificateFromBrokerToUser().verifySignature());
    }
}
