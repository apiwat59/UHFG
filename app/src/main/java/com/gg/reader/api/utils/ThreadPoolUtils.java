package com.gg.reader.api.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class ThreadPoolUtils {
    public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public static ExecutorService fixedThreadPool;

    public static void runCachedThread(Runnable command) {
        cachedThreadPool.execute(command);
    }

    public static void initFixedThreadPool(int nThread) {
        fixedThreadPool = Executors.newFixedThreadPool(10);
    }

    public static void runFixedThread(Runnable command) {
        fixedThreadPool.execute(command);
    }

    public static void run(Runnable command) {
        runCachedThread(command);
    }

    public static void shutdownCachedThread() {
        ExecutorService executorService = cachedThreadPool;
        if (executorService != null) {
            executorService.shutdown();
            cachedThreadPool = Executors.newCachedThreadPool();
        }
    }

    public static void shutdownFixedThread() {
        ExecutorService executorService = fixedThreadPool;
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
