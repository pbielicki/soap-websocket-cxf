package com.bielu.config;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jws.WebService;
import javax.xml.ws.WebServiceProvider;

import org.apache.cxf.Bus;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JAXWSMethodInvoker;
import org.apache.cxf.service.invoker.SpringBeanFactory;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Spring Application Context automatically registering and publishing CXF endpoints.
 * <p/>
 * After all singleton beans are initialized and instantiated, this implementation
 * looks up WebServices that are registered in Spring context and creates and publishes
 * CXF endpoint for each of them.
 * <p/>
 * Note that CXF endpoint is published without creating the WebService instance. We only
 * give CXF instructions how to instantiate the service if client call arrives.
 * It means that only when a client call arrives a service instance is retrieved from
 * Spring context (see {@linkplain SpringBeanFactory}).  
 * 
 * @author Przemyslaw Bielicki
 * @see AnnotationConfigWebApplicationContext
 * @see EndpointImpl
 * @see SpringBeanFactory
 */
public class JaxWsWebApplicationContext extends AnnotationConfigWebApplicationContext {

  ConnectionFactory factory;
  
  @Override
  protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    super.finishBeanFactoryInitialization(beanFactory);
    factory = beanFactory.getBean(ConnectionFactory.class);

    // initialize CXF endpoints after all Singletons are instantiated
    for (String beanName : beanFactory.getBeanDefinitionNames()) {
      Class<?> beanClass = null;
      try {
        String className = beanFactory.getBeanDefinition(beanName).getBeanClassName();
        if (className == null) {
          continue;
        }

        beanClass = Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }

      if (isWebService(beanClass)) {
        Bus bus = beanFactory.getBean(Bus.DEFAULT_BUS_ID, Bus.class);
        EndpointImpl endpoint = new EndpointImpl(bus, null);
        endpoint.setImplementorClass(beanClass);
        endpoint.setInvoker(new JAXWSMethodInvoker(new SpringBeanFactory(beanName)));
        endpoint.setAddress("/" + getServiceName(beanClass, beanName));
        endpoint.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        endpoint.getFeatures().add(configureJmsFeature(getServiceName(beanClass, beanName)));
        endpoint.publish();
      }
    }
  }

  Feature configureJmsFeature(String serviceName) {
    JMSConfigFeature jmsConfigFeature = new JMSConfigFeature();
    JMSConfiguration jmsConfig = new JMSConfiguration();
    jmsConfig.setCacheLevel(3);
    jmsConfig.setConnectionFactory(factory);
    jmsConfig.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    jmsConfig.setTargetDestination("jms/InQueue");
    jmsConfig.setMessageSelector("example_JaxWsService = '" + serviceName + "'");
    jmsConfigFeature.setJmsConfig(jmsConfig);
    return jmsConfigFeature;
  }

  String getServiceName(Class<?> beanClass, String beanName) {
    WebService ws = beanClass.getAnnotation(WebService.class);
    if (ws != null && ws.name().isEmpty() == false) {
      return StringUtils.capitalize(ws.name());
    }
    
    WebServiceProvider wsp = beanClass.getAnnotation(WebServiceProvider.class);
    if (wsp != null && wsp.serviceName().isEmpty() == false) {
      return StringUtils.capitalize(wsp.serviceName());
    }
    
    return StringUtils.capitalize(beanName);
  }
  
  static boolean isWebService(Class<?> beanClass) {
    return beanClass.getAnnotation(WebService.class) != null
        || beanClass.getAnnotation(WebServiceProvider.class) != null;
  }
}
