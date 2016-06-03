package com.zsb.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zsb.model.MdsParam;
import com.zsb.services.IMsgProduce;
import com.zsb.services.impl.KafkaMsgProduceImpl;

/**
 * @date 2016年6月2日
 * @author zhoushanbin
 *
 */
public class MsgProduceUtils {
	
	
	private static Map<String,IMsgProduce> senders = new ConcurrentHashMap<String,IMsgProduce>();
	
	
	public static IMsgProduce getProducer(MdsParam param){
		
		if(senders.containsKey(param.calcurateKey())){
			IMsgProduce sender = senders.get(param.calcurateKey());
			if(sender.isAlive()){
				return sender;
			}
			else{
				senders.remove(param.calcurateKey());
			}
		}
		IMsgProduce sender = new KafkaMsgProduceImpl(param);
		senders.put(param.calcurateKey(), sender);
		return sender;
		
	}
	
	
}
