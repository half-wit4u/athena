package com.sarf.rylr.push.utility;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.handler.auth.ClientContext;

import io.netty.channel.Channel;

@Component
public class ClientConnectionRegistry {

	private static final Logger logger = LoggerFactory.getLogger(ClientConnectionRegistry.class);

	private final ConcurrentMap<String, ClientContext> clientPushConnectionMap;
	private final SecureRandom secureTokenGenerator;

	private ClientConnectionRegistry() {
		clientPushConnectionMap = new ConcurrentHashMap(1024 * 32);
		secureTokenGenerator = new SecureRandom();
	}

	public ClientContext get(final String clientId) {
		return clientPushConnectionMap.get(clientId);
	}

	public String mintNewSecureToken() {
		byte[] tokenBuffer = new byte[15];
		secureTokenGenerator.nextBytes(tokenBuffer);
		return Base64.getUrlEncoder().encodeToString(tokenBuffer);
	}

	public void put(final String clientId, final ClientContext clientContext) {
		clientPushConnectionMap.put(clientId, clientContext);
	}

	public ClientContext remove(final String clientId) {
		final ClientContext pc = clientPushConnectionMap.remove(clientId);
		return pc;
	}

	public int size() {
		return clientPushConnectionMap.size();
	}

	public void printRegisteryStat() {

		for (Map.Entry<String, ClientContext> header : clientPushConnectionMap.entrySet()) {
			logger.info("Client ID={}, Value={}", header.getKey(), header.getValue().getUserContext());
		}

	}

	public List<Channel> getChannelList(String filterCriteria) {
		if (StringUtils.isEmpty(filterCriteria)) {
			return Collections.EMPTY_LIST;
		}

		List<Channel> channelList = new ArrayList<Channel>();
		for (Map.Entry<String, ClientContext> header : clientPushConnectionMap.entrySet()) {
			if (header.getValue().getUserContext() != null
					&& filterCriteria.contains(header.getValue().getUserContext())
					&& header.getValue().getChannel() != null) {
				logger.info("Client ID={} found for given message ={} with context {} ", header.getKey(),
						filterCriteria, header.getValue().getUserContext());
				channelList.add(header.getValue().getChannel());
			}

		}
		return channelList;
	}

}
