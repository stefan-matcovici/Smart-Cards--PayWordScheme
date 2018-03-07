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

    public Socket commitToSeller(int sellerPort, List<Integer> hashChainValues, List<Integer> hashChainSizes) throws Exception {
        Socket userSocketToSeller = new Socket("localhost", sellerPort);

        DataOutputStream outToSeller = new DataOutputStream(userSocketToSeller.getOutputStream());

        List<HashChainCommit> hashChainCommitList = new ArrayList<>();
        List<HashChain> hashChainList = new ArrayList<>();
        for (int i = 0; i < hashChainValues.size(); i++) {
            HashChain hashChain = new HashChain(hashChainSizes.get(i), hashChainValues.get(i));
            hashChainList.add(hashChain);

            HashChainCommit hashChainCommit = new HashChainCommit();
            hashChainCommit.setValue(hashChain.getValue());
            hashChainCommit.setHashChainRoot(hashChain.getHashChainRoot());
            hashChainCommit.setNumberHashChainElements(hashChain.getHashChainSize());

            hashChainCommitList.add(hashChainCommit);
        }


        Commit commit = new Commit();
        commit.setSellerIdentityName(SELLER_IDENTITY);
        commit.setSignedCertificateFromBrokerToUser(signedCertificateFromBroker);
        commit.setHashChainCommits(hashChainCommitList);

        sellerToHashChain.put(userSocketToSeller, hashChainList);

        SignedCommit signedCommit = new SignedCommit();
        signedCommit.setPlainCommit(commit);
        signedCommit.setSignature(sign(objectMapper.writeValueAsBytes(commit), privateKey));

        outToSeller.writeBytes(objectMapper.writeValueAsString(signedCommit) + "\n");
        outToSeller.flush();

        return userSocketToSeller;
    }

    private List<Payment> getPayments(List<HashChain> hashChains, int amount) throws Exception {
        List<Payment> payments = new ArrayList<>();
        for (HashChain hashChain: hashChains) {
            Payment payment = new Payment();
            payment.setPaymentValue(hashChain.getValue());

            int value = hashChain.getValue();

            if (amount >= value) {
                payment.setCurrentDigest(hashChain.computeNextHash(amount / value));
                amount -= ((amount / value) * value);
                amount = amount % value;
            }

            payment.setCurrentDigest(hashChain.computeNextHash(0));
            payment.setCurrentPaymentIndex(hashChain.getCurrentHashIndex());

            payments.add(payment);
        }

        return payments;
    }

    public void payToSeller(Socket sellerSocket, int amount) throws Exception {
        List<HashChain> hashChainList = sellerToHashChain.get(sellerSocket);
        System.out.printf("Starting to pay <%d> to the seller associated with the port <%s>...\n\n", amount, sellerSocket);

        System.out.printf("Current seller hash chain: <%s>\n\n", hashChainList.stream().map(HashChain::toString).collect(Collectors.joining(", ")));

        PaymentWithDifferentValues paymentWithDifferentValues = new PaymentWithDifferentValues();
        paymentWithDifferentValues.setPaymentsWithDifferentValues(getPayments(hashChainList, amount));

        System.out.printf("Sending payment <%s> to seller...\n", paymentWithDifferentValues);

        DataOutputStream outToSeller = new DataOutputStream(sellerSocket.getOutputStream());
        outToSeller.writeBytes(objectMapper.writeValueAsString(paymentWithDifferentValues) + "\n");
        outToSeller.flush();

        System.out.printf("Sent the payment <%s> to seller.\n", paymentWithDifferentValues);
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
