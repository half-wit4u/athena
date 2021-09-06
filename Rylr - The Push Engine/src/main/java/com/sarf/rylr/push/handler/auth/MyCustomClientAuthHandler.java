package com.sarf.rylr.push.handler.auth;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.utility.PushUserAuth;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

@Component
@ChannelHandler.Sharable
public class MyCustomClientAuthHandler extends AbstractCustomAuthHandler {

	private static final Logger logger = LoggerFactory.getLogger(MyCustomClientAuthHandler.class);

	@Override
	protected boolean isDelayedAuth(FullHttpRequest req, ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * TODO : this method to perform the authentication before entering into
	 * handshake mode
	 * Key=Host, Value=localhost:8085
		Key=Connection, Value=Upgrade
		Key=Pragma, Value=no-cache
		Key=Cache-Control, Value=no-cache
		Key=User-Agent, Value=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36
		Key=Upgrade, Value=websocket
		Key=Origin, Value=http://localhost:8080
		Key=Sec-WebSocket-Version, Value=13
		Key=Accept-Encoding, Value=gzip, deflate, br
		Key=Accept-Language, Value=en-US,en;q=0.9
		Key=Cookie, Value=apt_customer_id=AITA-XYZ; ref_airport=XYZ; SecurityToken=6112323-c232-4d23276-a669-cb5a52424
		Key=Sec-WebSocket-Key, Value=iChhQi166jZyTSxKEr9cvw==
		Key=Sec-WebSocket-Extensions, Value=permessage-deflate; client_max_window_bits
		Key=content-length, Value=0

	 */
	@Override
	protected PushUserAuth doAuth(FullHttpRequest req) {
		// TODO Auto-generated method stub
		logger.info("!Perform authentication!");

		HttpHeaders headers = req.headers();
		for (Map.Entry<String, String> header : headers.entries()) {
			logger.debug("HEADER  Key={}, Value={}", header.getKey(), header.getValue());
		}

		// Operate on Cookie to perform authentication check
		String customerId = req.headers().get("Sec-WebSocket-Key") !=null ?req.headers().get("Sec-WebSocket-Key") : UUID.randomUUID().toString();
		if (!StringUtils.isEmpty(customerId)) {
			return new ClientContext(customerId);
		}

		return new ClientContext(customerId);
		// return new ClientContext(HttpResponseStatus.UNAUTHORIZED.code());
	}

}
