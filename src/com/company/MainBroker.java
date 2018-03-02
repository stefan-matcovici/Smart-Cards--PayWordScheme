package com.company;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MainBroker {
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Broker broker = new Broker();
        broker.registerUser();
    }
}
