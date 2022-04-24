package org.skylight.executor;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public class ForkJoinTraverseTask<T> extends RecursiveAction {
    private final int THRESHOLD;
    private final int start;
    private final int end;
    private final List<T> list;
    private final Consumer<T> task;

    public ForkJoinTraverseTask(List<T> list, Consumer<T> consumer,int countPerThread) {
        super();
        this.list = list;
        this.task = consumer;
        this.start = 0;
        this.THRESHOLD = countPerThread;
        this.end = list.size();
    }

    private ForkJoinTraverseTask(int start, int end, List<T> list, Consumer<T> consumer,int countPerThread) {
        super();
        this.list = list;
        this.task = consumer;
        this.start = start;
        this.end = end;
        this.THRESHOLD = countPerThread;
    }

    @Override
    protected void compute() {
        if (end - start < THRESHOLD) {
            for (int i = start; i < end; i++) {
                task.accept(list.get(i));
            }
        } else {
            int middle = (start + end) / 2;
            new ForkJoinTraverseTask<>(start, middle, this.list, this.task,this.THRESHOLD).fork();
            new ForkJoinTraverseTask<>(middle, end, this.list, this.task,this.THRESHOLD).fork();
        }
    }
}
