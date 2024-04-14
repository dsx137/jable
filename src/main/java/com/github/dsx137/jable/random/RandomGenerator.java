package com.github.dsx137.jable.random;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public abstract class RandomGenerator<T> {

    protected Random getRandom(byte[] seed, boolean secure) {
        if (seed == null) {
            return secure ? new SecureRandom() : new Random();
        } else {
            return secure ? new SecureRandom(seed) : new Random(Arrays.hashCode(seed));
        }
    }

    protected abstract T generate(byte[] seed, int length, Random random);

    public T generate(byte[] seed, int length, boolean secure) {
        return generate(seed, length, getRandom(seed, secure));
    }

    public T generate(int length) {
        return this.generate(null, length, true);
    }

    public T generate(byte[] seed, int length) {
        return this.generate(seed, length, true);
    }

    public T generate(int length, boolean secure) {
        return this.generate(null, length, secure);
    }
}
