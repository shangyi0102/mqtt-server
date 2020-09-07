package com.qjzh.link.mqtt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.slf4j.LoggerFactory;

public class MqttLogger implements Logger {
	
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	public static boolean isLoggable = false;

	public void initialise(ResourceBundle messageCatalog, String loggerID, String resourceName) {
		logger.info("MqttPaho initialise，loggerId = " + loggerID + " , name = " + resourceName);
	}

	public void setResourceName(String logContext) {
		logger.info("MqttPaho setResourceName(), {}", logContext);
	}

	public boolean isLoggable(int level) {
		return isLoggable;
	}

	public void severe(String sourceClass, String sourceMethod, String msg) {
		toALog("severe", sourceClass, sourceMethod, msg);
	}

	public void severe(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("severe", sourceClass, sourceMethod, msg, inserts);
	}

	public void severe(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
		toALog("severe", sourceClass, sourceMethod, msg, inserts);
	}

	public void warning(String sourceClass, String sourceMethod, String msg) {
		toALog("warning", sourceClass, sourceMethod, msg);
	}

	public void warning(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("warning", sourceClass, sourceMethod, msg, inserts);
	}

	public void warning(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
		toALog("warning", sourceClass, sourceMethod, msg, inserts);
	}

	public void info(String sourceClass, String sourceMethod, String msg) {
		toALog("info", sourceClass, sourceMethod, msg);
	}

	public void info(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("info", sourceClass, sourceMethod, msg, inserts);
	}

	public void info(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
		toALog("info", sourceClass, sourceMethod, msg, inserts);
	}

	public void config(String sourceClass, String sourceMethod, String msg) {
		toALog("config", sourceClass, sourceMethod, msg);
	}

	public void config(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("config", sourceClass, sourceMethod, msg, inserts);
	}

	public void config(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable thrown) {
		toALog("config", sourceClass, sourceMethod, msg, inserts);
	}

	public void fine(String sourceClass, String sourceMethod, String msg) {
		toALog("fine", sourceClass, sourceMethod, msg);
	}

	public void fine(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("fine", sourceClass, sourceMethod, msg, inserts);
	}

	public void fine(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
		toALog("fine", sourceClass, sourceMethod, msg, inserts);
	}

	public void finer(String sourceClass, String sourceMethod, String msg) {
		toALog("finer", sourceClass, sourceMethod, msg);
	}

	public void finer(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("finer", sourceClass, sourceMethod, msg, inserts);
	}

	public void finer(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
		toALog("finer", sourceClass, sourceMethod, msg, inserts);
	}

	public void finest(String sourceClass, String sourceMethod, String msg) {
		toALog("finest", sourceClass, sourceMethod, msg);
	}

	public void finest(String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		toALog("finest", sourceClass, sourceMethod, msg, inserts);
	}

	public void finest(String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
		toALog("finest", sourceClass, sourceMethod, msg, inserts);
	}

	public void log(int level, String sourceClass, String sourceMethod, String msg, Object[] inserts,
			Throwable thrown) {
		toALog("log " + level, sourceClass, sourceMethod, msg, inserts);
	}

	public void trace(int level, String sourceClass, String sourceMethod, String msg, Object[] inserts, Throwable ex) {
		toALog("trace", sourceClass, sourceMethod, msg, inserts);
	}

	public String formatMessage(String msg, Object[] inserts) {
		return msg;
	}

	public void dumpTrace() {
		logger.info("dumpTrace()");
	}

	private void toALog(String logLevel, String sourceClass, String sourceMethod, String msg) {
		if (!isLoggable)
			return;
		print("MqttPaho", logLevel + ", c= " + sourceClass + " , method = " + sourceMethod + " , msg = " + msg);
	}

	private void toALog(String logLevel, String sourceClass, String sourceMethod, String msg, Object[] inserts) {
		if (!isLoggable) {
			return;
		}
		String parameters = "[";
		if (inserts != null && inserts.length > 0)
			for (Object o : inserts) {
				if (o != null) {
					parameters = parameters + o.toString() + ", ";
				}
			}
		parameters = parameters + "]";

		print("MqttPaho", logLevel + ", c= " + sourceClass + " , method = " + sourceMethod + " , msg = " + msg
				+ "params:" + parameters);
	}

	private void print(String tag, String msg) {
		String source = null;

		try {
			StackTraceElement st = Thread.currentThread().getStackTrace()[4];

			source = "[" + st.getFileName() + "] - " + st.getMethodName() + "(" + st.getLineNumber() + ")-("
					+ Thread.currentThread().getId() + ")";
		} catch (Exception var4) {
		}

		logger.info(fm.format(new Date()) + " - " + source + ":" + tag + ":" + msg);
	}
}

