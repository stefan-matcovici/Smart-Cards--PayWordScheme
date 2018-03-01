package com.company;

import com.company.models.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class Broker {
    private static final int BROKER_SERVER_PORT = 6789;
    private ServerSocket brokerServerSocket;

    public Broker() throws IOException {
        brokerServerSocket = new ServerSocket(BROKER_SERVER_PORT);
    }

    public void registerUser() throws IOException {
        Socket userConnectionSocket = brokerServerSocket.accept();

        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));

        String messageFromUser = inFromClient.readLine();


        final Identity identity = new ObjectMapper().readValue(messageFromUser, Identity.class);
        System.out.println(identity);
        PublicKey publicKey = identity.getPublicKey();
        System.out.println(publicKey);

        userConnectionSocket.close();
    }
}
