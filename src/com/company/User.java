package com.company;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {

    private static final int BROKER_SERVER_PORT = 6789;

    public void registerToBroker() throws IOException {
        Socket userSocket = new Socket("localhost", BROKER_SERVER_PORT);

        DataOutputStream outToServer = new DataOutputStream(userSocket.getOutputStream());

        outToServer.writeBytes("Hello world!\n");

        userSocket.close();
    }
}
