package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void baseControllerTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/job/").param("durationSeconds", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.put("/job/priority/high").param("durationSeconds", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        LocalDateTime runAt = LocalDateTime.now().plusSeconds(1);
        String url = String.format("/job/scheduled/%d/%d/%d", runAt.getHour(), runAt.getMinute(), runAt.getSecond());
        mvc.perform(MockMvcRequestBuilders.put(url).param("durationSeconds", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Thread.sleep(2000);

        String expectedContent = "PriorityJob{status=COMPLETED, failure=null, priority=NORMAL};PriorityJob{status=COMPLETED, failure=null, priority=HIGH}";
        mvc.perform(MockMvcRequestBuilders.get("/job/priority"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedContent));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String expectedScheduledJobs = String.format("ScheduledJob{status=COMPLETED, failure=null, scheduledRunTime=%s, actualRunTime=%s}",
                runAt.format(dateTimeFormatter), runAt.format(dateTimeFormatter));
        mvc.perform(MockMvcRequestBuilders.get("/job/scheduled"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedScheduledJobs));
    }
}
