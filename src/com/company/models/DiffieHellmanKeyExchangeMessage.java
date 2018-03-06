package com.company.models;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DiffieHellmanKeyExchangeMessage {
    private byte[] publicKeyByteArray;
    private String algorithm;

    public byte[] getPublicKeyByteArray() {
        return publicKeyByteArray;
    }

    public void setPublicKeyByteArray(byte[] publicKeyByteArray) {
        this.publicKeyByteArray = publicKeyByteArray;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public PublicKey computePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyByteArray);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(ks);
    }

    @Override
    public String toString() {
        return "DiffieHellmanKeyExchangeMessage{" +
                "publicKeyByteArray=" + Base64.getEncoder().encodeToString(publicKeyByteArray) +
                ", algorithm='" + algorithm + '\'' +
                '}';
    }
}
