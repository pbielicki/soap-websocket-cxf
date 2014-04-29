package com.bielu.config;

import javax.jms.ConnectionFactory;
import javax.jws.WebService;
import javax.xml.ws.WebServiceProvider;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
@ComponentScan(value = { "com.bielu.webservice" },
    includeFilters = {
        @Filter(WebService.class),
        @Filter(WebServiceProvider.class)
    })
@ImportResource({ 
  "classpath:META-INF/cxf/cxf.xml", 
  })
public class AppConfig {
  
  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory ccf = new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
    ccf.setSessionCacheSize(20);
    ccf.setCacheProducers(true);
    ccf.setCacheConsumers(true);
    return ccf;
  }
}
