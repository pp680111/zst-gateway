package com.zst.discovery.zstregistry.exception;

/**
 * 表示客户端调用错误的异常类
 */
public class ClientInvokeException extends RuntimeException {
    public ClientInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientInvokeException(String message) {
        super(message);
    }
}
