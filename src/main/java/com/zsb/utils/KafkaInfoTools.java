package com.zsb.utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

/**
 * Date: 2016年5月26日 <br>
 * @author zhoushanbin
 */
public class KafkaInfoTools {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaInfoTools.class);
	
	public KafkaInfoTools() {
	}
	public static long getLastOffset(SimpleConsumer consumer, String topic,
			int partition, long whichTime, String clientName) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
				partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(
				whichTime, 1));
		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
				requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
				clientName);
		OffsetResponse response = consumer.getOffsetsBefore(request);

		if (response.hasError()) {
			LOG.error("Error fetching data Offset Data the Broker. Reason: " +response.errorCode(topic, partition));
			return 0;
		}
		long[] offsets = response.offsets(topic, partition);
		return offsets[0];
	}

	public static TreeMap<Integer,PartitionMetadata> findLeader(List<String> a_seedBrokers,
			int a_port, String a_topic) {
		TreeMap<Integer, PartitionMetadata> map = new TreeMap<Integer, PartitionMetadata>();
		for (String seed : a_seedBrokers) {
			SimpleConsumer consumer = null;
			try {
				consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024,
						"leaderLookup"+new Date().getTime());
				List<String> topics = Collections.singletonList(a_topic);
				TopicMetadataRequest req = new TopicMetadataRequest(topics);
				kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

				List<TopicMetadata> metaData = resp.topicsMetadata();
				for (TopicMetadata item : metaData) {
					for (PartitionMetadata part : item.partitionsMetadata()) {
						map.put(part.partitionId(), part);
					}
				}
			} catch (Exception e) {
				LOG.error("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic + ", ]",e);
			} finally {
				if (consumer != null)
					consumer.close();
			}
		}
		return map;
	}
	
	
	public static void createTopic(){
		
	}
	
	public static void listTopic(){
		
	}
	
	public static void deleteTopic(){
		
	}
	
	
	public static void main(String[] args) {
		
		String topic = "mytest";
		String seed = "node01";
		int port = 9092;

		List<String> seeds = new ArrayList<String>();
		seeds.add(seed);
		TreeMap<Integer,PartitionMetadata> metadatas = findLeader(seeds, port, topic);
		
		int sum = 0;
		
		for (Entry<Integer,PartitionMetadata> entry : metadatas.entrySet()) {
			int partition = entry.getKey();
			String leadBroker = entry.getValue().leader().host();
			String clientName = "Client_" + topic + "_" + partition;
			SimpleConsumer consumer = new SimpleConsumer(leadBroker, port, 100000,
					64 * 1024, clientName);
			long readOffset = getLastOffset(consumer, topic, partition,
					kafka.api.OffsetRequest.LatestTime(), clientName);
			sum += readOffset;
			System.out.println(partition+":"+readOffset);
			if(consumer!=null)consumer.close();
		}
		System.out.println("总和："+sum);
	}
	
	
}