package com.company.models;

import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;

public class Commit {
    private String sellerIdentityName;
    private SignedCertificate signedCertificateFromBrokerToUser;
    private ArrayList<byte[]> hashChainsRoots;
    private int numberHashChainElements;
    private int[] hashChainsValues;

    public String getSellerIdentityName() {
        return sellerIdentityName;
    }

    public void setSellerIdentityName(String sellerIdentityName) {
        this.sellerIdentityName = sellerIdentityName;
    }

    public SignedCertificate getSignedCertificateFromBrokerToUser() {
        return signedCertificateFromBrokerToUser;
    }

    public void setSignedCertificateFromBrokerToUser(SignedCertificate signedCertificateFromBrokerToUser) {
        this.signedCertificateFromBrokerToUser = signedCertificateFromBrokerToUser;
    }

    public ArrayList<byte[]> getHashChainsRoots() {
        return hashChainsRoots;
    }

    public void setHashChainsRoots(ArrayList<byte[]> hashChainsRoots) {
        this.hashChainsRoots = hashChainsRoots;
    }

    public int getNumberHashChainElements() {
        return numberHashChainElements;
    }

    public void setNumberHashChainElements(int numberHashChainElements) {
        this.numberHashChainElements = numberHashChainElements;
    }

    public int[] getHashChainsValues() {
        return hashChainsValues;
    }

    public void setHashChainsValues(int[] hashChainsValues) {
        this.hashChainsValues = hashChainsValues;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "sellerIdentityName='" + sellerIdentityName + '\'' +
                ", signedCertificateFromBrokerToUser=" + signedCertificateFromBrokerToUser +
                ", hashChainRoot=" + hashChainsRoots.stream().map(Base64.getEncoder()::encodeToString).collect(Collectors.joining(", ")) +
                ", numberHashChainElements=" + numberHashChainElements +
                '}';
    }
}
