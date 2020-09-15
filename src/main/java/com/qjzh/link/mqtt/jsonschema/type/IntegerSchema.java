package com.qjzh.link.mqtt.jsonschema.type;


/**
 * @DESC: Integer类型
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午5:58:51
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class IntegerSchema extends AbsSchema {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1704071330747132873L;
	//最小整数
	private Integer minimum;
	//最大整数
	private Integer maximum;
	//排除最小整数
	private Integer exclusiveMinimum;
	//排除最大整数
	private Integer exclusiveMaximum;
	
	public IntegerSchema() {
		setType("integer");
	}

	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
		this.put("minimum", minimum);
	}

	public Integer getMaximum() {
		return maximum;
	}

	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
		this.put("maximum", maximum);
	}

	public Integer getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	public void setExclusiveMinimum(Integer exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
		this.put("exclusiveMinimum", exclusiveMinimum);
	}

	public Integer getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	public void setExclusiveMaximum(Integer exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
		this.put("exclusiveMaximum", exclusiveMaximum);
	}
	
	
}
