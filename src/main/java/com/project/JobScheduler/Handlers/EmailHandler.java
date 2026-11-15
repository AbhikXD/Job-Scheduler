package com.project.JobScheduler.Handlers;

import com.project.JobScheduler.Jobs;
import org.springframework.stereotype.Component;

@Component
public class EmailHandler implements JobHandler {
    @Override
    public String type() {
        return "SEND_EMAIL";
    }

    @Override
    public void handle(Jobs job) {
        System.out.println("---Reading JOB info---");
        System.out.println("Sending Email");
    }
}
