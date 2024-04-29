package it.mef.tm.scheduled.client.kafka;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.mef.tm.scheduled.client.exception.BusinessServiceException;
import it.mef.tm.scheduled.client.util.StringUtility;
import it.mef.tm.scheduled.client.util.model.ElaborazioneModel;
import lombok.extern.slf4j.Slf4j;

/**
 * KafkaProducerService.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  19 apr 2024
 * @Description prima versione
 */
@Service
@Slf4j
public class KafkaProducerService {

	private static final String ERR_KAFKA_MESSAGE_UNDELIVERABLE = "ERR_KAFKA_MESSAGE_UNDELIVERABLE";
	
	@Autowired(required = false)
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void publishFlussoElaborazione(ElaborazioneModel elaborazioneModel) {
		
		log.info("about to send Kafka message for timbrature request: " + elaborazioneModel.getPathToFile());
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			String proc = objMapper.writeValueAsString(elaborazioneModel);
			
			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.sendDefault(UUID.randomUUID().toString(), proc);

			SendResult<String, String> sendResult = future.get(20, TimeUnit.SECONDS);
			String responseDeliveredStringed = sendResult.getProducerRecord() != null ? sendResult.getProducerRecord().value() : null;
			
			if (!StringUtility.isNullOrEmpty(responseDeliveredStringed)) {
				log.info(StringUtility.concat("Message for file timbrature ", elaborazioneModel.getPathToFile(), " delivered"));
			} else {
				throw new BusinessServiceException(ERR_KAFKA_MESSAGE_UNDELIVERABLE, "Message for file timbrature not deliverable, kafka unrecognized error");
			}
			
		} catch (InterruptedException e) {
			// Sonar: 
			// "InterruptedException" should not be ignored (java:S2142)
			Thread.currentThread().interrupt();
			throw new BusinessServiceException(ERR_KAFKA_MESSAGE_UNDELIVERABLE, "Message for file timbrature not deliverable, kafka error", e);
		} catch (Exception ex) {
			throw new BusinessServiceException(ERR_KAFKA_MESSAGE_UNDELIVERABLE, "Message for file timbrature not deliverable, kafka error", ex);
		}
	}

}
