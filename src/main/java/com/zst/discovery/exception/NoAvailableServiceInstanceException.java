package com.zst.discovery.exception;

/**
 * 表示无可用服务实例的异常
 */
public class NoAvailableServiceInstanceException extends RuntimeException {
    public NoAvailableServiceInstanceException(String message) {
        super(message);
    }
}
