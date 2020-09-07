package com.qjzh.link.mqtt.channel;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @DESC: 事件通知分发器
 * @author LIU.ZHENXING
 * @date 2020年8月19日下午2:43:15
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttEventDispatcher {
	
	private final static Logger logger = LoggerFactory.getLogger(MqttEventDispatcher.class);

	public static final byte MSG_CONNECTED = 1;

	public static final byte MSG_DISCONNECT = 2;
	
	public static final byte MSG_CONNECT_FAIL = 7;
	
	public static final byte MSG_RECEIVECMD = 3;
	
	public static final byte MSG_NEEDLOGIN = 4;
	
	public static final byte MSG_SESSION_EFFECTIVE = 5;
	
	public static final byte MSG_SESSION_INVALID = 6;
	
	protected HashMap<IOnPushListener, Boolean> onPushListeners = null;
	
	protected HashMap<IConnectionStateListener, Boolean> tunnelListeners = null;
	
	protected HashMap<INetSessionStateListener, Boolean> sessionListeners = null;
	

	private static class InstanceHolder {
		private static final MqttEventDispatcher sInstance = new MqttEventDispatcher();
		static {
			sInstance.init();
		}
	}

	public static MqttEventDispatcher getInstance() {
		return InstanceHolder.sInstance;
	}

	void init() {
		
	}

	public void registerOnPushListener(IOnPushListener listener, boolean needUISafety) {
		logger.debug("注册推送监听类");
		synchronized (this) {
			if (null == listener) {
				return;
			}
			if (null == this.onPushListeners) {
				this.onPushListeners = new HashMap<>();
			}
			this.onPushListeners.put(listener, Boolean.valueOf(needUISafety));
		}
	}

	public void unregisterOnPushListener(IOnPushListener listener) {
		logger.debug("注销推送监听类");
		synchronized (this) {
			if (null == listener || null == this.onPushListeners || 0 >= this.onPushListeners.size()) {
				return;
			}
			this.onPushListeners.remove(listener);
		}
	}

	public void registerOnTunnelStateListener(IConnectionStateListener listener, boolean needUISafety) {
		logger.debug("注册通道监听类");
		synchronized (this) {
			if (null == listener) {
				return;
			}
			if (null == this.tunnelListeners) {
				this.tunnelListeners = new HashMap<>();
			}
			this.tunnelListeners.put(listener, Boolean.valueOf(needUISafety));
		}
	}

	public void unregisterOnTunnelStateListener(IConnectionStateListener listener) {
		logger.debug("注销通道监听类");
		synchronized (this) {
			if (null == listener || null == this.tunnelListeners || 0 >= this.tunnelListeners.size()) {
				return;
			}
			this.tunnelListeners.remove(listener);
		}
	}

	public void registerNetSessionStateListener(INetSessionStateListener listener, boolean needUISafety) {
		logger.debug("注册网络会话状态监听类");
		synchronized (this) {
			if (null == listener) {
				return;
			}
			if (null == this.sessionListeners) {
				this.sessionListeners = new HashMap<>();
			}
			this.sessionListeners.put(listener, Boolean.valueOf(needUISafety));
		}
	}

	public void unregisterNetSessionStateListener(INetSessionStateListener listener) {
		logger.debug("注销网络会话状态监听类");
		synchronized (this) {
			if (null == listener || null == this.sessionListeners || 0 >= this.sessionListeners.size()) {
				return;
			}
			this.sessionListeners.remove(listener);
		}
	}

	public void broadcastMessage(int what, String method, byte[] content, String message) {
		logger.debug("广播消息----->>>>>");
		synchronized (this) {
			if (what == 3 && this.onPushListeners != null) {
				for (IOnPushListener listener : this.onPushListeners.keySet()) {
					if (false == listener.shouldHandle(method)) {
						logger.warn("shouldHandle return false");
						continue;
					}
					if (((Boolean) this.onPushListeners.get(listener)).booleanValue()) {
						doNotify(3, listener, method, content);
						continue;
					}
					logger.debug("call onCommand directly");
					listener.onCommand(method, content);
				}

			} else if (what == 1 || what == 2 || what == 7) {
				if (this.tunnelListeners != null) {
					Set<IConnectionStateListener> listeners = this.tunnelListeners.keySet();
					for (IConnectionStateListener listener : listeners) {
						if (((Boolean) this.tunnelListeners.get(listener)).booleanValue()) {
							doNotify(what, listener, message);
							continue;
						}
						OnTunnelState(what, listener, message);
					}

				}
			} else if ((what == 5 || what == 6 || what == 4) && this.sessionListeners != null) {
				Set<INetSessionStateListener> listeners = this.sessionListeners.keySet();
				for (INetSessionStateListener listener : listeners) {
					if (((Boolean) this.sessionListeners.get(listener)).booleanValue()) {
						doNotify(what, listener, null);
						continue;
					}
					OnSessionState(what, listener);
				}
			}
		}
	}

	private static void doNotify(int what, Object listener, String message) {
		OnTunnelState(what, (IConnectionStateListener) listener, message);
	}

	private static void doNotify(int what, Object listener, String method, byte[] payload) {
		logger.debug("enter doNotify");
		
		if (listener instanceof IOnPushListener) {
			IOnPushListener cb = (IOnPushListener) listener;
			if (what == 3) {
				logger.debug("call onCommand");
				cb.onCommand(method, payload);
			}

		} else if (listener instanceof INetSessionStateListener) {
			OnSessionState(what, (INetSessionStateListener) listener);
		}
	}

	static void OnTunnelState(int type, IConnectionStateListener listener, String content) {
		if (listener != null) {
			try {
				if (type == 1) {
					listener.onConnected();
				} else if (type == 2) {
					listener.onDisconnect();
				} else if (type == 7) {
					listener.onConnectFail(content);
				}
			} catch (Exception e) {
				logger.debug("catch exception from IConnectionStateListener");
			}
		}
	}

	static void OnSessionState(int type, INetSessionStateListener listener) {
		logger.debug("会话状态通知");
		if (listener != null)
			try {
				if (type == 5) {
					listener.onSessionEffective();
				} else if (type == 6) {
					listener.onSessionInvalid();
				} else if (type == 4) {
					listener.onNeedLogin();
				}
			} catch (Exception e) {
				logger.debug("catch exception from INetSessionStateListener", e);
			}
	}

	private MqttEventDispatcher() {
	}
}

