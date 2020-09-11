package com.qjzh.link.mqtt.base;

public class Constants {
	
  public static final String REPLACE_PRODUCTID = "{productId}";
  
  public static final String REPLACE_DEVICEID = "{deviceId}";
  
  public static final String REPLACE_EVENTID = "{eventId}";
  
  public static final String REPLACE_SERVICEID = "{serviceId}";
  
  public static String REPLY_STR = "reply";
  
  public static String PROP_READ = "/device/{productId}/{deviceId}/property/read";
  
  public static String PROP_READ_REPLY = "/device/{productId}/{deviceId}/property/read_reply";
  
  public static String PROP_SET = "/device/{productId}/{deviceId}/property/set";
  
  public static String PROP_SET_REPLY = "/device/{productId}/{deviceId}/property/set_reply";
  
  public static String PROP_REPORT = "/device/{productId}/{deviceId}/property/report";
  
  public static String EVENT_REPORT = "/device/{productId}/{deviceId}/event/{eventId}/report";
  
  public static String SERVICE_INVOKE = "/device/{productId}/{deviceId}/service/{serviceId}/invoke";
  
  public static String SERVICE_INVOKE_REPLY = "/device/{productId}/{deviceId}/service/{serviceId}/invoke_reply";
  
  
}
