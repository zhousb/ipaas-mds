package com.zsb.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgConsumer;
import com.zsb.services.impl.KafkaMsgConsumerImpl;

/**
 * 消费者工具类
 * @date 2016年6月3日
 * @author zhoushanbin
 *
 */
public class MsgConsumerUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(MsgConsumerUtils.class);
	
	private static Map<String,IMsgConsumer> consumers = new ConcurrentHashMap<String,IMsgConsumer>();
	
	public static IMsgConsumer getConsumer(MdsParam param){
		Gson gson = new Gson();
		LOG.debug("get consumer ;param="+gson.toJson(param));
		if(consumers.containsKey(param.calcurateKey())){
			IMsgConsumer consumer = consumers.get(param.calcurateKey());
			if(consumer.isAlive()){
				return consumer;
			}
			else{
				consumers.remove(param.calcurateKey());
			}
		}
		String strategy = param.getStrategy();
		IMsgConsumer consumer = null;
		if(Consts.KafkaConst.STRATEGY_KAFKA.equals(strategy)){
			consumer = new KafkaMsgConsumerImpl(param);
		}
		else {
			throw new RuntimeException("get consumer error;strategy 不合法！");
		}
		consumers.put(param.calcurateKey(), consumer);
		return consumer;
		
	}
	
	public static KafkaMsgConsumerImpl getKafkaConsumer(MdsParam param){
		IMsgConsumer consumer = getConsumer(param);
		return (KafkaMsgConsumerImpl) consumer;
	}
	
	
	
}
