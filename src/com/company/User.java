package com.company;

import com.company.models.Identity;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.company.utils.CryptoUtils.getDiffieHellmanComputedSecret;

public class User {
    private static final int BROKER_SERVER_PORT = 6789;
    private PrivateKey privateKey;

    public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);

        return keyPairGenerator.genKeyPair();
    }

    public void registerToBroker() throws IOException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Socket userSocket = new Socket("localhost", BROKER_SERVER_PORT);

        DataOutputStream outToBroker = new DataOutputStream(userSocket.getOutputStream());
        BufferedReader inFromBroker = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

        final byte[] commonKey = getDiffieHellmanComputedSecret(outToBroker, inFromBroker);

        KeyPair keyPair = buildKeyPair();
        byte[] pubKey = keyPair.getPublic().getEncoded();
        privateKey = keyPair.getPrivate();

        System.out.println(keyPair.getPublic().toString());

        Identity identity = new Identity();
        identity.setIdentity("User");
        identity.setAlgorithm("RSA");
        identity.setPublicKeyByteArray(pubKey);

        String identityString = new ObjectMapper().writeValueAsString(identity);

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(commonKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] encryptedIdentityString = cipher.doFinal(identityString.getBytes());

        outToBroker.writeBytes(Base64.getEncoder().encodeToString(encryptedIdentityString) +"\n");

        userSocket.close();
    }
}
