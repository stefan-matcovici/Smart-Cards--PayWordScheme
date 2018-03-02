package com.company.models;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class Identity {

    private String identity;
    private byte[] publicKeyByteArray;
    private String algorithm;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setPublicKeyByteArray(byte[] publicKeyByteArray) {
        this.publicKeyByteArray = Arrays.copyOf(publicKeyByteArray, publicKeyByteArray.length);
    }

    public byte[] getPublicKeyByteArray() {
        return publicKeyByteArray;
    }

    public PublicKey computePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyByteArray);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(ks);
    }



}
