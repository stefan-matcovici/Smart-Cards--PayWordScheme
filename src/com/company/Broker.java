package com.company;

import com.company.models.Certificate;
import com.company.models.SignedCertificate;
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
import java.security.*;
import java.util.Base64;

import static com.company.utils.CryptoUtils.*;

public class Broker {
    private static final int BROKER_SERVER_PORT = 6789;
    private ServerSocket brokerServerSocket;
    private Identity ownIdentity;
    private PrivateKey privateKey;
    private ObjectMapper objectMapper;

    public Broker() throws IOException, NoSuchAlgorithmException {
        brokerServerSocket = new ServerSocket(BROKER_SERVER_PORT);
        objectMapper = new ObjectMapper();

        buildOwnIdentity();
    }

    public void registerUser() throws Exception {
        Socket userConnectionSocket = brokerServerSocket.accept();

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));
        DataOutputStream outToUser = new DataOutputStream(userConnectionSocket.getOutputStream());

        byte[] secret = getDiffieHellmanComputedSecret(outToUser, inFromUser);

        Identity identity = receiveUserIdentity(inFromUser, secret);

        SignedCertificate signedCertificate = createSignedCertificate(identity);
        outToUser.writeBytes(objectMapper.writeValueAsString(signedCertificate) + "\n");

        userConnectionSocket.close();
    }

    private void buildOwnIdentity() throws NoSuchAlgorithmException {
        ownIdentity = new Identity();
        ownIdentity.setIdentity("Broker");
        KeyPair keyPair = buildKeyPair();
        final PublicKey publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        ownIdentity.setAlgorithm(publicKey.getAlgorithm());
        ownIdentity.setPublicKeyByteArray(publicKey.getEncoded());
    }

    private Identity receiveUserIdentity(BufferedReader inFromUser, byte[] secret) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedIdentity = Base64.getDecoder().decode(inFromUser.readLine());
        SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] decryptedIdentity = cipher.doFinal(encryptedIdentity);

        return new ObjectMapper().readValue(decryptedIdentity, Identity.class);
    }

    private SignedCertificate createSignedCertificate(Identity identity) throws Exception {
        Certificate certificate = new Certificate();
        certificate.setCertifiedIdentity(identity);
        certificate.setCertifierIdentity(ownIdentity);

        final byte[] signature = sign(objectMapper.writeValueAsBytes(certificate), privateKey);

        SignedCertificate signedCertificate = new SignedCertificate();
        signedCertificate.setPlainCertificate(certificate);
        signedCertificate.setSignature(signature);
        return signedCertificate;
    }
}
