package com.company.models;

import java.util.List;

public class Payment {
    private List<byte[]> currentDigests;
    private List<Integer> currentPaymentIndexes;

    public List<byte[]> getCurrentDigests() {
        return currentDigests;
    }

    public void setCurrentDigests(List<byte[]> currentDigests) {
        this.currentDigests = currentDigests;
    }

    public void setCurrentPaymentIndexes(List<Integer> currentPaymentIndexes) {
        this.currentPaymentIndexes = currentPaymentIndexes;
    }

    public List<Integer> getCurrentPaymentIndexes() {
        return currentPaymentIndexes;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "currentDigest=" + currentDigests +
                ", currentPaymentIndex=" + currentPaymentIndexes +
                '}';
    }
}
