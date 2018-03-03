package com.company.main;

import com.company.Seller;

import java.net.Socket;

import static java.lang.Thread.sleep;

public class MainSeller {
    public static void main(String[] args) throws Exception {
        Seller seller = new Seller();
        while(true) {
            Socket userSocket = seller.receiveCommitFromUser();
            seller.receivePaymentsFromUser(userSocket);
            sleep(1000);
            System.out.println(seller.getLastUserPaymentDetails());
        }

    }
}
