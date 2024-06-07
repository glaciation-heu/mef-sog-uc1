package it.mef.tm.elaboration.timb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import it.mef.tm.elaboration.timb.db.repositories.impl.BaseRepositoryImpl;

/**
 * JpaConfiguration.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  31 mag 2024
 * @Description prima versione
 */
@Configuration
@EnableJpaRepositories(basePackages = {
		  "it.mef.tm.elaboration.timb.db.repositories"},
		  repositoryBaseClass = BaseRepositoryImpl.class)
public class JpaConfiguration {

}
