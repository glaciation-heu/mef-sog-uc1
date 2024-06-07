package it.mef.tm.scheduled.client.costants;

public class Costants {

	/** SISTEMA_PROTOCOLLO_HTTP */
	public static final String SISTEMA_PROTOCOLLO_HTTP = "HTTP";
	
	/** TYPE API*/
	private static final String TYPE_API = "API type: ";
	private static final String TYPE_OPERATION = "OPERATION ";
	private static final String TYPE_ORCHESTRATION = "ORCHESTRATION";
	public static final String TYPE_API_OPERATION = TYPE_API + TYPE_OPERATION;
	public static final String TYPE_API_OPERATION_ORCHESTRATION = TYPE_API + TYPE_OPERATION +"& "+ TYPE_ORCHESTRATION;
	
	/** url base */
	public static final String URL_BASE = "api.noipa.it/sec/";
	public static final String URL_VERSION = "V1";
	
    public static final String URL_SERVIZI_GFT_JOB = "workload";
    public static final String URL_SERVIZI_GFT_FILE = "file/timestamps";
    public static final String URL_SERVIZI_GFT_UPLOAD_FILE = URL_VERSION + "/upload";
    public static final String URL_SERVIZI_GFT_START = URL_VERSION + "/startWorkload";
    public static final String URL_SERVIZI_GFT_STOP = URL_VERSION + "/stopWorkload";

}
