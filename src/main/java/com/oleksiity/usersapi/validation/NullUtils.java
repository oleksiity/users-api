package com.oleksiity.usersapi.validation;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NullUtils {

    public static <T> void updateIfChanged(Consumer<T> consumer, T value, Supplier<T> supplier) {
        Predicate<T> predicate = input -> !value.equals(input);
        if (value != null && predicate.test(supplier.get())) {
            consumer.accept(value);
        }
    }
}
