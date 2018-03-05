package com.company.main;

import com.company.User;

import java.net.Socket;

public class MainUser {
    private static final int BROKER_SERVER_PORT = 6789;
    private static final int SELLER_PORT = 6791;

    public static void main(String[] args) throws Exception {
        User user1 = new User();
        user1.registerToBroker(BROKER_SERVER_PORT);
        Socket sellerSocket1 = user1.commitToSeller(SELLER_PORT);

        user1.payToSeller(sellerSocket1, 10);
//        user1.payToSeller(sellerSocket1, 20);
//        user1.payToSeller(sellerSocket1, 50);
        sellerSocket1.close();

//        User user2 = new User();
//        user2.registerToBroker(BROKER_SERVER_PORT);
//        Socket sellerSocket2 = user2.commitToSeller(SELLER_PORT);
//        user2.payToSeller(sellerSocket2, 20);
//        user2.payToSeller(sellerSocket2, 30);
//        sellerSocket2.close();
    }
}
