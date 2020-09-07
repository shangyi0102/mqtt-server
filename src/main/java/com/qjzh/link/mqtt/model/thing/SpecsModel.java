package com.qjzh.link.mqtt.model.thing;

/**
 * @DESC: 规格定义
 * @author LIU.ZHENXING
 * @date 2020年8月20日下午6:05:58
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class SpecsModel {
	// 最小值
	private String min;
	// 最大值
	private String max;
	// 单位
	private String unit;
	// 单位名称
	private String unitName;
	// 步长
	private String step;
	// 选择项
	private Optional optional;

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public Optional getOptional() {
		return optional;
	}

	public void setOptional(Optional optional) {
		this.optional = optional;
	}

}

class Optional {

	private String value;

	private String desc;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}