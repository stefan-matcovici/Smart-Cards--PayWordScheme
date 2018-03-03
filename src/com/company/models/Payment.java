package com.company.models;

import java.util.Arrays;

public class Payment {
    private byte[] currentDigest;
    private int currentPaymentIndex;

    public byte[] getCurrentDigest() {
        return currentDigest;
    }

    public void setCurrentDigest(byte[] currentDigest) {
        this.currentDigest = currentDigest;
    }

    public int getCurrentPaymentIndex() {
        return currentPaymentIndex;
    }

    public void setCurrentPaymentIndex(int currentPaymentIndex) {
        this.currentPaymentIndex = currentPaymentIndex;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currentDigest=" + Arrays.toString(currentDigest) +
                ", currentPaymentIndex=" + currentPaymentIndex +
                '}';
    }
}
