package com.sarf.rylr.push.handler.auth;

import com.sarf.rylr.push.utility.PushUserAuth;

import io.netty.channel.Channel;

public class ClientContext implements PushUserAuth {

	private String customerId;
	private int statusCode;
	private String userContext;
	private Channel channel;

	private ClientContext(String customerId, int statusCode) {
		this.customerId = customerId;
		this.statusCode = statusCode;

	}

	private ClientContext(String customerId, int statusCode, String userContext) {
		this.customerId = customerId;
		this.statusCode = statusCode;
		this.userContext = userContext;
	}

	// Successful auth
	public ClientContext(String customerId) {
		this(customerId, 200);
	}

	// Failed auth
	public ClientContext(int statusCode) {
		this("", statusCode);
	}

	public ClientContext(String customerId, String userContext) {
		this(customerId, 200, userContext);
	}

	public boolean isSuccess() {
		return statusCode == 200;
	}

	public int statusCode() {
		return statusCode;
	}

	public String getClientIdentity() {
		return customerId;
	}

	public String getUserContext() {
		return userContext;
	}

	public void setUserContext(String userContext) {
		this.userContext = userContext;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
