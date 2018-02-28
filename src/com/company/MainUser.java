package com.company;

import java.io.IOException;

public class MainUser {

    public static void main(String[] args) throws IOException {
        User user = new User();
        user.registerToBroker();
    }
}
