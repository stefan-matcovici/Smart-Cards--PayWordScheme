package com.company.main;

import com.company.Broker;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MainBroker {
    public static void main(String[] args) throws Exception {
        Broker broker = new Broker();
        while (true) {
            broker.registerUser();
        }
    }
}
