package com.project.JobScheduler.Handlers;

import com.project.JobScheduler.Jobs;
import org.springframework.stereotype.Component;

@Component
public class GenerateReport  implements JobHandler{

    @Override
    public String type() {
        return "GENERATE_REPORT";
    }

    @Override
    public void handle(Jobs job) {
        System.out.println("Generating Report");
    }
}
