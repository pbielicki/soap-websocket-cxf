package com.bielu.webservice.calculator;

import java.math.BigDecimal;

public enum Operation implements Calculable {

  ADD {
    @Override
    public BigDecimal calculate(BigDecimal left, BigDecimal right) {
      return left.add(right);
    }
  },
  
  MULTIPLY {
    @Override
    public BigDecimal calculate(BigDecimal left, BigDecimal right) {
      return left.multiply(right);
    }
  };
}
