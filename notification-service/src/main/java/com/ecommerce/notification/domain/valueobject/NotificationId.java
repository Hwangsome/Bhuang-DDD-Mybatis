package com.ecommerce.notification.domain.valueobject;

import java.util.UUID;
import java.util.Objects;

public class NotificationId {
    private final String value;

    private NotificationId(String value) {
        this.value = Objects.requireNonNull(value, "NotificationId cannot be null");
    }

    public static NotificationId of(String value) {
        return new NotificationId(value);
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationId that = (NotificationId) o;
        return Objects.equals(value, that.value);
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