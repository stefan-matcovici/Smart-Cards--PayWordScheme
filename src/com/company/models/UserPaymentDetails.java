package com.company.models;

public class UserPaymentDetails {
    private byte[] lastDigest;
    private int paymentIndex;

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
}
