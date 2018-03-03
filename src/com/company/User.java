package com.company;

import com.company.models.*;
import com.company.models.Identity;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.*;

import static com.company.utils.CryptoUtils.buildKeyPair;
import static com.company.utils.CryptoUtils.getDiffieHellmanComputedSecret;
import static com.company.utils.CryptoUtils.sign;

public class User {
    private static final int HASH_CHAIN_MAX_SIZE = 100;
    private static final String SELLER_IDENTITY = "SellerIdentity";
    private PrivateKey privateKey;
    private SignedCertificate signedCertificateFromBroker;
    private ObjectMapper objectMapper;
    private Map<Socket, HashChain> sellerToHashChain = new HashMap<>();

    public User() {
        objectMapper = new ObjectMapper();
    }

    public void registerToBroker(int brokerPort) throws Exception {
        Socket userSocketToBroker = new Socket("localhost", brokerPort);

        DataOutputStream outToBroker = new DataOutputStream(userSocketToBroker.getOutputStream());
        BufferedReader inFromBroker = new BufferedReader(new InputStreamReader(userSocketToBroker.getInputStream()));

        final byte[] commonKey = getDiffieHellmanComputedSecret(outToBroker, inFromBroker);

        sendEncryptedIdentityToBroker(outToBroker, commonKey);

        receiveSignedCertificateFromBroker(inFromBroker);

        userSocketToBroker.close();
    }

    public Socket commitToSeller(int sellerPort) throws Exception {
        Socket userSocketToSeller = new Socket("localhost", sellerPort);

        DataOutputStream outToSeller = new DataOutputStream(userSocketToSeller.getOutputStream());

        HashChain hashChain = new HashChain(HASH_CHAIN_MAX_SIZE);

        sellerToHashChain.put(userSocketToSeller, hashChain);

        Commit commit = new Commit();
        commit.setHashChainRoot(hashChain.getHashChainRoot());
        commit.setNumberHashChainElements(HASH_CHAIN_MAX_SIZE);
        commit.setSellerIdentityName(SELLER_IDENTITY);
        commit.setSignedCertificateFromBrokerToUser(signedCertificateFromBroker);

        SignedCommit signedCommit = new SignedCommit();
        signedCommit.setPlainCommit(commit);
        signedCommit.setSignature(sign(objectMapper.writeValueAsBytes(commit), privateKey));

        outToSeller.writeBytes(objectMapper.writeValueAsString(signedCommit) + "\n");

        return userSocketToSeller;
    }

    public void payToSeller(Socket sellerSocket, int amount) throws Exception {
        HashChain hashChain = sellerToHashChain.get(sellerSocket);

        Payment payment = new Payment();
        payment.setCurrentDigest(hashChain.computeNextHash(amount));
        payment.setCurrentPaymentIndex(hashChain.getCurrentHashIndex());

        DataOutputStream outToSeller = new DataOutputStream(sellerSocket.getOutputStream());

        outToSeller.writeBytes(objectMapper.writeValueAsString(payment) + "\n");
    }

    private void sendEncryptedIdentityToBroker(DataOutputStream outToBroker, byte[] commonKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        KeyPair keyPair = buildKeyPair();
        byte[] pubKey = keyPair.getPublic().getEncoded();
        privateKey = keyPair.getPrivate();

        byte[] encryptedIdentityString = getEncryptedIdentity(commonKey, pubKey);
        outToBroker.writeBytes(Base64.getEncoder().encodeToString(encryptedIdentityString) +"\n");
    }

    private void receiveSignedCertificateFromBroker(BufferedReader inFromBroker) throws Exception {
        SignedCertificate signedCertificate = objectMapper.readValue(inFromBroker.readLine(), SignedCertificate.class);
        signedCertificate.verifySignature();
        this.signedCertificateFromBroker = signedCertificate;
    }

    private byte[] getEncryptedIdentity(byte[] commonKey, byte[] pubKey) throws JsonProcessingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Identity identity = new Identity();
        identity.setIdentity("User");
        identity.setAlgorithm("RSA");
        identity.setPublicKeyByteArray(pubKey);

        String identityString = objectMapper.writeValueAsString(identity);

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(commonKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        return cipher.doFinal(identityString.getBytes());
    }
}
