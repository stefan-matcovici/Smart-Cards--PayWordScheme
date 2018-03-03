package com.company.main;

import com.company.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class MainUser {
    private static final int BROKER_SERVER_PORT = 6789;
    private static final int SELLER_PORT = 6790;

    public static void main(String[] args) throws Exception {
        User user = new User();
        user.registerToBroker(BROKER_SERVER_PORT);
        user.commitToSeller(SELLER_PORT);
    }
}
