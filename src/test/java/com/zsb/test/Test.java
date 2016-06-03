package com.zsb.test;

import java.util.HashMap;
import java.util.Map;

import com.zsb.consts.Consts;
import com.zsb.model.MdsParam;
import com.zsb.services.IMsgProduce;
import com.zsb.utils.MsgProduceUtils;

public class Test {
	
	
	public static void main(String args[]){
		testProducer();
	}
	
	public static void testProducer(){
		MdsParam param = new MdsParam();
		Map<String,String> conf = new HashMap<String,String>();
		conf.put("kafka.topic", "mytest");
		conf.put(Consts.KafkaConst.METADATA_BROKER_LIST, "node01:9092,node02:9092,node03:9092");
		conf.put(Consts.KafkaConst.REQUEST_REQUIRED_ACKS, "1");
		conf.put(Consts.KafkaConst.SERIALIZER_CLASS, "kafka.serializer.StringEncoder");
		param.setConf(conf);
		IMsgProduce producer = MsgProduceUtils.getProducer(param);
		producer.send("test2");
		System.out.println("end!!!!!");
	}
	
}
