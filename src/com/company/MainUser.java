package com.company;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainUser {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        User user = new User();
        user.registerToBroker();
    }
}
