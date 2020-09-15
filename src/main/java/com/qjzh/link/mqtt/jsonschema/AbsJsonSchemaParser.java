package com.qjzh.link.mqtt.jsonschema;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.qjzh.link.mqtt.jsonschema.internal.JsonFormat;
import com.qjzh.link.mqtt.jsonschema.internal.JsonFormatSerializer;
import com.qjzh.link.mqtt.jsonschema.type.ObjectSchema;

/**
 * @DESC: Schema 解析器抽象类
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午7:00:16
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public abstract class AbsJsonSchemaParser {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ObjectSchema objectSchema;
	//系统必填属性
	protected final static List<String> sysRequiredFields = new ArrayList<String>();
	
	protected static ObjectMapper objectMapper;
	
	static{
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		SimpleModule jsonFormatModule = new SimpleModule();
		jsonFormatModule.addSerializer(JsonFormat.class, new JsonFormatSerializer());
        objectMapper.registerModule(jsonFormatModule);
        
	}
	
	protected void setObjectSchema(ObjectSchema objectSchema){
		this.objectSchema = objectSchema;
	};
	
	/**
	 * DESC: 获取 Json Schema定义字符串
	 * @return
	 */
	public String getStrSchema(){
		if (objectSchema == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(objectSchema);
		} catch (Exception e) {
			logger.error("转化JSON String Schema 异常", e);
			return null;
		}
	}

	
}
