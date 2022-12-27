package com.ebay.pmt2.ejmask.core.util;

import javax.annotation.Nullable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executor to execute operation
 *
 * @author prakv
 */
public class ExecutorUtil {

    private static final ExecutorService executor;
    private static final int MAXIMUM_POOL_SIZE = 4000;
    private static final int CORE_POOL_SIZE = 40;

    static {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4000));
    }

    /**
     * Execute the given task with timeout
     *
     * @param task    as instance of
     * @param timeout as timeout
     * @param unit    as time unit
     * @param <R>
     * @return response of type R, if there is a timeout return null
     */
    @Nullable
    public static <R> R execute(Callable<R> task, long timeout, TimeUnit unit) {
        Future<R> future = executor.submit(task);
        try {
            return future.get(timeout, unit);
        } catch (TimeoutException ex) {
            LoggerUtil.error("executor-util", "TimeoutException", ex.getMessage());
        } catch (InterruptedException ex) {
            LoggerUtil.error("executor-util", "InterruptedException", ex.getMessage());
        } catch (ExecutionException ex) {
            LoggerUtil.error("executor-util", "ExecutionException", ex.getMessage());
        } finally {
            if (!(future.isDone() || future.isCancelled())) {
                future.cancel(true);
            }
        }
        return null;
    }
}
