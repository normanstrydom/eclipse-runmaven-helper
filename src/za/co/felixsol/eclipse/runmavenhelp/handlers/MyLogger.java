package za.co.felixsol.eclipse.runmavenhelp.handlers;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MyLogger {
    
    public static void logInfo(String message) {
//        ILog log = Activator.getDefault().getLog();
//        log.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
    	System.out.println(message);
    }

    public static void logError(String message, Throwable exception) {
        ILog log = Activator.getDefault().getLog();
        log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, exception));
    }

    public static void logDebug(String message) {
        if (isDebugEnabled()) {
            logInfo("[DEBUG] " + message);
        }
    }

    private static boolean isDebugEnabled() {
        // You can control this using plugin preferences or system properties
        return Boolean.getBoolean("myplugin.debug");
    }
}
