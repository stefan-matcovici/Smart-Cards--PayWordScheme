package com.company;

import com.company.models.Certificate;
import com.company.models.Payment;
import com.company.models.PaymentWithDifferentValues;
import com.company.models.SignedCertificate;
import com.company.models.Identity;
import com.company.models.UserPaymentDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.utils.CryptoUtils.*;

public class Broker {
    private static final int BROKER_SERVER_PORT = 6789;
    private static final int SELLER_BROKER_SERVER_PORT = 6790;
    private ServerSocket brokerServerSocket;
    private ServerSocket sellerBrokerServerSocket;
    private Identity ownIdentity;
    private PrivateKey privateKey;
    private ObjectMapper objectMapper;
    private Map<String, Map<String, UserPaymentDetails>> sellersPayments = new HashMap<>();

    public Broker() throws IOException, NoSuchAlgorithmException {
        brokerServerSocket = new ServerSocket(BROKER_SERVER_PORT);
        sellerBrokerServerSocket = new ServerSocket(SELLER_BROKER_SERVER_PORT);

        objectMapper = new ObjectMapper();

        buildOwnIdentity();
    }

    public void registerUser() throws Exception {
        Socket userConnectionSocket = brokerServerSocket.accept();

        System.out.println("Started to register a new user...");

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(userConnectionSocket.getInputStream()));
        DataOutputStream outToUser = new DataOutputStream(userConnectionSocket.getOutputStream());

        System.out.println("Starting to compute the Diffie Hellman secret...");

        byte[] secret = getDiffieHellmanComputedSecret(outToUser, inFromUser);

        System.out.printf("Computed the Diffie Hellman secret <%s>.\n\n", Base64.getEncoder().encodeToString(secret));

        System.out.println("Receiving the encrypted identity from the user...");

        Identity identity = receiveUserIdentity(inFromUser, secret);

        System.out.printf("Received the identity <%s> from user.", identity);

        SignedCertificate signedCertificate = createSignedCertificate(identity);
        System.out.printf("Created signed certificate <%s> from identity <%s>.\n\n", signedCertificate, identity);

        System.out.println("Sending certificate to user...");
        outToUser.writeBytes(objectMapper.writeValueAsString(signedCertificate) + "\n");

        userConnectionSocket.close();
    }

    public void processCommitsFromSellers() throws IOException, NoSuchAlgorithmException {
        Socket sellerConnectionSocket = sellerBrokerServerSocket.accept();

        BufferedReader inFromSeller =
                new BufferedReader(new InputStreamReader(sellerConnectionSocket.getInputStream()));

        System.out.println("Processing commit from a seller...");

        Map<String, UserPaymentDetails> userPaymentDetailsMap = new HashMap<>();
        String content = inFromSeller.readLine();
        String sellerIdentity = null;
        while (content != null) {
            UserPaymentDetails readUserPaymentDetails = objectMapper.readValue(content, UserPaymentDetails.class);

            System.out.printf("Got the following user payment: %s\n\n", readUserPaymentDetails.toString());

            System.out.println("Verifying that the seller payment is valid...");

            if (isValidPayment(readUserPaymentDetails)) {
                String userIdentity = readUserPaymentDetails.getCommit().getSignedCertificateFromBrokerToUser().getPlainCertificate().getCertifiedIdentity().getIdentity();
                userPaymentDetailsMap.put(userIdentity, readUserPaymentDetails);
                sellerIdentity = readUserPaymentDetails.getCommit().getSellerIdentityName();
            } else {
                System.out.printf("The <%s> payment is not valid.\n", userPaymentDetailsMap);
            }

            content = inFromSeller.readLine();
        }

        sellersPayments.put(sellerIdentity, userPaymentDetailsMap);

        System.out.printf("Processed the commits from user.\n The current seller payments mapping is <%s>\n\n", sellersPayments);
    }

    private boolean isValidPayment(UserPaymentDetails userPaymentDetails) throws NoSuchAlgorithmException {
        PaymentWithDifferentValues newPaymentWithDifferentValues = userPaymentDetails.getPayments();
        sellersPayments.computeIfAbsent(userPaymentDetails.getCommit().getSellerIdentityName(), k -> new HashMap<>());
        sellersPayments.get(userPaymentDetails.getCommit().getSellerIdentityName())
                .putIfAbsent(userPaymentDetails
                                .getCommit()
                                .getSignedCertificateFromBrokerToUser()
                                .getPlainCertificate()
                                .getCertifiedIdentity()
                                .getIdentity()
                        , userPaymentDetails);

        PaymentWithDifferentValues currentPaymentWithDifferentValues = sellersPayments.get(userPaymentDetails.getCommit().getSellerIdentityName())
                .get(userPaymentDetails
                        .getCommit()
                        .getSignedCertificateFromBrokerToUser()
                        .getPlainCertificate()
                        .getCertifiedIdentity()
                        .getIdentity()
                ).getPayments();

        for (int i = 0; i < newPaymentWithDifferentValues.getPaymentsWithDifferentValues().size(); i++) {
            byte[] currentHash = currentPaymentWithDifferentValues.getPaymentsWithDifferentValues().get(i).getCurrentDigest();
            int amount = newPaymentWithDifferentValues.getPaymentsWithDifferentValues().get(i).getCurrentPaymentIndex() -
                    currentPaymentWithDifferentValues.getPaymentsWithDifferentValues().get(i).getCurrentPaymentIndex();

            if (amount < 0) {
                throw new RuntimeException("The seller cannot send payments with negative amounts.");
            }

            for (int j = 0; j < amount; j++) {
                currentHash = getMessageDigest().digest(currentHash);
            }

            if (!Arrays.equals(currentHash, newPaymentWithDifferentValues.getPaymentsWithDifferentValues().get(i).getCurrentDigest())) {
                return false;
            }
        }

        return true;
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

    private Identity receiveUserIdentity(BufferedReader inFromUser, byte[] secret) throws Exception {
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
