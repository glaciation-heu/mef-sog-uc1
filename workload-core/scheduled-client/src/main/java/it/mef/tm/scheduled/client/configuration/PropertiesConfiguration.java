/**
 * @Project      acquisizione-timbrature-ms
 * @Package      PropertiesConfiguration.java
 * @DateCreation 12 apr 2019
 */
package it.mef.tm.scheduled.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * PropertiesConfiguration.java 
 * configurazione delle variabili di ambiente comuni tra tutti i microservizi
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  12 apr 2019
 * @Description prima versione
 */
@Configuration
@Data
public class PropertiesConfiguration {
	/** swaggerBasePackge*/
    @Value("${swagger.basePackage}")
    private String swaggerBasePackge;
    /** swaggerTitolo*/
    @Value("${swagger.titolo}")
    private String swaggerTitolo;
    /** swaggerDescrizione*/
    @Value("${swagger.descrizione}")
    private String swaggerDescrizione;
    /** swaggerVersione*/
    @Value("${swagger.versione}")
    private String swaggerVersione;
}
