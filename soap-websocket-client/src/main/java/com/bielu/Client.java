package com.bielu;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

public class Client {

  public static void main(String[] args) throws Exception {
    String destUri = "ws://localhost:8080/soap-websocket-webapp/jaxws";
    if (args.length > 0) {
      destUri = args[0];
    }
    
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    container.connectToServer(new JaxWsClientEndpoint(), new URI(destUri));
    
    Thread.sleep(10000);
  }
}
