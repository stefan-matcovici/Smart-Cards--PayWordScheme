package com.company.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UserPaymentDetails {
    private byte[] lastDigest;
    private int paymentIndex;
    private Commit commit;

    private MessageDigest messageDigest;

    public UserPaymentDetails() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("SHA-256");
    }

    public void processPayment(Payment payment) throws Exception {
        int amount = payment.getCurrentPaymentIndex() - paymentIndex;

        System.out.println(amount);

        byte[] currentHash = lastDigest;
        for (int i=0; i<amount; i++) {
            currentHash = messageDigest.digest(currentHash);
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

    @Override
    public String toString() {
        return "UserPaymentDetails{" +
                "lastDigest=" + Arrays.toString(lastDigest) +
                ", paymentIndex=" + paymentIndex +
                ", commit=" + commit +
                ", messageDigest=" + messageDigest +
                '}';
    }
}
