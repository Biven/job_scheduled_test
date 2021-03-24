package com.example.demo;

import com.example.demo.entity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FakeActivity implements Activity {

    private final Logger logger = LoggerFactory.getLogger(FakeActivity.class);

    private final int duration;
    private String output = "fake";

    public FakeActivity(int duration, String output) {
        this.duration = duration;
        this.output = output;
    }

    public FakeActivity(int duration) {
        this.duration = duration;
    }

    @Override
    public void doActivity() {
        try {
            logger.info(output);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }
}
