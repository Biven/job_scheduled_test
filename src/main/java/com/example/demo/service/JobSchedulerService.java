package com.example.demo.service;

import com.example.demo.entity.Activity;
import com.example.demo.entity.Priority;
import com.example.demo.entity.PriorityJob;
import com.example.demo.entity.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JobSchedulerService {

    private final Logger logger = LoggerFactory.getLogger(JobSchedulerService.class);

    private final PriorityBlockingQueue<PriorityJob> priorityQueue;
    private final ExecutorService priorityJobExecutor;
    private final ScheduledExecutorService scheduledJobExecutor;
    private final CopyOnWriteArrayList<Future<PriorityJob>> priorityJobExecutionList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Future<ScheduledJob>> scheduledJobExecutionList = new CopyOnWriteArrayList<>();

    public JobSchedulerService(@Value("${priorityExecutor.poolSize:8}") Integer priorityExecutorPoolSize,
                               @Value("${priorityExecutor.queueSize:10}") Integer priorityExecutorQueueSize) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int scheduledExecutorPoolSize = availableProcessors - priorityExecutorPoolSize;
        scheduledJobExecutor = Executors.newScheduledThreadPool(scheduledExecutorPoolSize);
        priorityJobExecutor = Executors.newFixedThreadPool(priorityExecutorPoolSize);
        priorityQueue = new PriorityBlockingQueue<>(priorityExecutorQueueSize, Comparator.comparingInt(value -> value.getPriority().ordinal()));
        ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
        priorityJobScheduler.execute(() -> {
            while (true) {
                try {
                    Future<PriorityJob> future = priorityJobExecutor.submit(priorityQueue.take());
                    priorityJobExecutionList.add(future);
                } catch (InterruptedException e) {
                    logger.error("Failure taking queue item", e);
                    break;
                }
            }
        });
    }

    public void putPriorityJob(Activity activity, Priority priority) {
        priorityQueue.add(new PriorityJob(activity, priority));
    }

    public void putScheduledJob(Activity activity, LocalDateTime runAt) {
        ScheduledJob scheduledJob = new ScheduledJob(activity, runAt);
        Duration duration = Duration.between(LocalDateTime.now(), runAt);
        Future<ScheduledJob> future = scheduledJobExecutor.schedule(scheduledJob, duration.toMillis(), TimeUnit.MILLISECONDS);
        scheduledJobExecutionList.add(future);
    }

    public List<PriorityJob> getCompletedPriorityJobs() {
        return this.priorityJobExecutionList.stream()
                .filter(Future::isDone)
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        logger.error("Failure getting status of job", e);
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    public List<ScheduledJob> getCompletedScheduledJobs() {
        return this.scheduledJobExecutionList.stream()
                .filter(Future::isDone)
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        logger.error("Failure getting status of job", e);
                    }
                    return null;
                }).collect(Collectors.toList());
    }
}
