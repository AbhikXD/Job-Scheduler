package com.project.JobScheduler;

import com.project.JobScheduler.Handlers.EmailHandler;
import com.project.JobScheduler.Handlers.HandlerRegistry;
import com.project.JobScheduler.Handlers.JobHandler;
import com.project.JobScheduler.Handlers.SMSHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class HandlerRegistryTest {

    @Autowired
    HandlerRegistry registry;
    @Test
    void sendEmailHandlerIsRegistered(){
        JobHandler jh = registry.get("SEND_EMAIL");
        assertThat(jh).isInstanceOf(EmailHandler.class);
    }

    @Test
    void sendSMSHandlerIsRegistered(){
        JobHandler jh = registry.get("SEND_SMS");
        assertThat(jh).isInstanceOf(SMSHandler.class);
    }
}
