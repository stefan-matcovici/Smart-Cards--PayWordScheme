package com.company.main;

import com.company.Seller;

import java.net.Socket;


public class MainSeller {
    public static void main(String[] args) throws Exception {
        Seller seller = new Seller();

//        while (true) {
            Socket userSocket1 = seller.receiveCommitFromUser();
            seller.receivePaymentsFromUser(userSocket1);
            seller.sendCommit();
//        }
    }
}
