package it.mef.tm.scheduled.client.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import it.mef.tm.scheduled.client.util.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * KafkaProducerConfig.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  9 apr 2024
 * @Description prima versione
 */
@Configuration
@Slf4j
public class KafkaProducerConfig {
 
	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String bootstrapAddress;

    @Value("${spring.kafka.producer.topic}")
    private String topicDefault;

    @Value("${spring.kafka.producer.topic-extra}")
    private String topicExtra;
	
	/**
	 * Instanza del bean producerFactory
	 * @return
	 */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
    	
    	log.info(StringUtility.concat("Kafka producer attesting to broker urls: ", bootstrapAddress));
    	
        Map<String, Object> configProps = new HashMap<>();
        
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // Serializzo stringhe come chiavi e come contenuti dei messaggi
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }
 
    /**
     * Instanza del bean kafkaTemplateDefault
     * @return
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateDefault() {
    	KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
    	// Setto il topic di default
    	kafkaTemplate.setDefaultTopic(topicDefault);
        return kafkaTemplate;
    }

    /**
     * Instanza del bean kafkaTemplateExtra
     * @return
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateExtra() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        // Setto il topic di default
        kafkaTemplate.setDefaultTopic(topicExtra);
        return kafkaTemplate;
    }
}
