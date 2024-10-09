package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.service.CommissionCalculationStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;

/**
 * @author lifengquan
 * @description: 具体策略：百分比提成策略
 * @date 2024-04-25 17:18
 */
@Data
public class PercentageCommissionStrategy implements CommissionCalculationStrategy {

  private BigDecimal percentage;

  public PercentageCommissionStrategy(BigDecimal percentage) {
    this.percentage = percentage;
  }

  @Override
  public BigDecimal calculateCommission(BigDecimal salesAmount) {
    return salesAmount.multiply(percentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
  }
}
