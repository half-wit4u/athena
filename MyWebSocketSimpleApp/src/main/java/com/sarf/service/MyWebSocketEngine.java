package com.sarf.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value = "/webSocket")
public class MyWebSocketEngine {

	private static final Set<MyWebSocketEngine> connections = new CopyOnWriteArraySet<MyWebSocketEngine>();
	public Session session;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		connections.add(this);
		System.out.println("websocket is open : " + session.getId());

	}

	@OnMessage
	public void onMessage(String message) {
		System.out.println("Message received from client : " + message);
		broadcast(message);
	}

	@OnClose
	public void onClose() {
		System.out.println("websocket closed");
	}

	@OnError
	public void onError(Throwable throwable) {
		throwable.printStackTrace();
	}

	public void broadcast(String message) {
		for (MyWebSocketEngine current : connections)
			try {
				System.out.println("Boardcasting the message to " + current.session.getId());
				current.session.getBasicRemote().sendText("Message broadcast -- " + message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	private void sendPulse() {
		System.out.println("Post Construct");

		while (true) {
			try {
				for (MyWebSocketEngine current : connections) {
					System.out.println("Boardcasting the pulse to " + current.session.getId());
					current.session.getBasicRemote().sendText("Pulse " + System.currentTimeMillis());
				}
				Thread.sleep(5000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
