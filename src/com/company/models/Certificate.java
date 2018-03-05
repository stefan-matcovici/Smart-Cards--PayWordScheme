package com.company.models;

public class Certificate {
    private Identity certifiedIdentity;
    private Identity certifierIdentity;

    public Identity getCertifiedIdentity() {
        return certifiedIdentity;
    }

    public void setCertifiedIdentity(Identity certifiedIdentity) {
        this.certifiedIdentity = certifiedIdentity;
    }

    public Identity getCertifierIdentity() {
        return certifierIdentity;
    }

    public void setCertifierIdentity(Identity certifierIdentity) {
        this.certifierIdentity = certifierIdentity;
    }
}
