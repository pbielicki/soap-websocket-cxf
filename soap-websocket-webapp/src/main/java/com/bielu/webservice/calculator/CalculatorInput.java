package com.bielu.webservice.calculator;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CalculatorInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class CalculatorInput {

  long requestId;
  BigDecimal left;
  BigDecimal right;
  Operation operation;
}
