package com.sarf.rylr.push.handler.auth;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sarf.rylr.push.constant.PushProtocol;
import com.sarf.rylr.push.utility.PushUserAuth;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

@ChannelHandler.Sharable

public abstract class AbstractCustomAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	public static final String NAME = "push_auth_handler";
	private static Logger logger = LoggerFactory.getLogger(AbstractCustomAuthHandler.class);

	
	
	
	
	public final void sendHttpResponse(HttpRequest req, ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status);
		resp.headers().add("Content-Length", "0");
		final boolean closeConn = ((status != OK) || (!HttpUtil.isKeepAlive(req)));
		if (closeConn) {
			resp.headers().add(HttpHeaderNames.CONNECTION, "Close");
		}
		final ChannelFuture cf = ctx.channel().writeAndFlush(resp);
		if (closeConn) {
			cf.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	
	/*@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Channel Active now !!");
		super.channelActive(ctx);
	}*/
	

	@Override
	protected final void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		if (req.method() != HttpMethod.GET) {
			sendHttpResponse(req, ctx, METHOD_NOT_ALLOWED);
			return;
		}

		final String path = req.uri();
		if ("/healthcheck".equals(path)) {
			logger.info("Health check OK");
			sendHttpResponse(req, ctx, OK);
		} else if (StringUtils.isNotEmpty(path)) {
			// TODO SARF - path can be evaluated against origin requester to
			// verify the domain
			// CSRF protection
			final String origin = req.headers().get(HttpHeaderNames.ORIGIN);
			if (((PushProtocol.WEBSOCKET.getPath().equals("ws"))))
				//TODO - Sarf to handle origin check
			// && ((origin == null) ||
			// (!origin.toLowerCase().endsWith(originDomain))))
			{
				logger.error("Invalid Origin header {} in WebSocket upgrade request", origin);
				sendHttpResponse(req, ctx, BAD_REQUEST);
			} else if (isDelayedAuth(req, ctx)) {
				// client auth will happen later, continue with WebSocket
				// upgrade handshake
				ctx.fireChannelRead(req.retain());
			} else {
				logger.info("Proceed to authenticate the request!!");
				final PushUserAuth authEvent = doAuth(req);
				if (authEvent.isSuccess()) {
					ctx.fireChannelRead(req.retain()); // continue with
														// WebSocket upgrade
														// handshake
					ctx.fireUserEventTriggered(authEvent);
				} else {
					logger.warn("Auth failed: {}", authEvent.statusCode());
					sendHttpResponse(req, ctx, HttpResponseStatus.valueOf(authEvent.statusCode()));
				}
			}
		} else {
			sendHttpResponse(req, ctx, NOT_FOUND);
		}
	}

	protected final Cookie parseCookies(FullHttpRequest req) {
		/*
		 * final Cookies cookies = new Cookies(); final String cookieStr =
		 * req.headers().get(HttpHeaderNames.COOKIE); if
		 * (!Strings.isNullOrEmpty(cookieStr)) { final Set<Cookie> decoded =
		 * CookieDecoder.decode(cookieStr, false); decoded.forEach(cookie ->
		 * cookies.add(cookie)); } return cookies;
		 */
		return null;
	}

	/**
	 * @return true if Auth credentials will be provided later, for example in
	 *         first WebSocket frame sent
	 */
	protected abstract boolean isDelayedAuth(FullHttpRequest req, ChannelHandlerContext ctx);

	protected abstract PushUserAuth doAuth(FullHttpRequest req);
}
