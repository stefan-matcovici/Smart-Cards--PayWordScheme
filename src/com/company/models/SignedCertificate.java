package com.company.models;

import com.company.utils.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
