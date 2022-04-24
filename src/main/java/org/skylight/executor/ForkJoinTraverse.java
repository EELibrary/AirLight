package org.skylight.executor;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ForkJoinTraverse extends AbstractExecutorService {
    private final ForkJoinPool pool;

    public ForkJoinTraverse(int threads) {
        pool = new ForkJoinPool(threads);
    }

    public ForkJoinTraverse() {
        this(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void shutdown() {
        pool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return pool.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return pool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return pool.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return pool.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        pool.execute(command);
    }

    private final AtomicReference<ForkJoinTask> concurrentTask = new AtomicReference<>();

    public <T> void execute(List<T> list, Consumer<T> action, int tpt) {
        concurrentTask.set(this.pool.submit(new ForkJoinTraverseTask<>(list, action, tpt)));
    }

    public <T> void execute(Iterable<T> iterable, Consumer<T> action, int tpt) {
        List<T> l = Lists.newArrayList(iterable);
        concurrentTask.set(this.pool.submit(new ForkJoinTraverseTask<>(l, action, tpt)));
    }

    public void awaitCompletion() {
        concurrentTask.get().join();

    }

}
