package com.zsb.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgProduce;
import com.zsb.utils.KafkaInfoTools;

/**
 * @date 2016年6月2日
 * @author zhoushanbin
 *
 */
public class KafkaMsgProduceImpl implements IMsgProduce{

	private static final Logger LOG = LoggerFactory.getLogger(KafkaMsgProduceImpl.class);
	
	private MdsParam param;
	
	private Properties pro = new Properties();
	
	private Producer<String,String> producer;
	
	private ProducerConfig producerConf;
	
	private Map<String,String> topicInfo;
	
	private String topic;
	
	private boolean active = true;
	
	private AtomicInteger seed = new AtomicInteger(0);
	
	public KafkaMsgProduceImpl(MdsParam param){
		
		this.param = param;	
		init();
	}
	
	@Override
	public void init() {
		try{
			Map<String,String> confMap = param.getConf();
			if(null != confMap){
				pro.putAll(confMap);
			}
			initTopicInfo();
			producerConf = new ProducerConfig(pro);
			producer = new Producer<String, String>(producerConf);
		}
		catch(Exception e){
			LOG.error("init error!",e);
		}
	}
	
	private void initTopicInfo(){
		
		topic = pro.getProperty(Consts.KafkaConst.TOPIC);
		TreeMap<Integer, PartitionMetadata> info = null;
		String[] brokers = (String[]) pro.getProperty(Consts.KafkaConst.METADATA_BROKER_LIST).split(",");
		List<String> bks = new ArrayList<String>();
		for (int i = 0; i < brokers.length; i++) {
			bks.add(brokers[i].substring(0, brokers[i].lastIndexOf(":")));
		}
		String port = brokers[0].substring(brokers[0].indexOf(":") + 1,
				brokers[0].length());
		info = KafkaInfoTools.findLeader(bks, new Integer(port).intValue(),
				topic);
		topicInfo = new HashMap<String,String>();
		for(Entry<Integer,PartitionMetadata> entry:info.entrySet()){
			topicInfo.put(String.valueOf(entry.getKey()),String.valueOf(entry.getKey()));
		}
		
	}
	
	@Override
	public void close() {
		
	}

	@Override
	public boolean isAlive() {

		return active;
	}


	@Override
	public void send(String message) {
		
		int partId = getSendPartitionId();
		
		KeyedMessage<String, String> km = new KeyedMessage<String, String>(topic, String.valueOf(partId), message);
		
		producer.send(km);
		
	}
	
	private int getSendPartitionId() {
		
		if(seed.getAndIncrement() > Integer.MAX_VALUE){
			seed.set(0);
		}
		return seed.get() % topicInfo.size();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
