package com.hayaizo.chatsystem.common.annotation;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Executor;

public interface SecureInvokeConfigurer {

    /**
     * 返回一个线程池
     */
    @Nullable
    default Executor getSecureInvokeExecutor() {
        return null;
    }
}
