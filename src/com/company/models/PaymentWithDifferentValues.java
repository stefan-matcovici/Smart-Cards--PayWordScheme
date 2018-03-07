package com.company.models;

import java.util.List;

public class PaymentWithDifferentValues {
    private List<Payment> paymentsWithDifferentValues;

    public List<Payment> getPaymentsWithDifferentValues() {
        return paymentsWithDifferentValues;
    }

    public void setPaymentsWithDifferentValues(List<Payment> paymentsWithDifferentValues) {
        this.paymentsWithDifferentValues = paymentsWithDifferentValues;
    }

    @Override
    public String toString() {
        return "PaymentWithDifferentValues{" +
                "paymentsWithDifferentValues=" + paymentsWithDifferentValues +
                '}';
    }
}
