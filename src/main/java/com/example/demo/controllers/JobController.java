package com.example.demo.controllers;

import com.example.demo.FakeActivity;
import com.example.demo.entity.Priority;
import com.example.demo.entity.PriorityJob;
import com.example.demo.entity.ScheduledJob;
import com.example.demo.service.JobSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobSchedulerService jobSchedulerService;

    @Autowired
    public JobController(JobSchedulerService jobSchedulerService) {
        this.jobSchedulerService = jobSchedulerService;
    }

    @PutMapping("/")
    public ResponseEntity<String> putJob(@RequestParam(value = "durationSeconds", defaultValue = "5") int durationSeconds) {
        jobSchedulerService.putPriorityJob(new FakeActivity(durationSeconds), Priority.NORMAL);
        return ResponseEntity.ok("Job scheduled");
    }


    @PutMapping("/priority/{priority}")
    public ResponseEntity<String> putPriorityJob(@PathVariable String priority,
                                                 @RequestParam(value = "durationSeconds", defaultValue = "5") int durationSeconds) {
        Priority p = Priority.HIGH.name().equalsIgnoreCase(priority) ? Priority.HIGH : Priority.NORMAL;
        jobSchedulerService.putPriorityJob(new FakeActivity(durationSeconds), p);
        return ResponseEntity.ok("Job scheduled");
    }

    @PutMapping("/scheduled/{hours}/{minutes}/{seconds}")
    public ResponseEntity<String> putScheduledJob(@PathVariable int hours,
                                                  @PathVariable int minutes,
                                                  @PathVariable int seconds,
                                                  @RequestParam(value = "durationSeconds", defaultValue = "5") int durationSeconds) {
        LocalDateTime runAt = LocalDateTime.now()
                .withHour(hours)
                .withMinute(minutes)
                .withSecond(seconds)
                .withNano(0);
        if (runAt.isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>("Planned run date is in the past", HttpStatus.BAD_REQUEST);
        }
        jobSchedulerService.putScheduledJob(new FakeActivity(durationSeconds), runAt);
        return ResponseEntity.ok("Job scheduled");
    }

    @GetMapping(value = "/scheduled")
    public ResponseEntity<String> getCompletedScheduledJobs() {
        return ResponseEntity.ok(jobSchedulerService.getCompletedScheduledJobs().stream().map(ScheduledJob::toString).collect(Collectors.joining(";")));
    }

    @GetMapping(value = "/priority")
    public ResponseEntity<String> getCompletedPriorityJobs() {
        return ResponseEntity.ok(jobSchedulerService.getCompletedPriorityJobs().stream().map(PriorityJob::toString).collect(Collectors.joining(";")));
    }

}
