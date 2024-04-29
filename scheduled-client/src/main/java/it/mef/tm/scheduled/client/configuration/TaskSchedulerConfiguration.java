package it.mef.tm.scheduled.client.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * TaskSchedulerConfiguration.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  15 apr 2024
 * @Description prima versione
 */
@Configuration
public class TaskSchedulerConfiguration {
	
	/**
	 * Istanza del bean threadPoolTaskScheduler
	 * @return
	 */
	@Bean
    TaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
