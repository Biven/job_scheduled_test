package com.example.demo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduledJob extends Job<ScheduledJob> {

    private LocalDateTime scheduledRunTime;
    private LocalDateTime actualRunTime;

    public ScheduledJob(Activity activity, LocalDateTime scheduledRunTime) {
        super(activity);
        this.scheduledRunTime = scheduledRunTime;
    }

    @Override
    public ScheduledJob call() {
        this.status = Status.IN_PROGRESS;
        this.actualRunTime = LocalDateTime.now();
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return "ScheduledJob{"
                + "status=" + status
                + ", failure=" + failure
                + ", scheduledRunTime=" + scheduledRunTime.format(dateTimeFormatter)
                + ", actualRunTime=" + actualRunTime.format(dateTimeFormatter)
                + '}';
    }
}
