/**
 * @Project      acquisizione-timbrature-ms
 * @Package      SwaggerConfiguration.java
 * @DateCreation 12 apr 2019
 */
package it.mef.tm.scheduled.client.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfiguration.java 
 * configurazione di swagger
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  12 apr 2019
 * @Description prima versione
 */
@Configuration
@EnableSwagger2
@Slf4j
public class SwaggerConfiguration {
	/** environment */
	@Autowired
	private PropertiesConfiguration configProperties;
	
	/**
	 * Metodo che imposta le informazioni di Swagger, in particolare la
	 * versione e le api info ed esegue il recupero di tutti i tag Swagger
	 * nel progetto
	 * 
	 * @return Docket
	 * @Change @history
	 * @version 1.0
	 * @DateUpdate 13 mar 2019
	 * @Description Prima Stesura
	 */
	
	@Bean
	public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(configProperties.getSwaggerBasePackge()))
                .build()
                .apiInfo(apiInfo());
    }
	/**
	 * imposta l'oggetto ApiInfo di swagger, indicando titolo, descrizione
	 * ecc
	 * 
	 * @return ApiInfo
	 * @Change @history
	 * @version 1.0
	 * @DateUpdate 13 mar 2019
	 * @Description Prima Stesura
	 */
	private ApiInfo apiInfo() {
		log.info("Inizio Configurazione Swagger costruzione apiInfo");
		ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
		apiInfoBuilder.title(configProperties.getSwaggerTitolo());
		apiInfoBuilder.description(configProperties.getSwaggerDescrizione());
		apiInfoBuilder.version(configProperties.getSwaggerVersione());
		log.info("Fine configurazione Swagger costruzione apiInfo");
		return apiInfoBuilder.build();
	}
	/**
	 * imposta dove swagger trova le risorse statiche
	 * 
	 * @Change @history
	 * @version 1.0
	 * @DateUpdate 13 mar 2019
	 * @Description Prima Stesura
	 */
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("SwaggerConfig aggiunta delle risorse handler");
		// aggiungiamo le risorse statiche home presente nel jar
		// springfox-swagger-ui-2.9.2
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		// aggiungiamo le risorse statiche css, js ecc presenti nel jar
		// springfox-swagger-ui-2.9.2
		registry.addResourceHandler("/webjars/**").addResourceLocations(
				"classpath:/META-INF/resources/webjars/");
	}
}
