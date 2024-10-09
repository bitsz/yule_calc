package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.service.CommissionCalculationStrategy;

import java.math.BigDecimal;

/**
 * @author lifengquan
 * @description: 具体策略：固定值提成策略
 * @date 2024-04-25 17:17
 */
public class FixedCommissionStrategy implements CommissionCalculationStrategy {


  private BigDecimal fixedAmount;

  public FixedCommissionStrategy(BigDecimal fixedAmount) {
    this.fixedAmount = fixedAmount;
  }

  @Override
  public BigDecimal calculateCommission(BigDecimal salesAmount) {
    return fixedAmount;
  }
}
