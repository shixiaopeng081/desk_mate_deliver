package com.sunlands.deskmate.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanliu on 2019/5/8.
 */
@Slf4j
public class ThreadFactory {

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(20000));

    static {
        threadPoolExecutor.setThreadFactory(new java.util.concurrent.ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        log.error("error occured thread-name={}", t.getName(), e);
                    }
                });
                return thread;
            }
        });
    }

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        return threadPoolExecutor;
    }


    public static void main(String[] args) {
        log.info("this is log");
        ThreadFactory.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int i = 1/0;
            }
        });
    }
}
