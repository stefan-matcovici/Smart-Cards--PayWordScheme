package com.company.models;


import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
                "currentDigest=" + currentDigests.stream().map(Base64.getEncoder()::encodeToString).collect(Collectors.joining(", ")) +
                ", currentPaymentIndex=" + currentPaymentIndexes.stream().map(String::valueOf).collect(Collectors.joining(", ")) +
                '}';
    }
}
