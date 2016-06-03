package com.zsb.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	
	private static Map<String,IMsgConsumer> consumers = new HashMap<String,IMsgConsumer>();
	
	
	public static IMsgConsumer getConsumer(MdsParam param){
		
		if(consumers.containsKey(param.calcurateKey())){
			IMsgConsumer consumer = consumers.get(param.calcurateKey());
			if(consumer.isAlive()){
				return consumer;
			}
		}
		IMsgConsumer consumer = new KafkaMsgConsumerImpl(param);
		consumers.put(param.calcurateKey(), consumer);
		return consumer;
		
	}
	
	
	
}
