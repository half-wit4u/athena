package com.sarf.rylr.push.server;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.sarf.rylr.push.handler.auth.MyCustomClientAuthHandler;
import com.sarf.rylr.push.handler.channel.MyCustomWebSocketHandler;
import com.sarf.rylr.push.handler.registration.AbstractCustomClientRegistration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component
@EnableAsync
public class MyNettyServer {
	private static final Logger log = LoggerFactory.getLogger(MyNettyServer.class);
	/**
	 * webSocket Protocol name
	 */
	private static final String WEBSOCKET_PROTOCOL = "WebSocket";

	/**
	 * Port number
	 */
	@Value("${webSocket.netty.port:8085}")
	private int port;

	/**
	 * webSocket Route
	 */
	@Value("${webSocket.netty.path:/ws}")
	private String webSocketPath;

	@Autowired
	private MyCustomWebSocketHandler myCustomWebSocketHandler;
	
	@Autowired
	private MyCustomClientAuthHandler pushAuthHandler;
	
	@Autowired
	private AbstractCustomClientRegistration abstractCustomClientRegistration;

	private EventLoopGroup parentGroup;
	private EventLoopGroup workerGroup;

	/**
	 * start-up with async in new thread so that the main thread is not blocked for further execution
	 * 
	 * @throws InterruptedException
	 */
	@Async
	public void start() throws InterruptedException {
		log.info("!!START NETTY SERVER!!");
		
		// https://netty.io/wiki/user-guide-for-4.x.html
		parentGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		
		// Suggestion to use ALLOCATOR with Pooled buffer by Norman Maurer
		// https://www.youtube.com/watch?v=_GRIyCMNGGI
		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		
		// For the tcp connection request of the secondary client of bossGroup,
		// workGroup is responsible for the read and write operations before the
		// client
		bootstrap.group(parentGroup, workerGroup);
		// Set channel of NIO type
		bootstrap.channel(NioServerSocketChannel.class);
		// Set listening port
		bootstrap.localAddress(new InetSocketAddress(port));
		// A channel is created when the connection arrives
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

			// Channel is initialised for each new connection
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				log.info("!!Initialize channle!!");
				
				// The Handler in the pipeline management channel is used to
				// handle business
				// The webSocket protocol itself is based on the http protocol,
				// so we also need to use the http codec here
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new ObjectEncoder());
				// A processor written in blocks
				ch.pipeline().addLast(new ChunkedWriteHandler());
				/*
				 * Explain: 1,http Data is segmented during transmission, and
				 * HttpObjectAggregator can aggregate multiple segments 2,That's
				 * why when a browser sends a lot of data, it sends multiple
				 * http requests
				 */
				ch.pipeline().addLast(new HttpObjectAggregator(8192));
				// Custom Auth handler
				ch.pipeline().addLast(pushAuthHandler);
				// Custom Registration Hander , to capture the client context and register in registry
				ch.pipeline().addLast(abstractCustomClientRegistration);
				
				
				/*
				 * Explain: 1,Corresponding to webSocket, its data is passed in
				 * the form of frame 2,When the browser requests,
				 * ws://localhost:8085/xxx indicates the requested uri 3,The
				 * core function is to upgrade http protocol to ws protocol and
				 * maintain long connection
				 */
				ch.pipeline().addLast(
						new WebSocketServerProtocolHandler(webSocketPath, WEBSOCKET_PROTOCOL, true, 65536 * 10));
				// Custom handler, handling business logic
				ch.pipeline().addLast(myCustomWebSocketHandler);
				
				log.info("!!New channel Initialized!!");

			}
		});
		// After the configuration is completed, start binding the server, and
		// block by calling the sync synchronization method until the binding is
		// successful
		ChannelFuture channelFuture = bootstrap.bind().sync();
		log.info("Server started and listen on:{}", channelFuture.channel().localAddress());
		// Monitor the closed channel
		channelFuture.channel().closeFuture().sync();

	}

	/**
	 * Release resources
	 * 
	 * @throws InterruptedException
	 */
	@PreDestroy
	public void destroy() throws InterruptedException {
		if (workerGroup != null) {
			workerGroup.shutdownGracefully().sync();
		}
		if (parentGroup != null) {
			parentGroup.shutdownGracefully().sync();
		}
		
	}

	@PostConstruct()
	public void init() {
		
		// TODO SARF: Async start is taken care by MyNettyServerStartup, as Spring does not allow async start within the class method call
		/*
		 * log.info("Starting the netty server post construction"); // A new
		 * thread needs to be opened to execute the netty server try {
		 * 
		 * new Thread("Netty Server Demon thread") { public void run() {
		 * this.start(); } }.start(); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}
}