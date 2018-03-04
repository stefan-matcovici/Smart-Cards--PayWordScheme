package com.company.main;

import com.company.Seller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class MainSeller {
    public static void main(String[] args) throws Exception {
        Seller seller = new Seller();

        Thread watcher = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10000);
                    System.out.println(seller.getLastUserPaymentDetails());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        watcher.start();

        while (true) {
            Socket userSocket1 = seller.receiveCommitFromUser();
            seller.receivePaymentsFromUser(userSocket1);
        }
    }
}
