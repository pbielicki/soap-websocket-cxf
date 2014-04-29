package com.bielu.webservice.calculator;

import java.math.BigDecimal;

public interface Calculable {

  BigDecimal calculate(BigDecimal left, BigDecimal right);
}
