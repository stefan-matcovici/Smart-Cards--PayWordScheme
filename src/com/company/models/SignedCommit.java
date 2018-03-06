package com.company.models;

import com.company.utils.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class SignedCommit {
    private Commit plainCommit;
    private byte[] signature;

    public Commit getPlainCommit() {
        return plainCommit;
    }

    public void setPlainCommit(Commit plainCommit) {
        this.plainCommit = plainCommit;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public void verifySignature() throws Exception {
        final PublicKey userPublicKey = plainCommit.getSignedCertificateFromBrokerToUser()
                .getPlainCertificate()
                .getCertifiedIdentity()
                .computePublicKey();

        CryptoUtils.verifySignature(new ObjectMapper().writeValueAsBytes(plainCommit), signature, userPublicKey);
    }

    @Override
    public String toString() {
        return "SignedCommit{" +
                "plainCommit=" + Base64.getEncoder().encodeToString(signature) +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
