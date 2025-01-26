package com.hayaizo.chatsystem.common.factorty;

import com.hayaizo.chatsystem.common.exception.GlobalUncaughtExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = factory.newThread(r);
        thread.setUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.getInstance());
        return thread;
    }
}
