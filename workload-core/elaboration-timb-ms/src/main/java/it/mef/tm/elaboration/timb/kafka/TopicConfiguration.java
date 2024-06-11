package it.mef.tm.elaboration.timb.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class TopicConfiguration {

    @Value(value = "${spring.kafka.listener.topic}")
    private String topic;

    @Value(value = "${spring.kafka.listener.partitions}")
    private int partitions;

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .build();
    }

}
