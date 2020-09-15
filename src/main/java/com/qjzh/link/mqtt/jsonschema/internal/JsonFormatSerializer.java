package com.qjzh.link.mqtt.jsonschema.internal;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
/**
 * @DESC: JsonFormat 序列化
 * @author LIU.ZHENXING
 * @date 2020年2月24日下午6:11:28
 * @version 1.0.0
 * @copyright www.7gwifi.com
 */
public class JsonFormatSerializer extends JsonSerializer<JsonFormat>{

	@Override
	public void serialize(JsonFormat arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
		arg1.writeString(arg0.getCode());
	}
	
}