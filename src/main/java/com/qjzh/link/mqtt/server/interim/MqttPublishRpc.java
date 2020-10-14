package com.qjzh.link.mqtt.server.interim;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjzh.link.mqtt.base.ErrorCode;
import com.qjzh.link.mqtt.base.IMqttNet;
import com.qjzh.link.mqtt.base.PublishRequest;
import com.qjzh.link.mqtt.base.PublishResponse;
import com.qjzh.link.mqtt.base.exception.MqttInvokeException;
import com.qjzh.link.mqtt.base.exception.MqttRpcException;
import com.qjzh.link.mqtt.base.exception.MqttTimeoutException;
import com.qjzh.link.mqtt.server.channel.IOnCallListener;
import com.qjzh.link.mqtt.server.response.GeneralPublishResponse;
import com.qjzh.link.mqtt.utils.MqttUtils;

/**
 * @DESC: mqtt发布同步
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:50:19
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttPublishRpc extends MqttPublish {

	private static final Logger logger = LoggerFactory.getLogger(MqttPublishRpc.class);

	public static final int DEFAULT_TIMEOUT = 5000;

	private String matchId;
	//响应体
	private volatile PublishResponse publishResponse; 
	
	private final Lock lock = new ReentrantLock();

	private final Condition done = lock.newCondition();
	
	//同步调用超时时间
	private final int timeout;
	//等待开始时间
	private long start;
	
	public MqttPublishRpc(IMqttNet mqttNet, PublishRequest publishRequest,
			IOnCallListener listener) {
		this(mqttNet, publishRequest, DEFAULT_TIMEOUT, listener);
	}
	
	public MqttPublishRpc(IMqttNet mqttNet, PublishRequest publishRequest, 
			int timeout, IOnCallListener listener) {
		super(mqttNet, publishRequest, listener);
		this.timeout = timeout;
		this.start = System.currentTimeMillis();
		this.matchId = MqttUtils.getMatchId(publishRequest.getReplyTopic(), publishRequest.getMsgId());
		MqttRpcExtractor.put(this);
		MqttRpcExtractor.signal();
	}

	public String getMatchId() {
		return matchId;
	}

	public boolean isTimeout(){
		return (System.currentTimeMillis() - start > timeout);
	}
	
	public PublishResponse sendRpc() throws MqttInvokeException {
		super.send();
		return this.get(timeout);
	}
	
	private PublishResponse get(int timeout){
		if (timeout <= 0) {
			timeout = this.timeout;
		}
		try {
			this.start = System.currentTimeMillis();
			lock.lock();
			try {
				while (!isFinish()) {
					done.await(timeout, TimeUnit.MILLISECONDS);
					if (isFinish() || System.currentTimeMillis() - this.start > timeout) {
						break;
					}
				}
			} catch (InterruptedException e) {
				throw new MqttRpcException(ErrorCode.RPC_CLIENT_INTERRUPT, "rpc lock Interrupted");
			} finally {
				lock.unlock();
			}
			
			if (!isFinish()) {
				throw new MqttTimeoutException("Waiting client-side response timeout");
			}
		} catch (MqttRpcException ex) {
			publishResponse = new GeneralPublishResponse();
			publishResponse.setStatus(ex.getCode());
			publishResponse.setErrorMsg(ex.getMessage());
		}finally {
			MqttRpcExtractor.remove(matchId);
		}
		
		return publishResponse;
	}

	public PublishResponse cancel() {
		lock.lock();
		try {
			publishResponse = new GeneralPublishResponse();
			publishResponse.setStatus(ErrorCode.RPC_SERVER_CANCEL);
			publishResponse.setErrorMsg("waiting is cancel of server!");
			
			done.signal();
			
			MqttRpcExtractor.remove(matchId);
		} finally {
			lock.unlock();
		}
		return publishResponse;
	}
	
	private void doReceived(PublishResponse publishResponse) {
		lock.lock();
		try {
			this.publishResponse = publishResponse;
			done.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isFinish(){
		return (null != this.publishResponse);
	}
	
	public static void received(PublishResponse publishResponse) { 
		String matchId = MqttUtils.getMatchId(publishResponse.getReplyTopic(), publishResponse.getMsgId());
		MqttPublishRpc publishRpc = MqttRpcExtractor.remove(matchId);
		if (publishRpc == null) {
			logger.warn("match fali!, matchId = <{}>, The timeout response finally, response={}", matchId, publishResponse.getData());
			return;
		} 
		
		logger.info("match succ, matchId = <{}>", matchId);
		publishRpc.doReceived(publishResponse);
	}
	
}

