package com.project.JobScheduler.Handlers;

import com.project.JobScheduler.Jobs;
import org.springframework.stereotype.Component;

@Component
public class SMSHandler implements  JobHandler{
    @Override
    public String type() {
        return "SEND_SMS";
    }

    @Override
    public void handle(Jobs job) {
        System.out.println("Reading JOB data ...");
        System.out.println("sending SMS");
    }
}
