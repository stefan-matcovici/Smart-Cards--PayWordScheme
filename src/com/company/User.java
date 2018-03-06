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
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.utils.CryptoUtils.buildKeyPair;
import static com.company.utils.CryptoUtils.getDiffieHellmanComputedSecret;
import static com.company.utils.CryptoUtils.sign;

public class User {
    private static final int HASH_CHAIN_MAX_SIZE = 100;
    private static final int[] HASH_CHAINS_SIZES = {9, 6, 5, 1};
    private static final String SELLER_IDENTITY = "SellerIdentity";
    private PrivateKey privateKey;
    private SignedCertificate signedCertificateFromBroker;
    private ObjectMapper objectMapper;
    private Map<Socket, List<HashChain>> sellerToHashChain = new HashMap<>();

    public User() {
        objectMapper = new ObjectMapper();
    }

    public void registerToBroker(int brokerPort) throws Exception {
        System.out.println("Starting to register to broker...");

        Socket userSocketToBroker = new Socket("localhost", brokerPort);

        DataOutputStream outToBroker = new DataOutputStream(userSocketToBroker.getOutputStream());
        BufferedReader inFromBroker = new BufferedReader(new InputStreamReader(userSocketToBroker.getInputStream()));

        System.out.println("Starting to compute the Diffie Hellman secret...");

        final byte[] commonKey = getDiffieHellmanComputedSecret(outToBroker, inFromBroker);

        System.out.printf("Computed the Diffie Hellman secret <%s>.\n\n", Base64.getEncoder().encodeToString(commonKey));

        System.out.println("Sending my identity encrypted to the broker...");

        sendEncryptedIdentityToBroker(outToBroker, commonKey);

        receiveSignedCertificateFromBroker(inFromBroker);

        System.out.printf("Received the following certificate from the broker <%s>.\n\n", signedCertificateFromBroker);

        userSocketToBroker.close();
    }

    public Socket commitToSeller(int sellerPort) throws Exception {
        Socket userSocketToSeller = new Socket("localhost", sellerPort);

        DataOutputStream outToSeller = new DataOutputStream(userSocketToSeller.getOutputStream());

        List<HashChain> hashChainList = new ArrayList<>();
        for (int i : HASH_CHAINS_SIZES) {
            hashChainList.add(new HashChain(HASH_CHAIN_MAX_SIZE));
        }

        Commit commit = new Commit();
        commit.setHashChainsRoots(hashChainList.stream().map(HashChain::getHashChainRoot).collect(Collectors.toCollection(ArrayList::new)));
        commit.setNumberHashChainElements(HASH_CHAIN_MAX_SIZE);
        commit.setSellerIdentityName(SELLER_IDENTITY);
        commit.setSignedCertificateFromBrokerToUser(signedCertificateFromBroker);
        commit.setHashChainsValues(HASH_CHAINS_SIZES);

        sellerToHashChain.put(userSocketToSeller, hashChainList);

        SignedCommit signedCommit = new SignedCommit();
        signedCommit.setPlainCommit(commit);
        signedCommit.setSignature(sign(objectMapper.writeValueAsBytes(commit), privateKey));

        outToSeller.writeBytes(objectMapper.writeValueAsString(signedCommit) + "\n");
        outToSeller.flush();

        return userSocketToSeller;
    }

    private int[] getCoins(int coinValues[], int amount) {
        int amounts[] = new int[coinValues.length];
        int index = 0;
        for (int value : coinValues) {
            if (amount >= value) {
                amounts[index] = amount / value;
                amount -= ((amount / value) * value);
                amount = amount % value;
            }
            index++;
        }

        return amounts;
    }

    public void payToSeller(Socket sellerSocket, int amount) throws Exception {
        List<HashChain> hashChainList = sellerToHashChain.get(sellerSocket);
        System.out.printf("Starting to pay <%d> to the seller associated with the port <%s>...\n\n", amount, sellerSocket);

        System.out.printf("Current seller hash chain: <%s>\n\n", hashChainList.stream().map(HashChain::toString).collect(Collectors.joining(", ")));

        int[] values = getCoins(HASH_CHAINS_SIZES, amount);
        List<byte[]> currentDigests = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            currentDigests.add(hashChainList.get(i).computeNextHash(values[i]));
        }

//        System.out.printf("Computed the hash that needs to be sent to the seller: <%s> and update the hash chain <%s> \n\n", Base64.getEncoder().encodeToString(currentDigest), hashChain);

        Payment payment = new Payment();
        payment.setCurrentDigests(currentDigests);
        payment.setCurrentPaymentIndexes(hashChainList.stream().map(HashChain::getCurrentHashIndex).collect(Collectors.toList()));

        System.out.printf("Sending payment <%s> to seller...\n", payment);

        DataOutputStream outToSeller = new DataOutputStream(sellerSocket.getOutputStream());
        outToSeller.writeBytes(objectMapper.writeValueAsString(payment) + "\n");
        outToSeller.flush();

        System.out.printf("Sent the payment <%s> to seller.\n", payment);
    }

    private void sendEncryptedIdentityToBroker(DataOutputStream outToBroker, byte[] commonKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        KeyPair keyPair = buildKeyPair();
        byte[] pubKey = keyPair.getPublic().getEncoded();
        privateKey = keyPair.getPrivate();

        byte[] encryptedIdentityString = getEncryptedIdentity(commonKey, pubKey);
        outToBroker.writeBytes(Base64.getEncoder().encodeToString(encryptedIdentityString) + "\n");
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
