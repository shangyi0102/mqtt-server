package com.qjzh.link.mqtt;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.qjzh.link.mqtt.server.MqttInitParams;
import com.qjzh.link.mqtt.server.PublishMqttNet;

public class Test {

	public static void main(String[] args) throws InterruptedException {  
		
		String suffix = String.format("%04d", 2);
		String name = "A" + suffix;
		MqttInitParams mqttConfig = new MqttInitParams(new String[]{"tcp://192.168.49.10:1883"}, "qjzh2020", "qjzh2020");
		//initParams.setProductKey(name);
		
		/*INet net = new MqttNet("client", mqttConfig);
		net.init();
		
		String replyTopic = QJConstants.PROP_REPORT.replace(QJConstants.REPLACE_PRODUCTID, name).replace(QJConstants.REPLACE_DEVICEID, suffix);
		
		ReportSubscribeRequest subscribeRequest = new ReportSubscribeRequest();
		subscribeRequest.setTopic(replyTopic);
		subscribeRequest.setQos(2);*/
		
		
		ThreadPoolTaskScheduler threadPoolTask = new ThreadPoolTaskScheduler();
		threadPoolTask.initialize();
		threadPoolTask.setPoolSize(10);
		threadPoolTask.setThreadNamePrefix("task-scheduler-");
		threadPoolTask.setRejectedExecutionHandler(new CallerRunsPolicy());
		
		PublishMqttNet publishMqttNet = new PublishMqttNet(mqttConfig, threadPoolTask);
		
		try {
			publishMqttNet.connect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*MqttNet mqttNet = (MqttNet)net;
		
		IMqttAsyncClient mqttAsyncClient = mqttNet.getClient();*/
		
		/*try {
			mqttAsyncClient.subscribe(replyTopic, 2).waitForCompletion(5000);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//mqttNet.subscribe(subscribeRequest);
		
		/*RequestModel<PropGet> model = new RequestModel<>();
		model.setMsgId("UID:"+QjzhAtomicInteger.incre());
		model.setTimestamp(System.currentTimeMillis());
		
		PropGet params = new PropGet();
		params.setProperties(Arrays.asList("sn","status"));
		model.setParams(params);
		mqttRequest.setPayload(model);*/
			
		
	}
	
}
