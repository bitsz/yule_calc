package com.hrzj.yule.calc.service.calc;

import com.hrzj.yule.calc.config.enums.Floor;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.pojo.po.CompanyActivityRule;
import com.hrzj.yule.calc.pojo.po.Floor3_4;
import com.hrzj.yule.calc.pojo.po.Floor48;
import com.hrzj.yule.calc.pojo.po.Pay3_4_100;
import com.hrzj.yule.calc.pojo.po.Pay3_4_95;
import com.hrzj.yule.calc.pojo.po.Pay48_100;
import com.hrzj.yule.calc.pojo.po.Pay48_95;
import com.hrzj.yule.calc.service.FloorPerformanceCalculationStrategy;

import java.math.BigDecimal;


/**
 * @author lifengquan
 * @description: 楼层业绩提成计算策略
 * @date 2024-04-28 10:32
 */
public class FloorPerformanceCalculationStrategyImpl implements FloorPerformanceCalculationStrategy {

  @Override
  public BigDecimal calculatePerformance(Cash cash, CompanyActivityRule companyActivityRule, BigDecimal payMoney,
                                         BigDecimal discount, Floor floor) {

    if (null != companyActivityRule) {
      String method = null;
      BigDecimal value = null;
      Floor3_4 floor34 = companyActivityRule.getFloor3_4();
      switch (floor) {
        case F3:
        case F4:
          if (discount.compareTo(new BigDecimal(100)) == 0) {
            Pay3_4_100 pay100s = floor34.getPay100s();
            if (null == pay100s) {
              return BigDecimal.ZERO;
            }
            method = pay100s.getMethod();
            value = pay100s.getPoint();
          } else {
            Pay3_4_95 pay95s = floor34.getPay95s();
            if (null == pay95s) {
              return BigDecimal.ZERO;
            }
            method = pay95s.getMethod();
            value = pay95s.getPoint();
          }
          break;
        case F48:
          Floor48 floor48 = companyActivityRule.getFloor48();
          if (discount.compareTo(new BigDecimal(100)) == 0) {
            if (null != floor48) {
              Pay48_100 pay48100 = floor48.getPay48_100();
              method = pay48100.getMethod();
              value = pay48100.getPoint();
            }
          } else {
            if (null != floor48) {
              Pay48_95 pay4895 = floor48.getPay48_95();
              method = pay4895.getMethod();
              value = pay4895.getPoint();
            }
          }
          break;
      }
      BigDecimal commission = FloorPerformanceCalculationStrategy.getCommission(cash, payMoney, method, value);
      log.info("公司酒水活动 {} {},原价{} 实际支付 {},支付比例{} 提成方式 {}{} 提成{}", floor.name(), cash.getId(), cash.getCTrueMoney(),
        payMoney,
        discount,
        value,
        method,
        commission);
      return commission;

    } else {
      return BigDecimal.ZERO;
    }
  }
}
