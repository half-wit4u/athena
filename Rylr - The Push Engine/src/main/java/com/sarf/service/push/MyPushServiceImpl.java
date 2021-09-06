package com.sarf.service.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.handler.auth.ClientContext;
import com.sarf.rylr.push.utility.ClientConnectionRegistry;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

@Component
public class MyPushServiceImpl implements MyPushService {

	private static final Logger logger = LoggerFactory.getLogger(MyPushServiceImpl.class);

	@Autowired
	private ClientConnectionRegistry clientConnectionRegistry;

	public Boolean push(String clientId, String message) {

		// TODO Auto-generated method stub
		ClientContext context = clientConnectionRegistry.get(clientId);
		if (context != null && context.getChannel() != null) {
			logger.info("Pushing message to specific client {} ", clientId);
			context.getChannel().writeAndFlush(new TextWebSocketFrame(message + ", status=PROCESSED"));
		}
		return true;
	}

	public Boolean pushToAll(final String message) {
		// TODO : Check recommendation from Norman Maurer on writing frequently
		// the data into channel
		// https://www.youtube.com/watch?v=_GRIyCMNGGI
		for (final Channel channel : clientConnectionRegistry.getChannelList(message)) {
			logger.info("Pushing message to channel {} ", channel.id());
			// TODO : Make proper use of isWritable to prevent OOM

			if (channel.isWritable()) {
				// https://www.youtube.com/watch?v=_GRIyCMNGGI at timeline 44:00
				channel.eventLoop().execute(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						channel.writeAndFlush(new TextWebSocketFrame(message));
						ReferenceCountUtil.release(message);
					}
				});

			}

		}

		// MyNettyConfig.getChannelGroup().writeAndFlush(new
		// TextWebSocketFrame(message));
		return true;
	}

}
