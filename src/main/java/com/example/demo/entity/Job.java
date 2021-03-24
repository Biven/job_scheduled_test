package com.example.demo.entity;

import lombok.Data;

import java.util.concurrent.Callable;

@Data
public abstract class Job<T> implements Callable<T> {

    protected Status status;
    protected Throwable failure;
    protected Activity activity;

    private Job() {
    }

    public Job(Activity activity) {
        this.activity = activity;
        this.status = Status.QUEUED;
    }
}
