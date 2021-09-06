package com.sarf.service.push;

public interface MyPushService {

	public Boolean push(String clientId, String message);

	public Boolean pushToAll(String message);

}
