package com.qjzh.link.mqtt.model.product;


import java.util.HashMap;
import java.util.Map;

/**
 * @DESC: 产品设备类型
 * @author LIU.ZHENXING
 * @date 2020年3月20日下午5:27:15
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public enum DevType {

	GATEWAY("GATEWAY"),
	DEVICE("DEVICE"),
	SUB_DEVICE("SUB_DEVICE");
	
	
	private static Map<String, DevType> constants = new HashMap<String, DevType>();

    static {
        for (DevType rt : values()) {
            constants.put(rt.code, rt);
        }
    }
	
	private String code;
	
	DevType(String code){
		this.code = code;
	}
	
	public static DevType forCode(String code){
		return constants.get(code);
	}
	
}
