package com.github.dsx137.jable.base;

import java.util.Optional;

/**
 * <h1>包装类</h1>
 */
public class Wrapper<T> {
    private T value;

    private Wrapper(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }


    public boolean isPresent() {
        return value != null;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public T orElse(T other) {
        return this.isPresent() ? value : other;
    }

    public <R> R orElse(Class<R> clazz, R other) {
        if (this.isPresent()) {
            try {
                return clazz.cast(value);
            } catch (ClassCastException e) {
                return other;
            }
        } else {
            return other;
        }
    }

    public static <D> Wrapper<D> of(D value) {
        return new Wrapper<>(value);
    }

    public static <D> Wrapper<D> empty() {
        return new Wrapper<>(null);
    }
}
