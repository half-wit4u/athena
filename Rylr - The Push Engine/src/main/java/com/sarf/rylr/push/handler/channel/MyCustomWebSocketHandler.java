package com.sarf.rylr.push.handler.channel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.handler.auth.ClientContext;
import com.sarf.rylr.push.utility.ClientConnectionRegistry;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

@Component
@ChannelHandler.Sharable
public class MyCustomWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private static final Logger logger = LoggerFactory.getLogger(MyCustomWebSocketHandler.class);
 
	@Autowired
	private ClientConnectionRegistry clientConnectionRegistry;

	/**
	 * Once connected, the first is executed
	 * 
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		logger.info("handlerAdded Be called - " + ctx.channel().id().asLongText());
		// Add to channelGroup channel group

	}

	/**
	 * BinaryWebSocketFrame Data frame: binary data TextWebSocketFrame Data
	 * frame: text data ContinuationWebSocketFrame Data frame: text or binary
	 * data that belongs to a previous BinaryWebSocketFrame or
	 * TextWebSocketFrame CloseWebSocketFrame Control frame: a CLOSE request,
	 * close status code, and a phrase PingWebSocketFrame Control frame:
	 * requests a PongWebSocketFrame PongWebSocketFrame Control frame: responds
	 * to a PingWebSocketFrame request Read data
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		logger.info("Server received message:{}", msg.text());

		// Get user ID and associate with channel
		JSONObject jsonObject = JSONUtil.parseObj(msg.text());
		String clientId = jsonObject.getStr("clientId");
		String context = jsonObject.getStr("context");

		// The user ID is added to the channel as a custom attribute, which is
		// convenient to obtain the user ID in the channel at any time
		AttributeKey<String> key = AttributeKey.valueOf("userId");
		ctx.channel().attr(key).setIfAbsent(clientId);

		// Updating context into client registry
		ClientContext clientContext = clientConnectionRegistry.get(clientId);
		if (clientContext != null) {
			// update context
			clientContext.setUserContext(context);
			clientContext.setChannel(ctx.channel());

			clientConnectionRegistry.put(clientId, clientContext);
		} else {
			clientContext = new ClientContext(clientId);
			clientContext.setUserContext(context);
			clientContext.setChannel(ctx.channel());
			clientConnectionRegistry.put(clientId, clientContext);
		}
		clientConnectionRegistry.printRegisteryStat();

		// Reply message
		// BAD
		// ctx.channel().writeAndFlush(new TextWebSocketFrame("Server connection
		// succeeded!\nClientId->"+clientId+" & Context->"+context));
		// Good way
		// https://www.youtube.com/watch?v=_GRIyCMNGGI at 45:00 timeline
		ctx.writeAndFlush(new TextWebSocketFrame(
				"Server connection succeeded!\nClientId->" + clientId + " & Context->" + context));

	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		logger.info("handlerRemoved Be called" + ctx.channel().id().asLongText());
		// Delete channel
		removeUserId(ctx);
		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("Exception:{}", cause.getMessage());
		cause.printStackTrace();
		// Delete channel
		removeUserId(ctx);
		ctx.close();
	}

	/**
	 * Delete the corresponding relationship between user and channel
	 * 
	 * @param ctx
	 */
	private void removeUserId(ChannelHandlerContext ctx) {
		AttributeKey<String> key = AttributeKey.valueOf("userId");
		String userId = ctx.channel().attr(key).get();
		logger.info("Removing  user registery for user {}", userId);
		if (StringUtils.isNotEmpty(userId)) {
			clientConnectionRegistry.remove(userId);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelInactive Be called" + ctx.channel().id().asLongText());
		super.channelInactive(ctx);
	}

}
