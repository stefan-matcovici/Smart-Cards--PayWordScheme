package com.company.models;

import com.company.utils.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Base64;

public class SignedCertificate {

    private Certificate plainCertificate;
    private byte[] signature;

    public Certificate getPlainCertificate() {
        return plainCertificate;
    }

    public void setPlainCertificate(Certificate plainCertificate) {
        this.plainCertificate = plainCertificate;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public void verifySignature() throws Exception {
        CryptoUtils.verifySignature(new ObjectMapper().writeValueAsBytes(plainCertificate), signature, plainCertificate.getCertifierIdentity().computePublicKey());
    }

    @Override
    public String toString() {
        return "SignedCertificate{" +
                "plainCertificate=" + plainCertificate +
                ", signature=" + Base64.getEncoder().encodeToString(signature) +
                '}';
    }
}
