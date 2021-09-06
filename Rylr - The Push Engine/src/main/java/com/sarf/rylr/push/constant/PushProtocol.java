package com.sarf.rylr.push.constant;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public enum PushProtocol {

	WEBSOCKET {
		@Override
		public Object getHandshakeCompleteEvent() {
			return WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE;
		}

		@Override
		public String getPath() {
			return "/ws";
		}

		@Override
		public ChannelFuture sendPushMessage(ChannelHandlerContext ctx, ByteBuf mesg) {
			final TextWebSocketFrame wsf = new TextWebSocketFrame(mesg);
			return ctx.channel().writeAndFlush(wsf);
		}

		@Override
		public ChannelFuture sendPing(ChannelHandlerContext ctx) {
			return ctx.channel().writeAndFlush(new PingWebSocketFrame());
		}

		@Override
		public Object goAwayMessage() {
			return new TextWebSocketFrame("_CLOSE_");
		}

		@Override
		public Object serverClosingConnectionMessage(int statusCode, String reasonText) {
			return new CloseWebSocketFrame(statusCode, reasonText);
		}

	};

	public final void sendErrorAndClose(ChannelHandlerContext ctx, int statusCode, String reasonText) {
		final Object mesg = serverClosingConnectionMessage(statusCode, reasonText);
		ctx.writeAndFlush(mesg).addListener(ChannelFutureListener.CLOSE);
	}

	public abstract Object getHandshakeCompleteEvent();

	public abstract String getPath();

	public abstract ChannelFuture sendPushMessage(ChannelHandlerContext ctx, ByteBuf mesg);

	public abstract ChannelFuture sendPing(ChannelHandlerContext ctx);

	/**
	 * Application level protocol for asking client to close connection
	 * 
	 * @return WebSocketFrame which when sent to client will cause it to close
	 *         the WebSocket
	 */
	public abstract Object goAwayMessage();

	/**
	 * Message server sends to the client just before it force closes connection
	 * from its side
	 * 
	 * @return
	 */
	public abstract Object serverClosingConnectionMessage(int statusCode, String reasonText);

}
