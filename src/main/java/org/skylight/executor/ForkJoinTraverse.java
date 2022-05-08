package org.skylight.executor;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ForkJoinTraverse extends AbstractExecutorService {
    private final ForkJoinPool pool;

    public AtomicInteger threadId = new AtomicInteger(0);

    public ForkJoinTraverse(int threads) {
        ForkJoinPool.ForkJoinWorkerThreadFactory forkJoinWorkerThreadFactory = p -> {
            ForkJoinWorkerThread forkJoinWorkerThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(p);
            forkJoinWorkerThread.setName("AirLight-Pool-Thread-"+threadId.getAndIncrement());
            forkJoinWorkerThread.setContextClassLoader(ForkJoinTraverse.class.getClassLoader());
            return forkJoinWorkerThread;
        };
        pool = new ForkJoinPool(threads,forkJoinWorkerThreadFactory,null,true);
    }

    public String toString(){
        return "ForkJoinTraverse using ForkJoinPool : " + pool.toString();
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
        if (command == null) {
            throw new NullPointerException("Command is null!");
        }
        pool.execute(command);
    }

    private final AtomicReference<ForkJoinTask> concurrentTask = new AtomicReference<>();

    public <T> void execute(List<T> list, Consumer<T> action, int tpt) {
        concurrentTask.set(this.pool.submit(new ForkJoinTraverseTask<>(list, action, tpt)));
    }

    public <T> void blockTraverse(List<T> list, Consumer<T> action, int tpt) {
        this.pool.submit(new ForkJoinTraverseTask<>(list, action, tpt)).join();
    }

    public <T extends Integer> int sum(T[] array, int tpt) {
        AtomicInteger sum = new AtomicInteger(0);
        Consumer<T> action = sum::getAndAdd;
        blockTraverse(Arrays.asList(array), action, tpt);
        return sum.get();
    }

    public <T> void execute(Iterable<T> iterable, Consumer<T> action, int tpt) {
        List<T> l = Lists.newArrayList(iterable);
        concurrentTask.set(this.pool.submit(new ForkJoinTraverseTask<>(l, action, tpt)));
    }

    public void awaitCompletion() {
        concurrentTask.get().join();
    }

}
