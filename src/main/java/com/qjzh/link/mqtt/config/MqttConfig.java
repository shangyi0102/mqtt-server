package com.qjzh.link.mqtt.config;


import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.qjzh.link.mqtt.server.MqttInitParams;
import com.qjzh.link.mqtt.server.MqttNet;
import com.qjzh.link.mqtt.server.ThingsInitData;

/**
 * @DESC: MQTT 启动 配置类
 * @author LIU.W
 * @date 2017年7月20日下午9:27:08
 * @version 1.0.0
 * @since
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttConfig {

	@Autowired
	private MqttProperties mqttProperties;

	@Bean
	public ThreadPoolTaskScheduler taskScheduler(){
		ThreadPoolTaskScheduler threadPoolTask = new ThreadPoolTaskScheduler();
		threadPoolTask.setPoolSize(10);
		threadPoolTask.setThreadNamePrefix("task-scheduler-");
		threadPoolTask.setRejectedExecutionHandler(new CallerRunsPolicy());
		
		return threadPoolTask;
	}
	
	@Bean
	public MqttInitParams mqttInitParams(){
		MqttInitParams mqttConfig = new MqttInitParams(mqttProperties.getUrls(), 
				mqttProperties.getUsername(), mqttProperties.getPassword());
		mqttConfig.setClientMark("things");
		mqttConfig.setTimeToWait(mqttProperties.getTimeToWait());
		mqttConfig.setMaxInflight(mqttProperties.getMaxInflight());
		return mqttConfig;
	}
	
	@Bean
	public MqttNet mqttNet(MqttInitParams mqttInitParams) {
		MqttNet mqttNet = new MqttNet(mqttInitParams);
		mqttNet.init();
		return mqttNet;
	}
	
	@Bean
	public ThingsInitData thingsInitData(){
		return new ThingsInitData("+", "+", "+", "+", mqttProperties.getQos());
	}
	
}
