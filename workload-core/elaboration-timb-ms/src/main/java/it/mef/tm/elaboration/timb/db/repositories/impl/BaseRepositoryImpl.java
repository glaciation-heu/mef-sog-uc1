package it.mef.tm.elaboration.timb.db.repositories.impl;

import static it.mef.tm.elaboration.timb.util.Constants.FORMAT_ORARIO;
import static it.mef.tm.elaboration.timb.util.Constants.MEZZANOTTE;
import static it.mef.tm.elaboration.timb.util.Constants.TRUNC;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import it.mef.tm.elaboration.timb.db.entities.BaseDomainTimed;
import it.mef.tm.elaboration.timb.db.filter.BaseDomainFilter;
import it.mef.tm.elaboration.timb.db.repositories.BaseRepository;

/**
 * BaseRepositoryImpl.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  31 mag 2024
 * @Description prima versione
 */
@Transactional(rollbackFor = Exception.class)
public class BaseRepositoryImpl<T extends BaseDomainTimed, ID extends Serializable>
		extends SimpleJpaRepository<T,ID> implements BaseRepository<T, ID> {


	private EntityManager entityManager;

	/**
	 * @param domainClass
	 * @param em
	 */
	public BaseRepositoryImpl(Class<T> domainClass) {
		super(domainClass, null);
	}

	/**
	 * descrizione
	 * 
	 * @param entityInformation
	 * @param entityManager
	 */
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}
	
	/**
	 * @param domainClass
	 * @param em
	 */
	public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}


	/**
	 * Imposta la ricerca di tutti i record con quella determinata seq_id che
	 * entrano nell'intervallo temporale, applicando la paginazione e l'ordinamento
	 *
	 * @param filter
	 * @return
	 */
	@Override
	public <F extends BaseDomainFilter<T>> List<T> ricercaStandard(F filter) {

		// Dove non è necessaria la root è possibile invocare il metodo impostaRicerca
		CriteriaQuery<T> query = impostaRicerca(filter);

		return entityManager.createQuery(query).getResultList();
	}

	/**
	 * imposta i predicati.
	 * @param filter
	 * 
	 * @return
	 */
	protected <F extends BaseDomainFilter<T>> CriteriaQuery<T> impostaRicerca(F filter) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> query = null;
		Root<T> root = null;
		query = builder.createQuery(this.getDomainClass());
		query.distinct(true);
		root = query.from(this.getDomainClass());
		
		Predicate wherePred;
		wherePred = getPredicates(filter, builder, root);
		
		query.where(wherePred);
		query.select(root);
		
		return query;
	}

	/**
	 * genera i predicati.
	 * @param filter
	 * @param builder
	 * @param root
	 * 
	 * @return
	 */
	private <F extends BaseDomainFilter<T>> Predicate getPredicates(F filter, CriteriaBuilder builder, Root<T> root) {
		List<Predicate> predicates = new ArrayList<>();
		predicates.addAll(filter.getCustomPredicate(builder, root));
		
		// Se è impostata la ricerca Storica non si devono applicare filtri sulle
		// versioni (ricercaStorico = true) altrimenti devono essere controllate le date
		if (filter.getDataInizioValidita() != null) {

			Expression<Date> dataInizExpr = null;
			Expression<Date> dataFineExpr = null;
			
			SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ORARIO);
			
			if (MEZZANOTTE.equals(sdf.format(filter.getDataInizioValidita()))) {
				dataInizExpr = builder.function(TRUNC, Date.class, root.<Date>get("dataInizioValidita"));
				dataFineExpr = builder.function(TRUNC, Date.class, root.<Date>get("dataFineValidita"));
			}
			else {
				dataInizExpr = root.<Date>get("dataInizioValidita");
				dataFineExpr = root.<Date>get("dataFineValidita");
			}
			
			// la DataFineValidità della versione sul DB deve essere maggiore uguale della
			// data Inizio
			predicates.add(builder.greaterThanOrEqualTo(dataFineExpr, filter.getDataInizioValidita()));

			// Se NON è stato impostato il filtro di data Fine validità la da
			if (!filter.isIntervalloTemporale()) {
				// la DataInizioValidita della versione deve essere minore o uguale al campo di
				// filtro impostato
				predicates.add(builder.lessThanOrEqualTo(dataInizExpr, filter.getDataInizioValidita()));
			} else {
				predicates.add(
						builder.lessThanOrEqualTo(dataInizExpr, filter.getDataFineValidita()));
			}
		}
		
		return builder.and(predicates.toArray(new Predicate[0]));
	}

}
