package it.mef.tm.elaboration.timb.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * KafkaListenerConfiguration.java
 * Configurazione del listener kafka
 *  
 * @Change @history
 * @version     1.0
 * @DateUpdate  30 apr 2024
 * @Description prima versione
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class KafkaListenerConfiguration {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaListenerService kafkaListenerService() {
        log.info("Kafka consumer service linked to broker urls: " + bootstrapAddress);
        return new KafkaListenerService();
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
