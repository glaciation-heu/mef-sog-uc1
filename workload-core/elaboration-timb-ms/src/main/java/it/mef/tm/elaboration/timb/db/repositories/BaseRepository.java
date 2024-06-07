package it.mef.tm.elaboration.timb.db.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import it.mef.tm.elaboration.timb.db.entities.BaseDomainTimed;
import it.mef.tm.elaboration.timb.db.filter.BaseDomainFilter;

/**
 * BaseRepository.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseDomainTimed, ID extends Serializable> extends JpaRepository<T, ID> {
	<F extends BaseDomainFilter<T>> List<T> ricercaStandard(F filter);
}
