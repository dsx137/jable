package com.github.dsx137.jable.base;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <h1>C# 风格的属性</h1>
 *
 * @param <T> 值的类型
 *            <p>getter: wov -> {}</p>
 *            <p>setter: (wov, nv) -> {}</p>
 */
public class Property<T> {
    private final Wrapper<T> value; // 这里包装是因为如果setter直接传T类型的对象，直接修改对象本身的值，不会反映到类的成员变量上（引用传递直接修改引用）

    private final Function<Wrapper<T>, T> getter;

    private final BiConsumer<Wrapper<T>, T> setter;


    public Property(T value, Function<Wrapper<T>, T> getter, BiConsumer<Wrapper<T>, T> setter) {
        this.value = Wrapper.of(value);
        this.getter = getter;
        this.setter = setter;
    }

    public Property(Function<Wrapper<T>, T> getter, BiConsumer<Wrapper<T>, T> setter) {
        this(null, getter, setter);
    }

    public T get() {
        return getter.apply(this.value);
    }

    public void set(T value) {
        setter.accept(this.value, value);
    }
}
