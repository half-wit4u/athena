package com.sarf.rylr.push.handler.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyCustomClientRegisterHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(MyCustomClientRegisterHandler.class);

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// TODO Auto-generated method stub

		log.info("Client event trigerred!!");
		super.userEventTriggered(ctx, evt);
	}

}
