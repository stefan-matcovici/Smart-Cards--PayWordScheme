package com.company.main;

import com.company.utils.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

    public boolean verifySignature() throws Exception {
        return CryptoUtils.verifySignature(new ObjectMapper().writeValueAsBytes(plainCertificate), signature, plainCertificate.getCertifierIdentity().computePublicKey());
    }


}
