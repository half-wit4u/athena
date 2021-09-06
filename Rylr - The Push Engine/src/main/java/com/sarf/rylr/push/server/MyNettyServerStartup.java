package com.sarf.rylr.push.server;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.utility.ClientConnectionRegistry;

@Component
@EnableAsync
public class MyNettyServerStartup {

	private static final Logger log = LoggerFactory.getLogger(MyNettyServerStartup.class);

	@Autowired
	private MyNettyServer myNettyServer;

	@Autowired
	ClientConnectionRegistry cientConnectionRegistry;

	@PostConstruct
	public void init() {
		log.info("!!!!START NETTY SERVER!!!");

		try {
			myNettyServer.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
