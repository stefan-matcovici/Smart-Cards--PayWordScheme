package com.company.main;

import com.company.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class MainUser {
    private static final int BROKER_SERVER_PORT = 6789;
    private static final int SELLER_PORT = 6790;

    public static void main(String[] args) throws Exception {
        User user1 = new User();
        user1.registerToBroker(BROKER_SERVER_PORT);
        Socket sellerSocket1 = user1.commitToSeller(SELLER_PORT);
        user1.payToSeller(sellerSocket1, 10);
        user1.payToSeller(sellerSocket1, 20);
//
//        User user2 = new User();
//        user2.registerToBroker(BROKER_SERVER_PORT);
//        Socket sellerSocket2 = user2.commitToSeller(SELLER_PORT);
//        user2.payToSeller(sellerSocket2, 20);
    }
}
