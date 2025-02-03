package it.mef.tm.elaboration.timb.service.impl;

import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT02;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT03;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT05;
import static it.mef.tm.elaboration.timb.util.Constants.ERR_GFT_SERVICE_TMGFT25;
import static it.mef.tm.elaboration.timb.util.Constants.UTF8_BOM;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import it.mef.tm.elaboration.timb.metrics.MetricsLog;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import it.mef.tm.elaboration.timb.db.entities.DTraccTimbrXmlH;
import it.mef.tm.elaboration.timb.db.entities.DTracciatoTimbratureH;
import it.mef.tm.elaboration.timb.db.repositories.TracciatoTimbratureHRepository;
import it.mef.tm.elaboration.timb.exception.NotRollBackException;
import it.mef.tm.elaboration.timb.exception.PreconditionException;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisica;
import it.mef.tm.elaboration.timb.model.DTimbraturaFisicaRaw;
import it.mef.tm.elaboration.timb.model.TimbraturaEsitoLettura;
import it.mef.tm.elaboration.timb.model.TimbraturaFisicaRawScarto;
import it.mef.tm.elaboration.timb.service.FileTimbratureService;
import it.mef.tm.elaboration.timb.service.TimbratureService;
import it.mef.tm.elaboration.timb.util.ListUtility;
import it.mef.tm.elaboration.timb.util.StatoAcquisizioneTimbratureEnum;
import it.mef.tm.elaboration.timb.util.StringUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * FileTimbratureServiceImpl.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  17 mag 2024
 * @Description prima versione
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, noRollbackFor = NotRollBackException.class)
public class FileTimbratureServiceImpl implements FileTimbratureService {

	@Autowired
	private TracciatoTimbratureHRepository tracciatoTimbratureHRepository;

	@Autowired
	private TimbratureService timbratureService;

	@Autowired
	private MetricsLog metricsLog;
	
	@Override
	public TimbraturaEsitoLettura avviaCaricamentoManuale(Path filePath) throws PreconditionException {
		TimbraturaEsitoLettura result = new TimbraturaEsitoLettura();
		result.setTimbrAcquisite(new ArrayList<>());
		result.setRawScartate(new ArrayList<>());
		List<DTracciatoTimbratureH> tracciati = tracciatoTimbratureHRepository.findAll();
		if (tracciati.isEmpty()) {
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT02);
		}
		DTracciatoTimbratureH tracciato = ListUtility.getVersionIncludingDate(new Date(), tracciati);
		
		try {
			verificaFile(filePath, tracciato, result);
		} catch (NotRollBackException e) {
			acqTimbrRawErr(result, e.getCode());
			log.error(e.getMessage(), e);
		} catch (PreconditionException e) {
			acqTimbrRawErr(result, e.getCode());
			log.error(e.getMessage(), e);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
	}
	
	/**
	 * metodo per verificare la coerenza del file timbrature
	 * 
	 * @param filePath         file
	 * @param tracciato      tracciato con guida
	 * @param result
	 * @throws NotRollBackException
	 * @throws PreconditionException 
	 */
	private void verificaFile(Path filePath, DTracciatoTimbratureH tracciato, TimbraturaEsitoLettura result) throws NotRollBackException, PreconditionException, IOException {
		List<DTimbraturaFisicaRaw> rawTimbr = new ArrayList<>();
		List<DTimbraturaFisica> timbrAcquisite = result.getTimbrAcquisite();
		List<TimbraturaFisicaRawScarto> rawScartate = result.getRawScartate();
		verificaXml(filePath, rawTimbr, (DTraccTimbrXmlH) tracciato);
		if(rawTimbr.isEmpty()) {
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT05);
		}
		// il blocco try - catch tolto
		// rilancia semplicemnte le eccezioni
		verificaTimbratureRaw(rawTimbr, tracciato, filePath);
		
		this.timbratureService.normalizzaTimbrature(rawTimbr, timbrAcquisite, rawScartate, tracciato, filePath);
	}
	
	/**
	 * metodo per verificare che le timbrature siano
	 * correttamente valorizzate
	 * 
	 * @param rawTimbr
	 * @throws PreconditionException
	 */
	private void verificaTimbratureRaw(List<DTimbraturaFisicaRaw> rawTimbr, DTracciatoTimbratureH tracciato, Path filePath) throws PreconditionException, IOException {
		for(DTimbraturaFisicaRaw raw :  rawTimbr) {
			metricsLog.writeLogMetrics(filePath);
			verificaTimbraturaRaw(raw, tracciato);
		}	
	}
	
	/**
	 * metodo per verificare che la singola
	 * timbrature sia correttamente
	 * valorizzata
	 * 
	 * @param rawTimbr
	 * @throws PreconditionException
	 */
	private void verificaTimbraturaRaw(DTimbraturaFisicaRaw rawTimbr, DTracciatoTimbratureH tracciato) throws PreconditionException {
		if(StringUtility.isNullOrEmpty(rawTimbr.getDescCodiceBadge())
				|| StringUtility.isNullOrEmpty(rawTimbr.getDescData())
				|| StringUtility.isNullOrEmpty(rawTimbr.getDescOra()))
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT25);
		try {
			this.timbratureService.verifyDate(rawTimbr, tracciato);
		} catch (Exception e) {
			/**
			 * se trovo un'eccezione la converto
			 */
			throw new PreconditionException(e);
		}
	}

	/**
	 * metodo che genera l'acquisizione in errore del file
	 * 
	 * @param result                   file
	 * @param errCode                  codice errore
	 * @throws ServiceException 
	 */
	private void acqTimbrRawErr(TimbraturaEsitoLettura result, String errCode) throws PreconditionException {
		result.setStatoAcquisizioneFinale(StatoAcquisizioneTimbratureEnum.ACQ_TIMBRATURE_RAW_ERR);
		if(!StringUtility.isNullOrEmpty(errCode)) {
			result.setError(errCode);
		}
		
		throw new PreconditionException(errCode);
	}

	/**
	 * metodo per ottenere le righe da un file testuale
	 * 
	 * @param pathFinale         file
	 * @throws PreconditionException
	 */
	private List<String> getFileText(Path pathFinale) throws PreconditionException {
		List<String> listaRisultati = null;
		try {
			listaRisultati = Files.readAllLines(pathFinale);
		} catch (IOException e) {
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT03);
		}
		if (listaRisultati.isEmpty()) {
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT05);
		}
		return listaRisultati;
	}

	/**
	 * metodo per ottenere il tracciato xml da file
	 * 
	 * @param pathFinale         file
	 * @throws PreconditionException
	 */
	private Document getFileXml(Path pathFinale) throws PreconditionException {
		List<String> listaRisultati = getFileText(pathFinale);
		StringBuilder sb = new StringBuilder();
		boolean firstLine = true;
		for (String s : listaRisultati) {
			if (firstLine) {
				sb.append(removeUTF8BOM(s));
				firstLine = false;
			} else {
				sb.append(s);
			}
		}
		String tempXml = sb.toString();
		
		//NOSONAR
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(tempXml)));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT03);
		}
		return doc;
	}
	
	private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
	
	/**
	 * metodo per verificare un file xml
	 * @param filePath 
	 * 
	 * @param rawTimbr       timbrature grezze
	 * @param tracciato
	 * @throws NotRollBackException
	 * @throws PreconditionException 
	 */
	private void verificaXml(Path filePath, List<DTimbraturaFisicaRaw> rawTimbr, DTraccTimbrXmlH tracciato) throws NotRollBackException, PreconditionException, IOException {
		Document doc = getFileXml(filePath);
		NodeList node = doc.getElementsByTagName(tracciato.getDescTagTimbratura());
		if (node.getLength() == 0) {
			throw new PreconditionException(ERR_GFT_SERVICE_TMGFT05);
		}
		for (int i = 0; i < node.getLength(); i++) {
			metricsLog.writeLogMetrics(filePath);
			getSingleTimbrFromXml(rawTimbr, node.item(i), tracciato);
		}
	}

	/**
	 * metodo per verificare una singola timbratura xml
	 * 
	 * @param listaRaw       timbrature grezze
	 * @param node           nodo timbratura
	 * @param tracciato
	 * @throws NotRollBackException
	 */
	private void getSingleTimbrFromXml(List<DTimbraturaFisicaRaw> listaRaw, Node node,
			DTraccTimbrXmlH tracciato) throws NotRollBackException {
		try {
			DTimbraturaFisicaRaw raw = new DTimbraturaFisicaRaw();
			Element el = (Element) node;
			raw.setDescCodiceBadge(getXmlValueFromTag(el, tracciato.getDescTagCodiceBadge()));
			if (tracciato.getDescTagCodiceTimbratura() != null)
				raw.setDescCodiceTimbratura(getXmlValueFromTag(el, tracciato.getDescTagCodiceTimbratura()));
			if (tracciato.getDescTagCodiceLettore() != null)
				raw.setDescCodiceLettore(getXmlValueFromTag(el, tracciato.getDescTagCodiceLettore()));
			if (tracciato.getDescTagCodiceTestina() != null)
				raw.setDescCodiceTestina(getXmlValueFromTag(el, tracciato.getDescTagCodiceTestina()));
			if (tracciato.getDescTagCodiceConcentr() != null)
				raw.setDescCodiceConcentratore(getXmlValueFromTag(el, tracciato.getDescTagCodiceConcentr()));
			raw.setDescData(getXmlValueFromTag(el, tracciato.getDescTagData()));
			raw.setDescOra(getXmlValueFromTag(el, tracciato.getDescTagOra()));
			if (tracciato.getDescTagVerso() != null)
				raw.setDescVerso(getXmlValueFromTag(el, tracciato.getDescTagVerso()));
			raw.setDescCodiceCausale(getXmlValueFromTag(el, tracciato.getDescTagCodiceCausale()));
			listaRaw.add(raw);
		} catch (Exception ex) {
			throw new NotRollBackException(ex.getMessage(), ex);
		}
	}

	/**
	 * preleva il valore di un nodo dal tag
	 * 
	 * @param element elemento
	 * @param code    nome tag
	 */
	private String getXmlValueFromTag(Element element, String code) {
		return element.getElementsByTagName(code).getLength() > 0
				? element.getElementsByTagName(code).item(0).getTextContent()
				: null;
	}

}
