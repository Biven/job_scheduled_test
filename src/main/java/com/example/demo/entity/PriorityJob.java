package com.example.demo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PriorityJob extends Job<PriorityJob> {

    private Priority priority;

    public PriorityJob(Activity activity, Priority priority) {
        super(activity);
        this.priority = priority;
    }

    @Override
    public PriorityJob call() {
        this.status = Status.IN_PROGRESS;
        try {
            this.activity.doActivity();
            this.status = Status.COMPLETED;
        } catch (Throwable ex) {
            this.status = Status.FAILED;
            this.failure = ex;
        }
        return this;
    }

    @Override
    public String toString() {
        return "PriorityJob{"
                + "status=" + status
                + ", failure=" + failure
                + ", priority=" + priority
                + '}';
    }
}
