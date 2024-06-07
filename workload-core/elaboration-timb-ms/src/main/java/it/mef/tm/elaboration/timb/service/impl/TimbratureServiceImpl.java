package it.mef.tm.elaboration.timb.service.impl;

import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT09;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT10;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT11;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT12;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT12_B;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT13;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT14;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT15;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT19;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT20;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT21;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT22;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT23;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT35;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import it.mef.tm.elaboration.timb.metrics.MetricsLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mef.tm.elaboration.timb.db.entities.DAssegnazioneBadge;
import it.mef.tm.elaboration.timb.db.entities.DBadgeH;
import it.mef.tm.elaboration.timb.db.entities.DBadgePk;
import it.mef.tm.elaboration.timb.db.entities.DBadgeTecnologiaTestinaH;
import it.mef.tm.elaboration.timb.db.entities.DCausaleTimbrGruppoLettH;
import it.mef.tm.elaboration.timb.db.entities.DCausaleTimbraturaH;
import it.mef.tm.elaboration.timb.db.entities.DLettoreBadgeFisico;
import it.mef.tm.elaboration.timb.db.entities.DTestinaLettoreBadge;
import it.mef.tm.elaboration.timb.db.entities.DTracciatoTimbratureH;
import it.mef.tm.elaboration.timb.db.filter.AssegnazioneBadgeFilter;
import it.mef.tm.elaboration.timb.db.filter.CausaleTimbratureFilter;
import it.mef.tm.elaboration.timb.db.filter.LettoreBadgeFisicoFilter;
import it.mef.tm.elaboration.timb.db.repositories.AssegnazioneBadgeRepository;
import it.mef.tm.elaboration.timb.db.repositories.CausaleTimbratureHRepository;
import it.mef.tm.elaboration.timb.db.repositories.LettoreBadgeRepository;
import it.mef.tm.elaboration.timb.exception.NotRollBackException;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisica;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisicaRaw;
import it.mef.tm.elaboration.timb.model.TimbraturaFisicaRawScarto;
import it.mef.tm.elaboration.timb.service.TimbratureService;
import it.mef.tm.elaboration.timb.util.DateUtility;
import it.mef.tm.elaboration.timb.util.ListUtility;
import it.mef.tm.elaboration.timb.util.StringUtility;
import lombok.extern.slf4j.Slf4j;


/**
 * TimbratureServiceImpl.java
 * implementazione del servizio per le timbrature
 *  
 * @Change @history
 * @version     1.0
 * @DateUpdate  23 mag 2024
 * @Description prima versione
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, noRollbackFor = NotRollBackException.class)
public class TimbratureServiceImpl implements TimbratureService {

	private static final String STAT_VERSO_TIMBR_INPUT = "I";
	private static final String STAT_VERSO_TIMBR_OUTPUT = "O";
	private static final String STAT_VERSO_TESTINA_INPUT = "I";
	private static final String STAT_VERSO_TESTINA_OUTPUT = "O";
	private static final String STAT_VERSO_TESTINA_INOUT = "A";
	private static final String FLAG_RECORD_SI = "S";
	private static final String FLAG_RECORD_NO = "N";
	
	@Autowired
	private LettoreBadgeRepository lettoreBadgeRepository;
	
	@Autowired
	private AssegnazioneBadgeRepository assegnazioneBadgeRepository;
	
	@Autowired
	private CausaleTimbratureHRepository causaleTimbratureHRepository;

	@Autowired
	private MetricsLog metricsLog;
	
	/**
	 * metodo che normalizza le timbrature
	 * 
	 * @param rawInput
	 * @param timbrAcquisite
	 * @param rawScartate
	 * @param entity
	 * @param tracciato
	 * @param configurazione
	 */
	@Override
	public void normalizzaTimbrature(List<DTimbraturaFisicaRaw> rawInput, List<DTimbraturaFisica> timbrAcquisite,
			List<TimbraturaFisicaRawScarto> rawScartate, DTracciatoTimbratureH tracciato, Path filePath) throws IOException {
		for (DTimbraturaFisicaRaw raw : rawInput) {
			metricsLog.writeLogMetrics(filePath);
			normalizzaTimbratura(rawInput, raw, timbrAcquisite, rawScartate, tracciato);
		}
	}
	
	/**
	 * Metodo verificaTestinaLettore
	 * per estrarre l'unico lettore configurato sull'amministrazione
	 * @param raw
	 * @param tracciato
	 * @throws NotRollBackException
	 */
	private void verificaTestinaLettore(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws NotRollBackException {
		// Se non è stato dichiarato alcun lettore e neanche una testina
		// si assume che il lettore sull'amministrazione debba essere univoco
		if(StringUtility.isNullOrEmpty(raw.getDescCodiceLettore()) && StringUtility.isNullOrEmpty(raw.getDescCodiceTestina())) {
			
			// Verifico se per l'amministrazione esiste un solo LettoreBage configurato e imposto di Default quello
			// Segnalazione 747
			LettoreBadgeFisicoFilter lettoreFilter = new LettoreBadgeFisicoFilter();
			lettoreFilter.setFkAmministrazioneId(tracciato.getVAmministrazioniPk());
			
			boolean lettoriNonValidi = true;
			
			List<DLettoreBadgeFisico> lettori = lettoreBadgeRepository.ricercaStandard(lettoreFilter);
			lettoriNonValidi = !(lettori.size() == 1);
			
			// Se trovo un lettore imposto il suo CodicCodice come default sulla timbratura
			// Nel caso venga trovato più di un lettore, o nessuno, legato all'amministrazione 
			// la if su lettoriNonValidi solleverà l'eccezione
			raw.setDescCodiceLettore(lettori.isEmpty() ? null : lettori.get(0).getCodiCodice());
			
			if (lettoriNonValidi) {
				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT19);
			}
		}
	}
	
	/**
	 * metodo di verifica presenza campi
	 * 
	 * @param raw
	 */
	private void verificaPresenzaCampi(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws NotRollBackException {
		if(StringUtility.isNullOrEmpty(raw.getDescCodiceBadge())) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT20);
		}
		verificaTestinaLettore(raw, tracciato);
		if(StringUtility.isNullOrEmpty(raw.getDescData())) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT21);
		}
		if(StringUtility.isNullOrEmpty(raw.getDescOra())) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT22);
		}
		if(tracciato.getFlagCodiceBdgPerTecnTest().equals(FLAG_RECORD_SI) && StringUtility.isNullOrEmpty(raw.getDescCodiceTestina())) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT23);
		}
	}
	
	
	
	/**
	 * metodo di normalizzazione di una timbratura a partire dall'istanza grezza
	 * 
	 * @param rawInput       timbratre grezze
	 * @param raw            timbratura grezza di riferimento
	 * @param timbrAcquisite timbrature da acquisire
	 * @param rawScartate    timbrature da scartare
	 * @param entity         file
	 * @param tracciato      tracciato
	 * @param configurazione configurazione
	 */
	private void normalizzaTimbratura(List<DTimbraturaFisicaRaw> rawInput, DTimbraturaFisicaRaw raw,
			List<DTimbraturaFisica> timbrAcquisite, List<TimbraturaFisicaRawScarto> rawScartate,
			DTracciatoTimbratureH tracciato) {
		try {
			verificaPresenzaCampi(raw, tracciato);
			DTimbraturaFisica timbrNormalizzata = new DTimbraturaFisica();
			timbrNormalizzata.setDTimbraturaFisicaRaw(raw);
			setDate(timbrNormalizzata, raw, tracciato);
			inserisciVersoTimbratura(raw, timbrNormalizzata, tracciato);
			inserisciLettoreTestinaCausale(timbrNormalizzata, raw, tracciato);
			inserisciAssegnazioneBadge(timbrNormalizzata, raw, tracciato);
			// SE VA TUTTO BENE ACQUISISCO
			acquisisciTimbratura(timbrNormalizzata, timbrAcquisite);
		} catch (NotRollBackException nre) {
			scartaTimbratura(raw, rawScartate, nre);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}
	
	/**
	 * metodo per l'assegnazione delle date
	 * 
	 * @param timbrAcquisitePostAnn timbrature acquisite
	 * @param raw
	 * @param tracciato
	 */
	private void setDate(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) 
		throws NotRollBackException {
		try {
			inserisciDataTimbratura(raw, timbrNormalizzata, tracciato);
		} catch (ParseException pe) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT09);
		} catch (Exception ex) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT09);
		}
	}
	
	/**
	 * metodo per l'assegnazione delle date
	 * 
	 * @param timbrAcquisitePostAnn timbrature acquisite
	 * @param raw
	 * @param tracciato
	 * @throws NotRollBackException 
	 */
	public void verifyDate(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws NotRollBackException {
		try {
			normalizzaDataOraTimbratura(raw, tracciato);
		} catch (Exception ex) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT09);
		}
	}
	
		
	/**
	 * metodo per scartare una timbratura
	 * 
	 * @param rawScartate  scartata
	 * @param raw          timbratura grezza di riferimento
	 * @param motivoScarto
	 */
	private static void scartaTimbratura(DTimbraturaFisicaRaw raw, List<TimbraturaFisicaRawScarto> rawScartate,
			NotRollBackException motivoScarto) {
		rawScartate.add(initializeTimbrDaScartare(raw, motivoScarto));
	}

	/**
	 * metodo per creare una timbratura da scartare
	 * 
	 * @param raw          timbratura grezza di riferimento
	 * @param motivoScarto
	 */
	private static TimbraturaFisicaRawScarto initializeTimbrDaScartare(DTimbraturaFisicaRaw raw,
			NotRollBackException motivoScarto) {
		TimbraturaFisicaRawScarto scarto = new TimbraturaFisicaRawScarto();
		BeanUtils.copyProperties(raw, scarto);
		scarto.setEccezioneScarto(motivoScarto);
		return scarto;
	}
	
	/**
	 * metodo per acquisire una timbratura
	 * 
	 * @param timbrNormalizzata timbratura normalizzata
	 * @param raw               timbratura grezza di riferimento
	 * @param timbrAcquisite    timbrature acquisite
	 */
	private static void acquisisciTimbratura(DTimbraturaFisica timbrNormalizzata,
			List<DTimbraturaFisica> timbrAcquisite) {
		timbrAcquisite.add(timbrNormalizzata);
	}
	
	/**
	 * metodo verifica coerenza e setta assegnazione badge
	 * 
	 * @param timbrNormalizzata timbratre normalizzata
	 * @param raw               timbratura grezza di riferimento
	 * @param entity            file
	 * @param tracciato         tracciato
	 * @param configurazione    configurazione
	 */
	private void inserisciAssegnazioneBadge(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw,
			DTracciatoTimbratureH tracciato) throws NotRollBackException {
		/*
		 * VERIFICO CHE ESISTA UNA ASSEGNAZIONE BADGE IN QUELLA DATA PER
		 * QUELL'AMMINISTRAZIONE
		 */
		AssegnazioneBadgeFilter filter = new AssegnazioneBadgeFilter();
		if (tracciato.getFlagCodiceBdgPerTecnTest().equals(FLAG_RECORD_NO)) {
			filter.setCodBadgeEqual(raw.getDescCodiceBadge());
		}
		else if (tracciato.getFlagCodiceBdgPerTecnTest().equals(FLAG_RECORD_SI))
			filter.setCodBadgeTimbr(raw.getDescCodiceBadge());
		else {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT14);
		}
		filter.setCodAmministrazione(tracciato.getVAmministrazioniPk().toString());
		filter.setDataInizioValidita(timbrNormalizzata.getDttmDataOra());
		List<DAssegnazioneBadge> risultatoAssegnazioneBadge = assegnazioneBadgeRepository.ricercaStandard(filter);
		if (risultatoAssegnazioneBadge.isEmpty()) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT15);
		}
		
		DAssegnazioneBadge badge = risultatoAssegnazioneBadge.get(0);
		assegnaBadge(tracciato, badge, timbrNormalizzata, raw);
	}
	
	/**
	 * metodo per assegnazione badge
	 * 
	 * @param tracciato
	 * @param badge
	 * @param timbrNormalizzata
	 * @param raw
	 * 
	 */
	private void assegnaBadge(DTracciatoTimbratureH tracciato, DAssegnazioneBadge badge, 
			DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) throws NotRollBackException {
		if (verificaBadge(tracciato, badge, timbrNormalizzata, raw)) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT15);
		} else if (verificaBadgePerTech(tracciato, badge, timbrNormalizzata, raw)) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT15);
		} else
			timbrNormalizzata.setDAssegnazioneBadge(badge);
	}

	/**
	 * metodo di verifica badge
	 * 
	 * @param tracciato
	 * @param badge
	 * @param timbrNormalizzata
	 * @param raw
	 * 
	 */
	private boolean verificaBadge(DTracciatoTimbratureH tracciato, DAssegnazioneBadge badge, 
			DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) {
		return tracciato.getFlagCodiceBdgPerTecnTest().equals(FLAG_RECORD_NO)
				&& !verificaBadgePK(badge.getDBadgePk(), timbrNormalizzata, raw)
				&& !verificaBadgePK(badge.getDBadgePkSostituito(), timbrNormalizzata, raw);
	}
	
	/**
	 * metodo di verifica badge per tecnologia
	 * 
	 * @param tracciato
	 * @param badge
	 * @param timbrNormalizzata
	 * @param raw
	 * 
	 */
	private boolean verificaBadgePerTech(DTracciatoTimbratureH tracciato, DAssegnazioneBadge badge, 
			DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) {
		return tracciato.getFlagCodiceBdgPerTecnTest().equals(FLAG_RECORD_SI)
				&& !verificaBadgePKTech(badge.getDBadgePk(), timbrNormalizzata, raw)
				&& !verificaBadgePKTech(badge.getDBadgePkSostituito(), timbrNormalizzata, raw);
	}
	
	/**
	 * metodo di verifica coerenza di un badge per tecnologia
	 * 
	 * @param badge
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 */
	private Boolean verificaBadgePKTech(DBadgePk badge, DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) {
		if(badge == null || badge.getDBadgeTecnologiaTestinaHs() == null)
			return false;
		Supplier<Stream<DBadgeTecnologiaTestinaH>> badgeToSearch = () -> badge.getDBadgeTecnologiaTestinaHs().stream()
				.filter(x -> verificaDataInizioValiditaPerBadge(x.getDataInizioValidita(), timbrNormalizzata)
						&& verificaDataFineValiditaPerBadge(x.getDataFineValidita(), timbrNormalizzata)
						&& x.getCodiCodiceBadgePerTimbr().equals(raw.getDescCodiceBadge()));
		return !(badgeToSearch.get().count() == 0 || !badgeToSearch.get().findFirst().isPresent());

	}
	
	/**
	 * metodo di verifica data inizio validità badge
	 * 
	 * @param dataInizio
	 * @param timbrNormalizzata
	 * 
	 */
	private static boolean verificaDataInizioValiditaPerBadge(Date dataInizio, DTimbraturaFisica timbrNormalizzata) {
		return dataInizio.before(timbrNormalizzata.getDttmDataOra())
				|| dataInizio.equals(timbrNormalizzata.getDttmDataOra());
	}
	
	/**
	 * metodo di verifica data fine validità badge
	 * 
	 * @param dataInizio
	 * @param timbrNormalizzata
	 * 
	 */
	private static boolean verificaDataFineValiditaPerBadge(Date dataFine, DTimbraturaFisica timbrNormalizzata) {
		return dataFine.after(timbrNormalizzata.getDttmDataOra())
				|| dataFine.equals(timbrNormalizzata.getDttmDataOra());
	}
	
	/**
	 * metodo di verifica coerenza di un badge
	 * 
	 * @param badge
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 */
	private Boolean verificaBadgePK(DBadgePk badge, DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) {
		if(badge == null || badge.getDBadgeHs().isEmpty())
			return false;
		Supplier<Stream<DBadgeH>> badgeToSearch = () -> badge.getDBadgeHs().stream()
				.filter(x -> verificaDataInizioValiditaPerBadge(x.getDataInizioValidita(), timbrNormalizzata)
						&& verificaDataFineValiditaPerBadge(x.getDataFineValidita(), timbrNormalizzata)
						&& x.getCodiCodice().equals(raw.getDescCodiceBadge()));
		return !(badgeToSearch.get().count() == 0 || !badgeToSearch.get().findFirst().isPresent());
	}
	
	/**
	 * metodo di verifica coerenza del verso
	 * 
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 * @param tracciato
	 */
	private static void inserisciVersoTimbratura(DTimbraturaFisicaRaw raw, DTimbraturaFisica timbrNormalizzata, DTracciatoTimbratureH 
			tracciato) throws NotRollBackException {
		if(!StringUtility.isNullOrEmpty(raw.getDescVerso()) && !StringUtility.isNullOrEmpty(tracciato.getDescRappresentazVersoIn())) {
			if(raw.getDescVerso().equals(tracciato.getDescRappresentazVersoIn()))
				timbrNormalizzata.setStatVerso(STAT_VERSO_TIMBR_INPUT);
			else if (raw.getDescVerso().equals(tracciato.getDescRappresentazVersoOut()))
				timbrNormalizzata.setStatVerso(STAT_VERSO_TIMBR_OUTPUT);
			else {
				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT10);
			}
		}
	}
	
	/**
	 * metodo di verifica coerenza di: lettore, testina e causale
	 * 
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 * @param entity            file
	 * @param tracciato
	 */
	private void inserisciLettoreTestinaCausale(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw,
			DTracciatoTimbratureH tracciato) throws NotRollBackException {
		/* INIZIO RICERCA LETTORE */
		DLettoreBadgeFisico lettore = timbrNormalizzata.getDLettoreBadgeFisico();
		if (!StringUtility.isNullOrEmpty(raw.getDescCodiceLettore())) {
			LettoreBadgeFisicoFilter filter = new LettoreBadgeFisicoFilter();
			filter.setCodiCodice(raw.getDescCodiceLettore());
			filter.setDataInizioValidita(timbrNormalizzata.getDttmDataOra());
			filter.setDataFineValidita(timbrNormalizzata.getDttmDataOra());
			filter.setFkAmministrazioneId(tracciato.getVAmministrazioniPk());
			List<DLettoreBadgeFisico> lettori = lettoreBadgeRepository.ricercaStandard(filter);
			/* FINE RICERCA LETTORE */
			/*
			 * viene verificata l’esistenza del Codice Lettore all’interno
			 * dell’Amministrazione alla data della timbratura
			 */
			if (lettori.isEmpty()) {

				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT11);
			}
			lettore = lettori.get(0);
			timbrNormalizzata.setDLettoreBadgeFisico(lettore);
		}
		
		// ASSEGNO IL LETTORE
		

		DLettoreBadgeFisico tempo = inserisciTestina(timbrNormalizzata, raw, tracciato, lettore);
		if(lettore == null && tempo != null) {
			lettore = tempo;
			timbrNormalizzata.setDLettoreBadgeFisico(tempo);
		}
		inseriscoCausale(timbrNormalizzata, raw, lettore);
	}
	
	/**
	 * metodo per inserire la causale
	 * 
	 * @param timbrNormalizzata
	 * @param raw
	 * @param lettore
	 * @param entity
	 * @param tracciato
	 * 
	 */
	private void inseriscoCausale(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw, DLettoreBadgeFisico lettore) throws NotRollBackException {
		// VERIFICO IL CODICE CAUSALE
		if (!StringUtility.isNullOrEmpty(raw.getDescCodiceCausale())) {
			verificoCausale(lettore, timbrNormalizzata, raw);
			if (timbrNormalizzata.getDCausaleTimbraturaPk() == null) {
				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT13);
			}
		}
	}
	
	/**
	 * metodo per verificare la causale
	 * 
	 * @param lettore
	 * @param timbrNormalizzata
	 * @param raw
	 * @param entity
	 * 
	 */
	private void verificoCausale(DLettoreBadgeFisico lettore, DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) throws NotRollBackException {
		if(!verificaAssociabilitaCausale(lettore, raw, timbrNormalizzata)) {
			for (DCausaleTimbrGruppoLettH gruppo : lettore.getDGruppoLettoriPerCausali()
				.getDCausaleTimbrGruppoLettHs()) {
				if (verificaGruppo(gruppo)) {
					Supplier<Stream<DCausaleTimbraturaH>> causaliToSearch = () -> gruppo.getDCausaleTimbraturaPk()
							.getDCausaleTimbraturaHs().stream()
							.filter(x -> verificaAppartenenzaGruppo(x, timbrNormalizzata, raw));
					if (causaliToSearch.get().count() != 0 && causaliToSearch.get().findFirst().isPresent()) {
						timbrNormalizzata.setDCausaleTimbraturaPk(gruppo.getDCausaleTimbraturaPk());
						break;
					}
				}
			} 
		}
	}
	
	private boolean verificaAssociabilitaCausale(DLettoreBadgeFisico lettore, DTimbraturaFisicaRaw raw, DTimbraturaFisica timbrNormalizzata) throws NotRollBackException {
		CausaleTimbratureFilter filter = new CausaleTimbratureFilter();
		filter.setCodice(raw.getDescCodiceCausale());
		filter.setCodiceAmministrazione(lettore.getVAmministrazioniPk().toString());
		filter.setDataInizioValidita(timbrNormalizzata.getDttmDataOra());
		filter.setDataFineValidita(timbrNormalizzata.getDttmDataOra());
		List<DCausaleTimbraturaH> causali = this.causaleTimbratureHRepository.ricercaStandard(filter);
		if(ListUtility.isNullOrEmpty(causali) && condizioneCausale(causali, timbrNormalizzata)) {
				return true;
		}
		if (lettore.getDGruppoLettoriPerCausali() == null || lettore.getDGruppoLettoriPerCausali().getDCausaleTimbrGruppoLettHs().isEmpty()) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT13);
		}
		return false;
	}

	private boolean condizioneCausale(List<DCausaleTimbraturaH> causali, DTimbraturaFisica timbrNormalizzata) {
		if( causali != null && !causali.isEmpty()) {
			if(causali.get(0).getDCausaleTimbraturaPk().getDCausaleTimbrGruppoLettHs() == null || causali.get(0).getDCausaleTimbraturaPk().getDCausaleTimbrGruppoLettHs().isEmpty()) {
				timbrNormalizzata.setDCausaleTimbraturaPk(causali.get(0).getDCausaleTimbraturaPk());
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean verificaAppartenenzaGruppo(DCausaleTimbraturaH x, DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw) {
//		return x.getVAmministrazioniPk().equals(entity.getVAmministrazioniPk().getSequIdAmministrazionePk())
		return x.getCodiCodice().equals(raw.getDescCodiceCausale())
				&& verificaDataInizioValiditaPerBadge(x.getDataInizioValidita(), timbrNormalizzata)
				&& verificaDataFineValiditaPerBadge(x.getDataFineValidita(), timbrNormalizzata);
	}
	
	/**
	 * verifica gruppo causale
	 * 
	 * @param gruppo
	 * 
	 */
	private static boolean verificaGruppo(DCausaleTimbrGruppoLettH gruppo) {
		return gruppo.getDCausaleTimbraturaPk() != null
				&& !gruppo.getDCausaleTimbraturaPk().getDCausaleTimbraturaHs().isEmpty();
	}
	
	/**
	 * metodo per inserire la testina
	 * 
	 * @param timbrNormalizzata
	 * @param raw
	 * @param entity
	 * @param tracciato
	 * @param lettore
	 * 
	 */
	private DLettoreBadgeFisico inserisciTestina(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw,
			DTracciatoTimbratureH tracciato, DLettoreBadgeFisico lettore) throws NotRollBackException {
		// VERIFICO IL CODICE TESTINA
		if (!StringUtility.isNullOrEmpty(raw.getDescCodiceTestina())) {
			if (lettore != null && lettore.getDTestinaLettoreBadges().isEmpty()) {
				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT12);
			} else if(lettore == null) {
				lettore = getLettoreFromTestina(timbrNormalizzata, raw, tracciato);
			}
			/*
			 * VERIFICO CHE, ALLA DATA ED ORA DELLA TIMBRATURA, ESISTA UNA TESTINA DEL
			 * LETTORE CON LO STESSO CODICE DI QUELLA PRESENTE SULLA TIMBRATURA
			 */
			boolean setVerso = timbrNormalizzata.getStatVerso() == null;
			DTestinaLettoreBadge testina = verificaVerso(timbrNormalizzata, raw, lettore);
			// ASSEGNO LA TESTINA
			timbrNormalizzata.setDTestinaLettoreBadge(testina);
			if(setVerso && testina.getStatVerso() != null) {
				if(testina.getStatVerso().equals(STAT_VERSO_TESTINA_INPUT)) {
					timbrNormalizzata.setStatVerso(STAT_VERSO_TIMBR_INPUT);
				} else if(testina.getStatVerso().equals(STAT_VERSO_TESTINA_OUTPUT)) {
					timbrNormalizzata.setStatVerso(STAT_VERSO_TIMBR_OUTPUT);
				} else {
					throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT10);
				}
			}
			return lettore;
		}
		return null;
	}
	
	/**
	 * metodo per prelevare il lettore dalla testina
	 * 
	 * @param timbrNormalizzata
	 * @param raw
	 * 
	 */
	private DLettoreBadgeFisico getLettoreFromTestina(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws NotRollBackException {
		if(StringUtility.isNullOrEmpty(raw.getDescCodiceTestina())) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT12);
		}
		LettoreBadgeFisicoFilter filter = new LettoreBadgeFisicoFilter();
		filter.setFkAmministrazioneId(tracciato.getVAmministrazioniPk());
		filter.setCodiTestina(raw.getDescCodiceTestina());
		filter.setDataInizioValidita(timbrNormalizzata.getDttmDataOra());
		filter.setDataFineValidita(timbrNormalizzata.getDttmDataOra());
		List<DLettoreBadgeFisico> ris = lettoreBadgeRepository.ricercaStandard(filter);
		if(ris.isEmpty()) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT12);
		} else if(ris.size() > 1) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT35);
		}
		return ris.get(0);
	}
	
	/**
	 * metodo per verificare il verso
	 * 
	 * @param timbrNormalizzata
	 * @param raw
	 * @param lettore
	 * 
	 */
	private DTestinaLettoreBadge verificaVerso(DTimbraturaFisica timbrNormalizzata, DTimbraturaFisicaRaw raw, DLettoreBadgeFisico lettore) throws NotRollBackException {
		Supplier<Stream<DTestinaLettoreBadge>> testineToSearch = () -> cercaTestina(lettore, timbrNormalizzata, raw, null);
		if(testineToSearch.get().count() == 0 || !testineToSearch.get().findFirst().isPresent()) {
			throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT12);
		}
		if(timbrNormalizzata.getStatVerso() != null) {
			testineToSearch = () -> cercaTestina(lettore, timbrNormalizzata, raw, timbrNormalizzata.getStatVerso().equals(STAT_VERSO_TIMBR_INPUT) ? STAT_VERSO_TESTINA_INPUT: STAT_VERSO_TESTINA_OUTPUT);
			if(testineToSearch.get().count() == 0 || !testineToSearch.get().findFirst().isPresent()) {
				throw new NotRollBackException(ERR_GFT_SERVICE_TMGFT12_B);
			}
		}
		
		return testineToSearch.get().findFirst().get();
	}
	
	/**
	 * metodo di verifica coerenza di una testina
	 * 
	 * @param lettore
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 * @param verso
	 */
	private Stream<DTestinaLettoreBadge> cercaTestina(DLettoreBadgeFisico lettore, DTimbraturaFisica timbrNormalizzata, 
			DTimbraturaFisicaRaw raw, String verso) {
		if(verso != null) { 
			return lettore.getDTestinaLettoreBadges().stream().
				filter(x -> verificaDataInizioValiditaPerBadge(x.getDataInizioValidita(), timbrNormalizzata)
						&& verificaDataFineValiditaPerBadge(x.getDataFineValidita(), timbrNormalizzata)
						&& (verifyVerso(x, verso, raw)));
		} else {
			return lettore.getDTestinaLettoreBadges().stream().
					filter(x -> x.getCodiCodice().equals(raw.getDescCodiceTestina())
							&& verificaDataInizioValiditaPerBadge(x.getDataInizioValidita(), timbrNormalizzata)
							&& (verificaDataFineValiditaPerBadge(x.getDataFineValidita(), timbrNormalizzata)));
		}
	}
	
	/**
	 * metodo di verifica coerenza di una testina
	 * 
	 * @param x
	 * @param verso
	 */
	private static boolean verifyVerso(DTestinaLettoreBadge x, String verso, DTimbraturaFisicaRaw raw) {
		return (x.getCodiCodice().equals(raw.getDescCodiceTestina()) && x.getStatVerso() == null) 
				|| (x.getCodiCodice().equals(raw.getDescCodiceTestina()) && (x.getStatVerso().equals(verso) || x.getStatVerso().equals(STAT_VERSO_TESTINA_INOUT)));
	}
	
	/**
	 * metodo di verifica coerenza della data di timbratura
	 * 
	 * @param timbrNormalizzata timbratura normalizzata di riferimento
	 * @param raw               timbratura grezza di riferimento
	 * @param tracciato
	 */
	private static void inserisciDataTimbratura(DTimbraturaFisicaRaw raw, DTimbraturaFisica timbrNormalizzata,
			DTracciatoTimbratureH tracciato) throws ParseException {
		String dataPattern = DateUtility.FORMATO_ITALY + " "
				+ DateUtility.FORMATO_TIME_DB;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataPattern);
		simpleDateFormat.setLenient(false);
		timbrNormalizzata.setDttmDataOra(simpleDateFormat.parse(raw.getDescData() + " " + raw.getDescOra()));
	}
	
	/**
	 * metodo di verifica coerenza della data di timbratura
	 * 
	 * @param raw               timbratura grezza di riferimento
	 * @param tracciato
	 */
	private static void normalizzaDataOraTimbratura(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws ParseException {
		normalizzaDataTimbratura(raw, tracciato);
		normalizzaOraTimbratura(raw, tracciato);
		inserisciDataTimbratura(raw, new DTimbraturaFisica(), tracciato);
	}
	
	/**
	 * metodo di verifica coerenza della data di timbratura
	 * 
	 * @param raw               timbratura grezza di riferimento
	 * @param tracciato
	 */
	private static void normalizzaDataTimbratura(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws ParseException {
		String dataPattern = tracciato.getStatFormatoData().toLowerCase().replace("mm", "MM");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataPattern);
		simpleDateFormat.setLenient(false);
		Date dataRaw = simpleDateFormat.parse(raw.getDescData());
		simpleDateFormat = new SimpleDateFormat(DateUtility.FORMATO_ITALY);
		raw.setDescData(simpleDateFormat.format(dataRaw));
	}
	
	/**
	 * metodo di verifica coerenza dell'ora di timbratura
	 * 
	 * @param raw               timbratura grezza di riferimento
	 * @param tracciato
	 */
	private static void normalizzaOraTimbratura(DTimbraturaFisicaRaw raw, DTracciatoTimbratureH tracciato) throws ParseException {
		switch(tracciato.getStatFormatoOra()) {
			case "MMM":
				raw.setDescOra(DateUtility.getHoursFromTimeMMM(raw.getDescOra()));
				break;
			case "SSS":
				raw.setDescOra(DateUtility.getHoursFromTimeSSS(raw.getDescOra()));
				break;
			default:
				String dataPattern = DateUtility.FORMATO_ITALY + " "
						+ tracciato.getStatFormatoOra().toLowerCase().replace("hh", "HH").replace("mi", "mm");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataPattern);
				simpleDateFormat.setLenient(false);
				Date temp = simpleDateFormat.parse(raw.getDescData() + " " + raw.getDescOra());
				simpleDateFormat = new SimpleDateFormat(DateUtility.FORMATO_TIME_DB);
				raw.setDescOra(simpleDateFormat.format(temp));
				break;
		}
	}
	
	
}
