package com.sarf.restservice;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sarf.kafka.producer.KafkaProducer;
import com.sarf.service.push.MyPushService;

import cn.hutool.json.JSONObject;

@Service
public class MyPushEngineRestImpl implements MyPushEngineRest {

	private static final Logger logger = LoggerFactory.getLogger(MyPushEngineRestImpl.class);

	@Autowired
	private MyPushService myPushService;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	

	public Response push(String clientId, String message) {
		// TODO Auto-generated method stub
		if (clientId == null || clientId.isEmpty()) {
			myPushService.pushToAll(message);
		} else {
			myPushService.push(clientId, message);
		}
		return Response.ok().build();
	}

	public Response push(String message) {
		// TODO Auto-generated method stub
		myPushService.pushToAll(message);
		return Response.ok().status(202).build();
	}

	public Response process(String message) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject(message);
		logger.info("Process message initiated! - Message data = {}, Client {}", message, jsonObject.get("clientId"));
		//myPushService.push(jsonObject.get("clientId").toString(), jsonObject.get("data").toString());
		kafkaProducer.produceMessage(message);
		return Response.ok().build();
	}

}
