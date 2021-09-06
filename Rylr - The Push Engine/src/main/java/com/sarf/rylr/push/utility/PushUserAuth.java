package com.sarf.rylr.push.utility;

public interface PushUserAuth {

	boolean isSuccess();

	int statusCode();

	String getClientIdentity();

}
