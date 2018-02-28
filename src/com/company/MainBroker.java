package com.company;

import java.io.IOException;

public class MainBroker {
    public static void main(String[] args) throws IOException {
        Broker broker = new Broker();
        broker.registerUser();
    }
}
