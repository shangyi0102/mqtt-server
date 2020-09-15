package com.qjzh.link.mqtt.jsonschema.type;


/**
 * @DESC: Enum 类型
 * @author LIU.ZHENXING
 * @date 2020年2月25日下午5:59:16
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class EnumSchema<T> extends AbsSchema{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7460640771267987118L;

	public EnumSchema(T[] t) {
		if(t == null || t.length == 0){
			throw new IllegalArgumentException("t 参数不能为空");
		}
		if (t[0] instanceof Integer) {
			setType("number");
		}else{
			setType("string");
		}
		this.put("enum", t);
	}
	
	

}
