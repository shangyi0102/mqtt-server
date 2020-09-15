package com.qjzh.link.mqtt.jsonschema.type;

import com.qjzh.link.mqtt.jsonschema.internal.JsonFormat;
/**
 * @DESC: String类型
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午5:57:53
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class StringSchema extends AbsSchema {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3128979359655962841L;
	//最小长度
	private Integer minLength;
	//最大长度
	private Integer maxLength;
	//格式化类型
	private JsonFormat format;
	
	public StringSchema() {
		setType("string");
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
		this.put("minLength", minLength);
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		this.put("maxLength", maxLength);
	}

	public JsonFormat getFormat() {
		return format;
	}

	public void setFormat(JsonFormat format) {
		this.format = format;
		this.put("format", format);
	}
	
}
