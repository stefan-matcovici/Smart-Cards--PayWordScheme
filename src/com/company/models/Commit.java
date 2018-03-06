package com.company.models;

import java.util.Base64;

public class Commit {
    private String sellerIdentityName;
    private SignedCertificate signedCertificateFromBrokerToUser;
    private byte[] hashChainRoot;
    private int numberHashChainElements;

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

    public byte[] getHashChainRoot() {
        return hashChainRoot;
    }

    public void setHashChainRoot(byte[] hashChainRoot) {
        this.hashChainRoot = hashChainRoot;
    }

    public int getNumberHashChainElements() {
        return numberHashChainElements;
    }

    public void setNumberHashChainElements(int numberHashChainElements) {
        this.numberHashChainElements = numberHashChainElements;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "sellerIdentityName='" + sellerIdentityName + '\'' +
                ", signedCertificateFromBrokerToUser=" + signedCertificateFromBrokerToUser +
                ", hashChainRoot=" + Base64.getEncoder().encodeToString(hashChainRoot) +
                ", numberHashChainElements=" + numberHashChainElements +
                '}';
    }
}
