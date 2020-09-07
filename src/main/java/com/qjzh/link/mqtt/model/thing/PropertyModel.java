package com.qjzh.link.mqtt.model.thing;

/**
 * @DESC: 属性定义
 * @author LIU.ZHENXING
 * @date 2020年8月20日下午5:14:51
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class PropertyModel {
	//无
	private String thingIdentifier;
	// 属性ID
	private String identifier;
	// 属性名称
	private String name;
	// 访问模式，ro/rw
	private String accessMode;
	// 是否必须，true/false
	private String required;
	// 数据类型
	private String type;
	// 规格
	private SpecsModel specs;

	public String getThingIdentifier() {
		return thingIdentifier;
	}

	public void setThingIdentifier(String thingIdentifier) {
		this.thingIdentifier = thingIdentifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SpecsModel getSpecs() {
		return specs;
	}

	public void setSpecs(SpecsModel specs) {
		this.specs = specs;
	}

}
