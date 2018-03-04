package com.company.main;

import com.company.Seller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class MainSeller {
    public static void main(String[] args) throws Exception {
        Seller seller = new Seller();
        Socket userSocket1 = seller.receiveCommitFromUser();

        seller.receivePaymentsFromUser(userSocket1);
        seller.receivePaymentsFromUser(userSocket1);
        seller.receivePaymentsFromUser(userSocket1);

        Socket userSocket2 = seller.receiveCommitFromUser();

        seller.receivePaymentsFromUser(userSocket2);
        seller.receivePaymentsFromUser(userSocket2);

        System.out.println(seller.getLastUserPaymentDetails());

        userSocket1.close();
        userSocket2.close();
    }
}
