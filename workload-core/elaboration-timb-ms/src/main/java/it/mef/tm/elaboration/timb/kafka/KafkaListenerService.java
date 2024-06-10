package it.mef.tm.elaboration.timb.kafka;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.util.StringUtils;
import it.mef.tm.elaboration.timb.model.ElaborazioneModel;
import it.mef.tm.elaboration.timb.service.LetturaTimbratureService;
import lombok.extern.slf4j.Slf4j;

/**
 * KafkaListenerService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  30 apr 2024
 * @Description prima versione
 */
@Slf4j
public class KafkaListenerService {

	@Autowired
	private LetturaTimbratureService letturaTimbratureService;
	
	/**
	 * Metodo consume del messaggio kafka
	 * @param message
	 */
	@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
	@KafkaListener(topics = "${spring.kafka.listener.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${spring.kafka.listener.auto-start:true}", concurrency = "${spring.kafka.listener.concurrency}")
	public void consume(ConsumerRecord<String, String> message) {
		ElaborazioneModel response = null;
		
		try {
			log.info("$$ -> Kafka message received -> {}", message.value());

			ObjectMapper objMapper = new ObjectMapper();
			response = objMapper.readValue(message.value(), new TypeReference<ElaborazioneModel>(){});

			processKafkaMessage(response);
		} catch (Throwable t) {
			log.error(String.format("Processing encountered a error: %s", t.getMessage()), t);
		}
	}

	/**
	 * Metodo che processa il messaggio di ritorno da kafka
	 * @param response
	 * @param message
	 * @throws JAXBException 
	 */
	protected void processKafkaMessage(ElaborazioneModel response) throws JAXBException {
		
		if (StringUtils.isEmpty(response.getPathToFile())) {
			log.error("Kafka received message error: PathToFile not defined!");
			return;
		}

		log.info("$$ -> file in elaboration received: {}", response.getPathToFile());
		
		if (!Paths.get(response.getPathToFile()).toFile().exists()) {
			log.error("Error in time stamps elaboration: file in {} not found!", response.getPathToFile());
			return;
		}
		
		try {
			letturaTimbratureService.letturaFornitura(response.getPathToFile());
		} catch (JAXBException | IOException e) {
			log.error("Error in time stamps elaboration for file {}: Errore writing XML!", response.getPathToFile(), e);
		}
	}
}
