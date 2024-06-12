package it.mef.tm.scheduled.client.util;

import java.util.concurrent.ScheduledFuture;

/**
 * ScheduledUtil.java
 * 
 * @Change @history
 * @version     1.0
 * @DateUpdate  11 giu 2024
 * @Description prima versione
 */
public class ScheduledUtil {

    private static ScheduledFuture<?> scheduledFuture;
    
    private ScheduledUtil() {}
    
    public static void storeScheduledFuture(ScheduledFuture<?> scheduledFuture) {
    	ScheduledUtil.scheduledFuture = scheduledFuture;
    }

	/**
	 * Verifica che la schedulazione sia attiva
	 * verificando scheduledFuture <> null AND scheduledFuture.isCancelled <> true
	 * AND scheduledFuture.isDone <> true
	 * @return
	 */
	public static boolean stopScheduledFuture() {
		return scheduledFuture.cancel(true);
	}
	
	/**
	 * Verifica che la schedulazione sia attiva
	 * verificando scheduledFuture <> null AND scheduledFuture.isCancelled <> true
	 * AND scheduledFuture.isDone <> true
	 * @return
	 */
	public static boolean isScheduledFutureActive() {
		return scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone();
	}
}
