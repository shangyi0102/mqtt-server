package com.qjzh.link.mqtt;

import com.qjzh.link.mqtt.base.INet;
import com.qjzh.link.mqtt.base.Constants;
import com.qjzh.link.mqtt.server.MqttInitParams;
import com.qjzh.link.mqtt.server.MqttNet;
import com.qjzh.link.mqtt.server.callback.ReplyMessageListener;
import com.qjzh.link.mqtt.server.request.ReportSubscribeRequest;

public class Test {

	public static void main(String[] args) throws InterruptedException {  
		
		String suffix = String.format("%04d", 2);
		String name = "A" + suffix;
		MqttInitParams mqttConfig = new MqttInitParams(new String[]{"tcp://192.168.49.91:1883"}, "qjzh2020", "qjzh2020");
		//initParams.setProductKey(name);
		
		INet mqttNet = new MqttNet(mqttConfig);
		mqttNet.init();
		
		String replyTopic = Constants.PROP_REPORT.replace(Constants.REPLACE_PRODUCTID, name).replace(Constants.REPLACE_DEVICEID, suffix);
		
		ReportSubscribeRequest subscribeRequest = new ReportSubscribeRequest();
		subscribeRequest.setTopic(replyTopic);
		subscribeRequest.setQos(2);
		
		mqttNet.subscribe(subscribeRequest);
		
		/*RequestModel<PropGet> model = new RequestModel<>();
		model.setMsgId("UID:"+QjzhAtomicInteger.incre());
		model.setTimestamp(System.currentTimeMillis());
		
		PropGet params = new PropGet();
		params.setProperties(Arrays.asList("sn","status"));
		model.setParams(params);
		mqttRequest.setPayload(model);*/
			
		
	}
	
}
