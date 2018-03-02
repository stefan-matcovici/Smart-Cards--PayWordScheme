package com.company;

import com.company.models.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class Broker {
    private static final int BROKER_SERVER_PORT = 6789;
    private ServerSocket brokerServerSocket;

    public Broker() throws IOException {
        brokerServerSocket = new ServerSocket(BROKER_SERVER_PORT);
    }

    public void registerUser() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Socket userConnectionSocket = brokerServerSocket.accept();

        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        String messageFromUser = inFromClient.readLine();


        final Identity identity = new ObjectMapper().readValue(messageFromUser, Identity.class);
        PublicKey pk = identity.computePublicKey();

        System.out.println(pk.toString());


        userConnectionSocket.close();
    }
}
