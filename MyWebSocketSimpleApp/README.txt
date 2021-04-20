HOW TO TEST
=============

Open your chrome developer tool
Go to Console



1. Open a session/connection
  var ws = new WebSocket("ws://localhost:8080/MyWebSocketApp/webSocket");

2. Receive message at client browser 

	ws.onmessage = function (evt) { 
	    console.log(evt.data);
	}

  
3. Send a message
   ws.send("Hi there");

