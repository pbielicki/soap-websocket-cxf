package com.bielu.webservice.calculator;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.springframework.context.annotation.Scope;

@WebService(name = "Calculator", serviceName = "Calculator", targetNamespace = "http://example")
@Scope("prototype") // means stateless
public class CalculatorService {

  public @WebResult(name = "result") CalculatorResult calculate(@WebParam(name = "input") CalculatorInput input) {
    return new CalculatorResult(input.requestId, input.operation.calculate(input.left, input.right));
  }
}
