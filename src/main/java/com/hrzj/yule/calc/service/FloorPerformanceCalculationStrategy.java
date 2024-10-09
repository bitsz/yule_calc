package com.hrzj.yule.calc.service;

import com.hrzj.yule.calc.config.enums.Floor;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.po.CompanyActivityRule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author lifengquan
 * @description: 楼层业绩计算的策略
 * @date 2024-04-28 10:31
 */
public interface FloorPerformanceCalculationStrategy {
  static final Logger log = LoggerFactory.getLogger(FloorPerformanceCalculationStrategy.class);

  static BigDecimal getCommission(Cash cash, BigDecimal payMoney, String method, BigDecimal value) {
    if (StringUtils.isBlank(method)) {
      return BigDecimal.ZERO;
    }
    BigDecimal commission;
    switch (method) {
      case "百分比":
      case "%":
        commission = payMoney.multiply(value).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        break;
      case "固定值":
        commission = value;
        break;
      default:
        return BigDecimal.ZERO;
    }


    return commission;
  }

  BigDecimal calculatePerformance(Cash cash, CompanyActivityRule companyActivityRule, BigDecimal payMoney,
                                  BigDecimal discount, Floor floor);
}
