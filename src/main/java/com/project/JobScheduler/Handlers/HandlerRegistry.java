package com.project.JobScheduler.Handlers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class HandlerRegistry {
    public final HashMap<String, JobHandler> handlersMap  = new HashMap<>();

    public HandlerRegistry(List<JobHandler> listHandlers){
        for(JobHandler handler : listHandlers){
            handlersMap.put(handler.type(),handler);
        }
    }
    public JobHandler get(String type){
        JobHandler jobHandler = handlersMap.get(type);
        if (jobHandler == null) {
            throw new IllegalArgumentException("No Handler could be for the type: "+type);
        }
        return jobHandler;
    }
}
