package com.github.dsx137.jable.exception;

import com.github.dsx137.jable.base.Wrapper;

/**
 * <h1>仅用于代码执行逻辑的异常</h1>
 */
public class LogicException extends Exception {

    private final Wrapper<?> payload;

    /**
     * @param message     异常消息
     * @param payloadData 异常携带的数据
     */
    public LogicException(String message, Object payloadData) {
        super(message);
        this.payload = Wrapper.of(payloadData);
    }

    public LogicException(String message) {
        this(message, null);
    }

    public Wrapper<?> getPayload() {
        return payload;
    }
}
