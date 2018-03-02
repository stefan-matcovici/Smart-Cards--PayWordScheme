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
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.company.utils.CryptoUtils.getDiffieHellmanComputedSecret;

public class Broker {
    private static final int BROKER_SERVER_PORT = 6789;
    private ServerSocket brokerServerSocket;

    public Broker() throws IOException {
        brokerServerSocket = new ServerSocket(BROKER_SERVER_PORT);
    }

    public void registerUser() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Socket userConnectionSocket = brokerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));
        DataOutputStream outToUser = new DataOutputStream(userConnectionSocket.getOutputStream());

        final byte[] secret = getDiffieHellmanComputedSecret(outToUser, inFromUser);

        byte[] encryptedIdentity = Base64.getDecoder().decode(inFromUser.readLine());
        SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decryptedIdentity = cipher.doFinal(encryptedIdentity);

        final Identity identity = new ObjectMapper().readValue(decryptedIdentity, Identity.class);
        PublicKey pk = identity.computePublicKey();

        System.out.println(pk.toString());


        userConnectionSocket.close();
    }
}
