package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.SalesType;
import com.hrzj.yule.calc.service.CommissionCalculationStrategy;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author lifengquan
 * @description: 具体销售项目
 * @date 2024-04-25 17:35
 */
@Data
public class SalesItem {

  private SalesType salesType;

  private CommissionCalculationStrategy commissionStrategy;

  public SalesItem(SalesType SalesType, CommissionCalculationStrategy commissionStrategy) {
    this.salesType = SalesType;
    this.commissionStrategy = commissionStrategy;
  }

  public BigDecimal calculateCommission(BigDecimal salesAmount) {
    if (commissionStrategy != null) {
      return commissionStrategy.calculateCommission(salesAmount);
    } else {
      return BigDecimal.ZERO;
    }
  }
}
