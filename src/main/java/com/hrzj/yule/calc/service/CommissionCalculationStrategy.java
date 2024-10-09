package com.hrzj.yule.calc.service;

import java.math.BigDecimal;

/**
 * @author lifengquan
 * @description: 拥金策略接口(可能是按照固定值 和 百分比)
 * @date 2024-04-25 17:17
 */
public interface CommissionCalculationStrategy {

  BigDecimal calculateCommission(BigDecimal salesAmount);
}
