package com.company.main;

import com.company.Broker;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainBroker {
    public static void main(String[] args) throws Exception {
        Broker broker = new Broker();

        new Thread(() -> {
            try {
                System.out.println("Started to process commits from users...");
                while (true) {
                    broker.processCommitsFromSellers();
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Starting to register users...");
        while (true) {
            broker.registerUser();
        }
    }
}
