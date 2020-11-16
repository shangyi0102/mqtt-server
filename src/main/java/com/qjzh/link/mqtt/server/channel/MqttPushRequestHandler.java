package com.qjzh.link.mqtt.server.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.qjzh.link.mqtt.model.device.DeviceSubject;
import com.qjzh.link.mqtt.server.callback.PushRequest;

@Component
public class MqttPushRequestHandler implements IPushRequestHandler{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private AmqpTemplate amqpTemplate;
	
	@Override
	public JSONObject onCommand(PushRequest pushRequest) throws Exception {
		String tenandIdent = pushRequest.getTenandIdent();
		String appIdent = pushRequest.getAppIdent();
		String prodIdent = pushRequest.getProdIdent();
		DeviceSubject devSubject = pushRequest.getDevSubject();
		//拼接交换机名称
		String exchangeName =  "." + tenandIdent + "." + appIdent + "." + prodIdent; 
		
		amqpTemplate.convertAndSend(exchangeName, null, devSubject);
		
		logger.info("发送rabbitmq设备信息 --->{}", devSubject);
		return null;
	}

}
