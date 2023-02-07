package com.ebay.ejmask.core.util;

/**
 * Copyright (c) 2023 eBay Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * @param <R>     as Execution results.
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
