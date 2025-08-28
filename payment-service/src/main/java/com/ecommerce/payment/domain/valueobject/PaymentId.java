package com.ecommerce.payment.domain.valueobject;

import java.util.UUID;
import java.util.Objects;

public class PaymentId {
    private final String value;

    private PaymentId(String value) {
        this.value = Objects.requireNonNull(value, "PaymentId cannot be null");
    }

    public static PaymentId of(String value) {
        return new PaymentId(value);
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID().toString());
    }

    public static PaymentId generateForOrder(String orderId) {
        return new PaymentId("PAY_" + orderId + "_" + System.currentTimeMillis());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentId paymentId = (PaymentId) o;
        return Objects.equals(value, paymentId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}