package com.example.demo;

import com.example.demo.entity.Priority;
import com.example.demo.entity.PriorityJob;
import com.example.demo.service.JobSchedulerService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobScheduledServiceTest {

    @Test
    void jobSchedulerTest() throws InterruptedException {
        JobSchedulerService jobSchedulerService = new JobSchedulerService(8, 10);
        jobSchedulerService.putPriorityJob(new FakeActivity(3), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(3), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(3), Priority.NORMAL);
        assertEquals(0, jobSchedulerService.getCompletedPriorityJobs().size());
        Thread.sleep(4000);
        assertEquals(3, jobSchedulerService.getCompletedPriorityJobs().size());
    }

    @Test
    void scheduledJobTest() throws InterruptedException {
        JobSchedulerService jobSchedulerService = new JobSchedulerService(8, 10);
        LocalDateTime runIn1Second = LocalDateTime.now().plusSeconds(1);
        jobSchedulerService.putScheduledJob(new FakeActivity(1), runIn1Second);
        assertEquals(0, jobSchedulerService.getCompletedScheduledJobs().size());
        Thread.sleep(3000);
        assertEquals(1, jobSchedulerService.getCompletedScheduledJobs().size());
    }

    @Test
    void priorityJobTest() throws InterruptedException {
        JobSchedulerService jobSchedulerService = new JobSchedulerService(4, 10);

        jobSchedulerService.putPriorityJob(new FakeActivity(2, "normal"), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(6, "normal"), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(6, "normal"), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(6, "normal"), Priority.NORMAL);
        jobSchedulerService.putPriorityJob(new FakeActivity(6, "normal"), Priority.NORMAL);

        jobSchedulerService.putPriorityJob(new FakeActivity(2, "high"), Priority.HIGH);

        Thread.sleep(4500);
        List<PriorityJob> completedJobs = jobSchedulerService.getCompletedPriorityJobs();
        assertEquals(2, completedJobs.size());
        assertEquals(1L, completedJobs.stream().filter(priorityJob -> priorityJob.getPriority() == Priority.HIGH).count());
        assertEquals(1L, completedJobs.stream().filter(priorityJob -> priorityJob.getPriority() == Priority.NORMAL).count());
    }
}
