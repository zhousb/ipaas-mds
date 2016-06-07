package com.zsb.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgProduce;
import com.zsb.services.impl.KafkaMsgProduceImpl;

/**
 * @date 2016年6月2日
 * @author zhoushanbin
 *
 */
public class MsgProduceUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(MsgProduceUtils.class);
	
	private static Map<String,IMsgProduce> senders = new ConcurrentHashMap<String,IMsgProduce>();
	
	
	public static IMsgProduce getProducer(MdsParam param){
		Gson gson = new Gson();
		LOG.debug("get producer ;param="+gson.toJson(param));
		if(senders.containsKey(param.calcurateKey())){
			IMsgProduce sender = senders.get(param.calcurateKey());
			if(sender.isAlive()){
				return sender;
			}
			else{
				senders.remove(param.calcurateKey());
			}
		}
		
		String strategy = param.getStrategy();
		IMsgProduce sender = null;
		if(Consts.KafkaConst.STRATEGY_KAFKA.equals(strategy)){
			sender = new KafkaMsgProduceImpl(param);
		}
		//todo 后期扩张其他的mds
		else {
			throw new RuntimeException("get producer error;strategy 不合法！");
		}
		senders.put(param.calcurateKey(), sender);
		return sender;
		
	}
	
	
}
