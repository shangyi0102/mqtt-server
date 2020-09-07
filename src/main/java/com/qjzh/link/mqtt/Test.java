package com.qjzh.link.mqtt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		
		String perfix = "A";
		
		Lock lock = new ReentrantLock();
		Condition done = lock.newCondition();
		
		lock.lock();
		
		try {
			long start = System.currentTimeMillis();
            done.await(5000, TimeUnit.MILLISECONDS);
            if (isDone() || System.currentTimeMillis() - start > 5000) {
                System.out.println("timeout");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
		
		/*for (int i = 0; i < 800; i++) {
			String suffix = String.format("%04d", i);
			String name = perfix + suffix;
			MqttConfig mqttConfig = new MqttConfig(new String[]{"tcp://192.168.49.91:1883"}, "qjzh2020", "qjzh2020");
			//initParams.setProductKey(name);
			
			INet mqttNet = new MqttNet(mqttConfig);
			
			Thread.sleep(300);
			
			MqttPublishRequest mqttRequest = new MqttPublishRequest();
			mqttRequest.setTopic(DMConstants.PROP_GET.replace(DMConstants.REPLACE_PRODUCTID, name).replace(DMConstants.REPLACE_DEVICEID, suffix));
			//request.replyTopic = DMConstants.TSL_GET_REPLY.replace("{productKey}", info.productKey).replace("{deviceName}", info.deviceName);
			mqttRequest.setRPC(true);
			RequestModel<PropGet> model = new RequestModel<>();
			model.setMsgId("UID:"+QjzhAtomicInteger.incre());
			model.setTimestamp(System.currentTimeMillis());
			
			PropGet params = new PropGet();
			params.setProperties(Arrays.asList("sn","status"));
			model.setParams(params);
			
			mqttRequest.setPayload(model);
			
			mqttNet.asyncSend(mqttRequest, new IOnCallListener() {
				
				public void onSuccess(QJRequest request, QJResponse response) {
					System.out.println(request.toString());
				}
				
				public void onFailed(QJRequest aRequest, QJError aError) {
					System.out.println("asyncSend发送失败!");
					if (sendListener == null)
					return;
					sendListener.onFailure(request, ClassSwitchHelper.aErrorChannelToCmp(aError));
				}
				
				public boolean needUISafety() {
					return true;
				}
			});
		}*/
		
	}
	
	public static boolean isDone() {
        return false;
    }
	
}
