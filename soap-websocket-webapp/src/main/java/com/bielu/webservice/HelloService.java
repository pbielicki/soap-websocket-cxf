package com.bielu.webservice;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name = "Hello", serviceName = "Hello", targetNamespace = "http://example")
public class HelloService {

  public @WebResult(name = "welcome") String hello(@WebParam(name = "name") String name) {
    return "Hello " + name;
  }
}
