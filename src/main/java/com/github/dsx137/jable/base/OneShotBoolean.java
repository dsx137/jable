package com.github.dsx137.jable.base;

/**
 * <h1>获取即置false的Boolean</h1>
 */
public class OneShotBoolean extends Property<Boolean> {
    public OneShotBoolean() {
        super(false,
                wov -> {
                    boolean v = wov.get();
                    wov.set(false);
                    return v;
                },
                Wrapper::set);
    }

    public OneShotBoolean(boolean origin) {
        super(origin,
                wov -> {
                    boolean v = wov.get();
                    wov.set(false);
                    return v;
                },
                Wrapper::set);
    }
}