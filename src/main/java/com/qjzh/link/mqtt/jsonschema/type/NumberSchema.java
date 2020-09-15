package com.qjzh.link.mqtt.jsonschema.type;


import java.math.BigDecimal;

/**
 * @DESC: Number类型
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午5:58:23
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class NumberSchema extends AbsSchema{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2400981725610285354L;
	//最小数字
	private BigDecimal minimum;
	//最大数字
	private BigDecimal maximum;
	//排除最小数字
	private BigDecimal exclusiveMinimum;
	//排除最大数字
	private BigDecimal exclusiveMaximum;
	
	public NumberSchema() {
		setType("number");
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
		this.put("minimum", minimum);
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
		this.put("maximum", maximum);
	}

	public BigDecimal getExclusiveMinimum() {
		return exclusiveMinimum;
	}

	public void setExclusiveMinimum(BigDecimal exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
		this.put("exclusiveMinimum", exclusiveMinimum);
	}

	public BigDecimal getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	public void setExclusiveMaximum(BigDecimal exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
		this.put("exclusiveMaximum", exclusiveMaximum);
	}
	
}
