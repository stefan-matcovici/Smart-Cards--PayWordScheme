package com.company.models;

import java.util.Arrays;
import java.util.Base64;

import static com.company.utils.CryptoUtils.getMessageDigest;

public class UserPaymentDetails {
    private byte[] lastDigest;
    private int paymentIndex;
    private Commit commit;

    public void processPayment(Payment payment) throws Exception {
        int amount = payment.getCurrentPaymentIndex() - paymentIndex;

        byte[] currentHash = lastDigest;
        for (int i = 0; i < amount; i++) {
            currentHash = getMessageDigest().digest(currentHash);
        }

        if (Arrays.equals(currentHash, payment.getCurrentDigest())) {
            paymentIndex += amount;
            lastDigest = Arrays.copyOf(currentHash, currentHash.length);
        }
        else {
            throw new Exception("Invalid payment! Hashes don't match!");
        }
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public byte[] getLastDigest() {
        return lastDigest;
    }

    public void setLastDigest(byte[] lastDigest) {
        this.lastDigest = lastDigest;
    }

    public int getPaymentIndex() {
        return paymentIndex;
    }

    public void setPaymentIndex(int paymentIndex) {
        this.paymentIndex = paymentIndex;
    }

    public String computeUserIdentity() {
        return getCommit().getSignedCertificateFromBrokerToUser().getPlainCertificate().getCertifiedIdentity().getIdentity();
    }
    @Override
    public String toString() {
        return "UserPaymentDetails{" +
                "lastDigest=" + Base64.getEncoder().encodeToString(lastDigest) +
                ", paymentIndex=" + paymentIndex +
                ", commit=" + commit +
                '}';
    }
}
