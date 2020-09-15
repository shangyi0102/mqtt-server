package com.qjzh.link.mqtt.model.product;


import java.io.Serializable;
import java.util.List;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.qjzh.link.mqtt.jsonschema.ProductJsonSchemaParser;
import com.qjzh.link.mqtt.jsonschema.validator.DateTimeValidator;

/**
 * @DESC: 应用-产品信息
 * @author LIU.ZHENXING
 * @date 2020年2月3日下午4:56:17
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class Product implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7865490790725860476L;
	
	//租户唯一编码
	private String tenantCode;
	//应用唯一编码
	private String appCode;
	//产品唯一编码
	private String prodCode;
	// String JSON Schema
	private String jsonSchema;
	// JSON Schema 
	private Schema schema;
	
	//产品SN 自定义Key
	private String devSnKey;
	//必填字段数组
	private List<String> requiredFields;
	
	//必填字段个数(包括用户自定义字段,系统内置字段)
	private Integer requiredFieldsCount = 0;
	
	//产品数据结构
	private ProductMeta struct;
	
	public Product(String tenantCode, String appCode, String prodCode, ProductMeta struct){
		this.tenantCode = tenantCode;
		this.appCode = appCode;
		this.prodCode = prodCode;
		this.struct = struct;
		ProductJsonSchemaParser prodJsonSchema = ProductJsonSchemaParser.builder(this.struct);
		this.jsonSchema = prodJsonSchema.getStrSchema();
		this.devSnKey = prodJsonSchema.getDevSNKey();
		this.requiredFieldsCount = prodJsonSchema.getRequiredFieldCount();
		//初始化 JSON Schema
		JSONObject rawSchema = new JSONObject(new JSONTokener(this.jsonSchema));
		SchemaLoader schemaLoader = SchemaLoader.builder()
    			.schemaJson(rawSchema) 
    			.addFormatValidator(new DateTimeValidator())
    			.build();
		this.schema = schemaLoader.load().build();
		
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public ProductMeta getStruct() {
		return struct;
	}

	public void setStruct(ProductMeta struct) {
		this.struct = struct;
	}

	public String getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(String jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public String getDevSnKey() {
		return devSnKey;
	}

	public void setDevSnKey(String devSnKey) {
		this.devSnKey = devSnKey;
	}

	public String getMIMEKey() {
		return devSnKey;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public List<String> getRequiredFields() {
		return requiredFields;
	}

	public void setRequiredFields(List<String> requiredFields) {
		this.requiredFields = requiredFields;
	}

	public Integer getRequiredFieldsCount() {
		return requiredFieldsCount;
	}

}
