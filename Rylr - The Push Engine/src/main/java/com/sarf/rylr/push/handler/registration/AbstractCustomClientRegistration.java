package com.sarf.rylr.push.handler.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.handler.auth.ClientContext;
import com.sarf.rylr.push.utility.ClientConnectionRegistry;
import com.sarf.rylr.push.worker.ScheduleTaskWorker;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@Component
@ChannelHandler.Sharable
public class AbstractCustomClientRegistration extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(AbstractCustomClientRegistration.class);

	@Autowired
	private ClientConnectionRegistry clientConnectionRegistry;

	@Autowired
	private ScheduleTaskWorker scheduleTaskWorker;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Post connection stablishment,  Channel is active now !" + ctx.name());
		super.channelActive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("User Event trigerred" + evt);
		if (evt == "HANDSHAKE_COMPLETE") {
			// pushConnection = new PushConnection(pushProtocol, ctx);
			// Unauthenticated connection, wait for small amount of time for
			// a client to send auth token in
			// a first web socket frame, otherwise close connection
			// ctx.executor().schedule(this::closeIfNotAuthenticated,
			// 10,TimeUnit.SECONDS);
			logger.info("WebSocket handshake complete.");
		} else if (evt instanceof ClientContext) {
			ClientContext clientContext = (ClientContext) evt;
			if ((clientContext.isSuccess())) {
				logger.info("registering client {}", clientContext);
				// ctx.pipeline().remove(PushH.NAME);
				registerClient(ctx, clientContext);
				ctx.channel().writeAndFlush(new TextWebSocketFrame("clientId:" + clientContext.getClientIdentity()));
				logger.info("Client Registration complete {}", clientContext);
			} else {
				logger.error("Push registration failed: Auth success={}, WS handshake success={}",
						clientContext.isSuccess(), ctx != null);

			}
		}
		super.userEventTriggered(ctx, evt);

	}

	protected void registerClient(ChannelHandlerContext ctx, ClientContext clientContext) {
		logger.info("Registering client {} .", clientContext.getClientIdentity());
		clientConnectionRegistry.put(clientContext.getClientIdentity(), clientContext);

		// Add Scheduled task execution feature
		scheduleTaskWorker.registerScheduleTask(ctx.channel());

	}
}
