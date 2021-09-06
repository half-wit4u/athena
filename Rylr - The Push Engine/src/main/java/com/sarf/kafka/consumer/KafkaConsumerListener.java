package com.sarf.kafka.consumer;

import java.util.Calendar;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import com.sarf.service.push.MyPushService;

import cn.hutool.json.JSONObject;

public class KafkaConsumerListener implements MessageListener<String, String> {

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumerListener.class);

	@Autowired
	private MyPushService myPushService;

	public void onMessage(ConsumerRecord<String, String> record) {
		log.info("Message arrived");

		StringBuilder sb = new StringBuilder();
		sb.append("Key = ").append(record.key()).append(" Value = ").append(record.value()).append(" Partition = ")
				.append(record.partition()).append(" Offset= ").append(record.offset());
		log.info("Message =  {}", sb.toString());

		myPushService.pushToAll(", Timestamp=" + Calendar.getInstance().getTime() + "->" + record.value());

		if ("special".equalsIgnoreCase(record.key())) {
			JSONObject jsonObject = new JSONObject(record.value());

			myPushService.push(jsonObject.get("clientId").toString(), jsonObject.get("data").toString());
		}

	}
}
