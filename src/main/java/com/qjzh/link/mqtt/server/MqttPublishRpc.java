package com.qjzh.link.mqtt.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panda.mqtt.remote.MqttFuture;
import com.panda.mqtt.remote.MqttResponse;
import com.qjzh.link.mqtt.base.AbsMqttFeed;
import com.qjzh.link.mqtt.base.IOnCallListener;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.QJError;
import com.qjzh.link.mqtt.exception.BadNetworkException;
import com.qjzh.link.mqtt.exception.MqttRpcException;
import com.qjzh.link.mqtt.exception.MqttTimeoutException;
import com.qjzh.link.mqtt.server.request.GeneralPublishRequest;
import com.qjzh.link.mqtt.server.response.GeneralPublishResponse;

/**
 * @DESC: mqtt发送者
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublishRpc extends MqttPublish {
	

	public static final int DEFAULT_TIMEOUT = 5000;

	public static final Map<String, MqttPublishRpc> FUTURES = new ConcurrentHashMap<>();

	private final Lock lock = new ReentrantLock();

	private final Condition done = lock.newCondition();
	//同步调用超时时间
	private final int timeout ;
	//等待开始时间
	private final long start = System.currentTimeMillis();
	
	public MqttPublishRpc(MqttNet mqttNet, PublishRequest publishRequest, 
			IOnCallListener listener) {
		this(mqttNet, publishRequest, DEFAULT_TIMEOUT, listener);
	}
	
	public MqttPublishRpc(MqttNet mqttNet, PublishRequest publishRequest, 
			int timeout, IOnCallListener listener) {
		super(mqttNet, publishRequest, listener);
		this.timeout = timeout;
	}

	public void setStatus(MqttStatus status) {
		this.status = status;
	}

	public MqttStatus getStatus() {
		return (MqttStatus)status;
	}
	
	public PublishResponse get() throws MqttRpcException {
		return get(timeout);
	}

	public PublishResponse get(int timeout) throws MqttRpcException {
		if (timeout <= 0) {
			timeout = DEFAULT_TIMEOUT;
		}
		if (!isFinish()) {
			long start = System.currentTimeMillis();
			lock.lock();
			try {
				while (!isFinish()) {
					done.await(timeout, TimeUnit.MILLISECONDS);
					if (isFinish() || System.currentTimeMillis() - start > timeout) {
						break;
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
			if (!isFinish()) {
				throw new MqttTimeoutException("Waiting client-side response timeout");
			}
		}
		if (getPublishResponse() == null) {
			throw new IllegalStateException("response cannot be null");
		}
		
		return getPublishResponse();
	}

	public void cancel() {
		GeneralPublishResponse errorResponse = new GeneralPublishResponse();
		errorResponse.setStatus(GeneralPublishResponse.CANCEL);
		setPublishResponse(errorResponse);
		FUTURES.remove(getPublishResponse().getMsgId());
	}
	
	public boolean isFinish(){
		return (null != getPublishResponse());
	}
	
	public static void received(PublishResponse publishResponse) {
		MqttFuture future = FUTURES.remove(response.getMId());
		if (future != null) {
			future.doReceived(response);
		} else {
			log.warn("The timeout response finally returned at " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
					.format(new Date())) + ", response " + response);
		}
	}
	
}

