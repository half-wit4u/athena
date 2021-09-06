package com.sarf.rylr.push.worker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.ScheduledFuture;

@Component
public class ScheduleTaskWorker {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskWorker.class);

	public void registerScheduleTask(final Channel channel) {
		ScheduledFuture<?> future = channel.eventLoop().scheduleAtFixedRate(new Runnable() {
			public void run() {
				execute(channel);
			}

		}, 60, 60, TimeUnit.SECONDS);
	}

	/**
	 * “Never put a long-running task in the execution queue, because it will block any other task from executing on the same thread.”
	 * If you must make blocking calls or execute long-running tasks, we advise the use of a dedicated EventExecutor.
	 * @param channel
	 */
	public void execute(Channel channel) {
		logger.info("Scheduled Task Execution!!" + Calendar.getInstance().getTime());
		channel.writeAndFlush(new TextWebSocketFrame("Scheduled Task executed at " + Calendar.getInstance().getTime()));
	}

}
