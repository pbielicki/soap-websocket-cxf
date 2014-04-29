package com.bielu.webservice.calculator;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CalculatorResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalculatorResult {

  long requestId;
  BigDecimal result;

  public CalculatorResult() {
  }
  
  public CalculatorResult(long requestId, BigDecimal result) {
    this.requestId = requestId;
    this.result = result;
  }
}
