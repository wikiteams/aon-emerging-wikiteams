package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PjiitOutputter {
	
	public static void say(String s) {
		String dateStringRepresentation = getDateLogs();
		System.out.println(dateStringRepresentation + ": " + s);
		PjiitLogger.info(dateStringRepresentation + ": " + s);
	}
	
	public static void sanity(String s) {
		String dateStringRepresentation = getDateLogs();
		System.out.println(dateStringRepresentation + ": " + s);
		SanityLogger.sanity(dateStringRepresentation + ": " + s);
	}

	private static String getDateLogs() {
		return new SimpleDateFormat("DD/MM/yyyy HH:mm").format(new Date());
	}
}
