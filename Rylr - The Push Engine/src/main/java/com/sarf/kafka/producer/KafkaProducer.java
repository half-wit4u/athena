package com.sarf.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	@Autowired
	private KafkaTemplate kafkaTemplate;

	public void produceMessage(String message) {
		logger.info("Send Message");
		kafkaTemplate.send("pushMessageTopic", "special", message);

	}
}
