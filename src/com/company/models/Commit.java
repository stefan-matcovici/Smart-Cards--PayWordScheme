package com.company.models;

import java.util.List;

public class Commit {
    private String sellerIdentityName;
    private SignedCertificate signedCertificateFromBrokerToUser;
    private List<HashChainCommit> hashChainCommits;

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

    public List<HashChainCommit> getHashChainCommits() {
        return hashChainCommits;
    }

    public void setHashChainCommits(List<HashChainCommit> hashChainCommits) {
        this.hashChainCommits = hashChainCommits;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "sellerIdentityName='" + sellerIdentityName + '\'' +
                ", signedCertificateFromBrokerToUser=" + signedCertificateFromBrokerToUser +
                ", hashChainCommits=" + hashChainCommits +
                '}';
    }
}
