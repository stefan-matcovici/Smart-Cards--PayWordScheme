package com.company.models;


import java.util.Arrays;
import java.util.List;

import static com.company.utils.CryptoUtils.getMessageDigest;

public class UserPaymentDetails {
    private PaymentWithDifferentValues payments;
    private Commit commit;

    public void processPaymentWithDifferentValues(PaymentWithDifferentValues paymentWithDifferentValues) throws Exception {
        List<Payment> currentPayments = payments.getPaymentsWithDifferentValues();
        List<Payment> receivedPayments = paymentWithDifferentValues.getPaymentsWithDifferentValues();

        for (int i = 0; i< currentPayments.size(); i++) {
            int paymentIndex = currentPayments.get(i).getCurrentPaymentIndex();
            int amount = receivedPayments.get(i).getCurrentPaymentIndex() - paymentIndex;

            System.out.println(amount);

            byte[] currentHash = currentPayments.get(i).getCurrentDigest();

            for (int j = 0; j < amount; j++) {
                currentHash = getMessageDigest().digest(currentHash);
            }

            if (Arrays.equals(currentHash, receivedPayments.get(i).getCurrentDigest())) {
                paymentIndex += amount;
                byte[] lastDigest = Arrays.copyOf(currentHash, currentHash.length);

                payments.getPaymentsWithDifferentValues().get(i).setCurrentDigest(lastDigest);
                payments.getPaymentsWithDifferentValues().get(i).setCurrentPaymentIndex(paymentIndex);
            } else {
                throw new Exception("Invalid payment! Hashes don't match!");
            }

        }
    }

    public String computeUserIdentity() {
        return getCommit().getSignedCertificateFromBrokerToUser().getPlainCertificate().getCertifiedIdentity().getIdentity();
    }

    public PaymentWithDifferentValues getPayments() {
        return payments;
    }

    public void setPayments(PaymentWithDifferentValues payments) {
        this.payments = payments;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @Override
    public String toString() {
        return "UserPaymentDetails{" +
                "payments=" + payments +
                ", commit=" + commit +
                '}';
    }
}
