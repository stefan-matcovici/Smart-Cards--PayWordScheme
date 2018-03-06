package com.company.models;

import java.util.ArrayList;
import java.util.Arrays;

import static com.company.utils.CryptoUtils.getMessageDigest;

public class UserPaymentDetails {
    private ArrayList<byte[]> lastDigests;
    private ArrayList<Integer> paymentIndexes;
    private Commit commit;

    public void processPayment(Payment payment) throws Exception {

        int size = payment.getCurrentDigests().size();
        for (int i = 0; i < size; i++) {
            int paymentIndex = paymentIndexes.get(i);
            int amount = payment.getCurrentPaymentIndexes().get(i) - paymentIndex;

            System.out.println(amount);

            byte[] currentHash = lastDigests.get(i);

            for (int j = 0; j < amount; j++) {
                currentHash = getMessageDigest().digest(currentHash);
            }

            if (Arrays.equals(currentHash, payment.getCurrentDigests().get(i))) {
                paymentIndex += amount;
                byte[] lastDigest = Arrays.copyOf(currentHash, currentHash.length);

                lastDigests.set(i, lastDigest);
                paymentIndexes.set(i, paymentIndex);

            } else {
                throw new Exception("Invalid payment! Hashes don't match!");
            }

        }
    }

    public String computeUserIdentity() {
        return getCommit().getSignedCertificateFromBrokerToUser().getPlainCertificate().getCertifiedIdentity().getIdentity();
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public ArrayList<byte[]> getLastDigests() {
        return lastDigests;
    }

    public void setLastDigests(ArrayList<byte[]> lastDigests) {
        this.lastDigests = lastDigests;
    }

    public ArrayList<Integer> getPaymentIndexes() {
        return paymentIndexes;
    }

    public void setPaymentIndexes(ArrayList<Integer> paymentIndexes) {
        this.paymentIndexes = paymentIndexes;
    }

    @Override
    public String toString() {
        return "UserPaymentDetails{" +
                "lastDigest=" + lastDigests +
                ", paymentIndex=" + paymentIndexes +
                ", commit=" + commit +
                '}';
    }
}
