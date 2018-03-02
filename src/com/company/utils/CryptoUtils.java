package com.company.utils;

import com.company.models.DiffieHellmanKeyExchangeMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoUtils {
    private static BigInteger g512 = new BigInteger("1234567890", 16);
    private static BigInteger p512 = new BigInteger("1234567890", 16);
    public static DHParameterSpec dhParams = new DHParameterSpec(p512, g512);

    public static KeyPairGenerator getKeyGen() throws NoSuchAlgorithmException {
        final KeyPairGenerator dh = KeyPairGenerator.getInstance("DH");
        dh.initialize(2048);

        return dh;
    }

    public static byte[] getDiffieHellmanComputedSecret(DataOutputStream outToBroker, BufferedReader inFromBroker) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, InvalidKeySpecException {
        KeyAgreement userKeyAgree = KeyAgreement.getInstance("DH");
        KeyPair userKeyPair = getKeyGen().generateKeyPair();

        userKeyAgree.init(userKeyPair.getPrivate());

        DiffieHellmanKeyExchangeMessage sentDiffieHellmanUserPublicKey = new DiffieHellmanKeyExchangeMessage();
        sentDiffieHellmanUserPublicKey.setAlgorithm(userKeyPair.getPublic().getAlgorithm());
        sentDiffieHellmanUserPublicKey.setPublicKeyByteArray(userKeyPair.getPublic().getEncoded());

        outToBroker.writeBytes(new ObjectMapper().writeValueAsString(sentDiffieHellmanUserPublicKey) + "\n");

        final DiffieHellmanKeyExchangeMessage recievedDiffieHellmanBrokerPublicKey =
                new ObjectMapper().readValue(inFromBroker.readLine(), DiffieHellmanKeyExchangeMessage.class);

        userKeyAgree.doPhase(recievedDiffieHellmanBrokerPublicKey.computePublicKey(), true);

        return MessageDigest.getInstance("SHA-256").digest(userKeyAgree.generateSecret());
    }
}
