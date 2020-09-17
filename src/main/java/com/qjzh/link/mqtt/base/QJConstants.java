package com.qjzh.link.mqtt.base;

public class QJConstants {
	
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
  
  
  public static final String TENANT_LIST_KEY = "TENANT_LIST";
  
  
  /**
	 * DESC: 获取应用redis路径
	 * @param tenantCode
	 * @return
	 */
	public static String getAppPath(String tenantCode){
		
		return "APP:" + tenantCode ;
	}
	
	/**
	 * DESC: 获取产品redis路径
	 * @param tenantCode
	 * @param appCode
	 * @return
	 */
	public static String getProductPath(String tenantCode, String appCode){
		
		return "PROD:" + tenantCode + ":" + appCode ;
	}
	
	/**
	 * DESC: 获取设备redis路径
	 * @param tenantCode
	 * @param appCode
	 * @param prodCode
	 * @return
	 */
	public static String getDevicePath(String tenantCode, 
			String appCode, String prodCode){
		
		return "DEV:" + tenantCode + ":" + appCode + ":" + prodCode;
	}
	
	
	/**
	 * DESC: 生成产品关键路径
	 * @param tenantCode
	 * @param appCode
	 * @param prodCode
	 * @return
	 */
	public static String getPordKeyPath(String tenantCode, 
			String appCode, String prodCode){
		return tenantCode + "&" + appCode + "&" + prodCode;
	}
	
	/**
	 * DESC: 生成产品关键路径
	 * @param tenantCode
	 * @param appCode
	 * @param prodCode
	 * @return
	 */
	public static String getDevKeyPath(String prodCode, String devSn){
		return prodCode + "&" + devSn ;
	}
  
}
