package com.company.models;

import java.util.Base64;

public class Payment {
    private byte[] currentDigest;
    private Integer currentPaymentIndex;
    private int paymentValue;

    public byte[] getCurrentDigest() {
        return currentDigest;
    }

    public void setCurrentDigest(byte[] currentDigest) {
        this.currentDigest = currentDigest;
    }

    public Integer getCurrentPaymentIndex() {
        return currentPaymentIndex;
    }

    public void setCurrentPaymentIndex(Integer currentPaymentIndex) {
        this.currentPaymentIndex = currentPaymentIndex;
    }

    public int getPaymentValue() {
        return paymentValue;
    }

    public void setPaymentValue(int paymentValue) {
        this.paymentValue = paymentValue;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currentDigest=" + Base64.getEncoder().encodeToString(currentDigest) +
                ", currentPaymentIndex=" + currentPaymentIndex +
                ", paymentValue=" + paymentValue +
                '}';
    }
}
