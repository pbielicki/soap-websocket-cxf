package com.bielu;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

@ClientEndpoint(configurator = DummyConfigurator.class) // see https://issues.apache.org/bugzilla/show_bug.cgi?id=56343
public class JaxWsClientEndpoint {

  private static final String SOAP_PREFIX = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
                                            + "xmlns:echo='http://example'>"
                                            + "<soapenv:Header></soapenv:Header><soapenv:Body>";
  private static final String SOAP_END = "</soapenv:Body></soapenv:Envelope>";

  @OnClose
  public void onClose(Session session, CloseReason reason) {
    System.out.printf("Connection %s closed: %s%n", session.getId(), reason);
  }

  @OnOpen
  public void onConnect(final Session session) {
    System.out.printf("Got connect: %s%n", session);
    session.setMaxIdleTimeout(2000);
    final Basic remote = session.getBasicRemote();
      new Thread() {
        public void run() {
          try {
            remote.sendText(SOAP_PREFIX + "<echo:hello><name>Przemek</name></echo:hello>" + SOAP_END);
            for (int i = 0; i < 1000; i++) {
              remote.sendText(SOAP_PREFIX + "<echo:calculate><input><requestId>" + i + "</requestId>"
                  + "<left>" + (10 + i) + "</left><right>" + (20 + i) + "</right><operation>ADD</operation></input></echo:calculate>" + SOAP_END);
            }
          } catch (Throwable t) {
            t.printStackTrace();
          }
        };
      }.start();
  }

  @OnMessage
  public void onMessage(String msg, Session session) {
    System.out.printf("%s%n", msg);
  }
  
  @OnError
  public void onError(Throwable t) {
    // ignore
    t.printStackTrace();
  }
}
