package com.company;

import com.company.models.DiffieHellmanKeyExchangeMessage;
import com.company.models.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static com.company.CryptoUtils.dhParams;
import static com.company.CryptoUtils.getDiffieHellmanComputedSecret;
import static com.company.CryptoUtils.getKeyGen;

public class User {
    private static final int BROKER_SERVER_PORT = 6789;
    private PrivateKey privateKey;

    public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);

        return keyPairGenerator.genKeyPair();
    }

    public void registerToBroker() throws IOException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {
        Socket userSocket = new Socket("localhost", BROKER_SERVER_PORT);

        DataOutputStream outToBroker = new DataOutputStream(userSocket.getOutputStream());
        BufferedReader inFromBroker = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

        final String secret = getDiffieHellmanComputedSecret(outToBroker, inFromBroker);

        System.out.println(secret);

        KeyPair keyPair = buildKeyPair();
        byte[] pubKey = keyPair.getPublic().getEncoded();
        privateKey = keyPair.getPrivate();

        System.out.println(keyPair.getPublic().toString());

        Identity identity = new Identity();
        identity.setIdentity("User");
        identity.setAlgorithm("RSA");
        identity.setPublicKeyByteArray(pubKey);

        String identityString = new ObjectMapper().writeValueAsString(identity);

        outToBroker.writeBytes(identityString +"\n");

        userSocket.close();
    }
}
