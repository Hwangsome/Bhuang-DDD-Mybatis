package com.ecommerce.notification.domain.valueobject;

import java.util.Objects;

public class UserId {
    private final String value;

    private UserId(String value) {
        this.value = Objects.requireNonNull(value, "UserId cannot be null");
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
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