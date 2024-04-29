package it.mef.tm.scheduled.client;

import static it.mef.tm.scheduled.client.costants.Costants.URL_BASE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_FILE;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_JOB;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_START;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_STOP;
import static it.mef.tm.scheduled.client.costants.Costants.URL_SERVIZI_GFT_UPLOAD_FILE;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ControllerTest.java
 * 
 * Spiegazione annotation usate:
 * @SpringBootTest - Attiva lo start e configurazione 
 * di springboot in modalità di test JUnit
 * @ActiveProfiles("test") Permette il recupero dell'application-test.yaml
 * @EmbeddedKafka Mock del broker kafka
 * @TestInstance(Lifecycle.PER_CLASS) Permette al @BeforeAll di creare le cartelle temporanee 
 * senza dover essere dichiarato come metodo statico
 * @TestMethodOrder Ordina l'esecuzione delle JUnit
 * @Change @history
 * @version     1.0
 * @DateUpdate  18 apr 2024
 * @Description prima versione
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@AutoConfigureMockMvc
@EmbeddedKafka(topics = "${spring.kafka.producer.topic}", partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Value("${java.io.tmpdir}")
	private String dirTemp;
	
	@BeforeAll
	public void setUp() {
		Path path = Paths.get(dirTemp + File.separator + "timbrature");
		if (!path.toFile().exists()) {
			path.toFile().mkdir();
		}
		path = Paths.get(dirTemp + File.separator + "acquisite");
		if (!path.toFile().exists()) {
			path.toFile().mkdir();
		}
	}
	
	@Test
	@Order(2)
	void testStartTask() throws Exception {

		mockMvc.perform(post("/" + URL_BASE + URL_SERVIZI_GFT_JOB + "/" + URL_SERVIZI_GFT_START))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(true)));
				;
	}

	@Test
	@Order(3)
	void testStopTask() throws Exception {

		mockMvc.perform(post("/" + URL_BASE + URL_SERVIZI_GFT_JOB + "/" + URL_SERVIZI_GFT_STOP))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(true)));
				;
	}

	@Test
	@Order(1)
	void testUploadFile() throws Exception {
		MockMultipartFile kmlfile = new MockMultipartFile("file", UUID.randomUUID().toString() + "txt", MediaType.MULTIPART_FORM_DATA_VALUE, "a file".getBytes());
	    
		mockMvc.perform(multipart("/" + URL_BASE + URL_SERVIZI_GFT_FILE + "/" + URL_SERVIZI_GFT_UPLOAD_FILE)
				.file(kmlfile)
				.characterEncoding("UTF-8"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(true)));
				;
	}
}

