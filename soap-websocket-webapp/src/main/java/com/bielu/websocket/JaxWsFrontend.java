package com.bielu.websocket;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.activemq.ActiveMQConnectionFactory;

@ServerEndpoint("/jaxws")
public class JaxWsFrontend {

  Queue inQueue;
  Queue replyToQueue;
  javax.jms.Session jmsSession;
  Connection jmsConnection;
  MessageProducer producer;

  public JaxWsFrontend() {
    ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    try {
      jmsConnection = factory.createConnection();
    } catch (JMSException e) {
      throw new IllegalStateException("Unable to initialize JMS connection");
    }
  }

  @OnOpen
  public void onOpen(final Session session) {
    final Basic remote = session.getBasicRemote();
    try {
      jmsSession = jmsConnection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
      inQueue = jmsSession.createQueue("jms/InQueue");
      replyToQueue = jmsSession.createTemporaryQueue(); // one ReplyTo queue per websocket connection
      producer = jmsSession.createProducer(inQueue);
      jmsSession.createConsumer(replyToQueue).setMessageListener(new MessageListener() {
        public void onMessage(Message m) {
          try {
            if (m instanceof TextMessage) {
              TextMessage tm = (TextMessage) m;
              remote.sendText(tm.getText());
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      jmsConnection.start();
    } catch (JMSException e) {
      throw new IllegalStateException("Unable to initialize JMS", e);
    }
  }

  @OnMessage
  public void onMessage(String input) {
    try {
      TextMessage msg = jmsSession.createTextMessage(input);
      msg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
      msg.setStringProperty("SOAPJMS_contentType", "application/soap+xml");
      msg.setStringProperty("SOAPJMS_requestURI", "/jaxws");
      // POC - don't laugh
      if (input.contains("calculate")) {
        msg.setStringProperty("example_JaxWsService", "Calculator");
      } else {
        msg.setStringProperty("example_JaxWsService", "Hello");
      }
      msg.setJMSReplyTo(replyToQueue);
      producer.send(msg);
    } catch (JMSException e) {
      throw new IllegalArgumentException("Unable to enqueue received message into the JMS queue", e);
    }
  }
  
  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    System.out.println("Session " + session.getId() + " closed. Reason: " + closeReason);
    closeJmsSession();
  }
  
  @OnError
  public void onError(Throwable t, Session session) {
    closeJmsSession();
  }
  
  private void closeJmsSession() {
    try {
      jmsSession.close();
      jmsConnection.close();
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}
