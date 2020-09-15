package com.qjzh.link.mqtt.jsonschema.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @DESC: Object Schema
 * @author LIU.ZHENXING
 * @date 2020年2月21日上午10:54:26
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class ObjectSchema extends AbsSchema{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -681344785453111609L;
	
	//协议版本
	private String $schema = "http://json-schema.org/draft-07/schema#";
	//属性类型
	private Map<String, AbsSchema> properties = new HashMap<>();
	
	private List<String> required = new ArrayList<String>();
	//是否允许额外属性
	private Boolean additionalProperties;
	//最小属性个数
	private Integer minProperties;
	//最大属性个数
	private Integer maxProperties;
	
	
	public ObjectSchema(String title) {
		setType("object");
		setTitle(title);
		this.put("$schema", $schema);
		this.put("properties", properties);
		this.put("required", required);
	}

	public void addProperties(String filed, AbsSchema attr, boolean required) {
		properties.put(filed, attr);
		if (required) {
			this.required.add(filed);
		}
	}
	
	public Map<String, AbsSchema> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, AbsSchema> properties) {
		this.properties.putAll(properties);
		this.put("properties", this.properties);
	}

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(List<String> required) {
		this.required.addAll(required);
		this.put("required", this.required);
	}

	public String get$schema() {
		return $schema;
	}

	public void set$schema(String $schema) {
		this.$schema = $schema;
		this.put("$schema", $schema);
	}

	public Boolean getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Boolean additionalProperties) {
		this.additionalProperties = additionalProperties;
		this.put("additionalProperties", this.additionalProperties);
	}

	public Integer getMinProperties() {
		return minProperties;
	}

	public void setMinProperties(Integer minProperties) {
		this.minProperties = minProperties;
		this.put("minProperties", this.minProperties);
	}

	public Integer getMaxProperties() {
		return maxProperties;
	}

	public void setMaxProperties(Integer maxProperties) {
		this.maxProperties = maxProperties;
		this.put("maxProperties", this.maxProperties);
	}
	

}
