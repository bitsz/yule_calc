package com.hrzj.yule.calc.service.calc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 销售员工类
 * @date 2024-04-25 17:37
 */
@Data
@Slf4j
public abstract class SalesEmployeeBase<T> {

  private String name;

  private Map<SalesItem, BigDecimal> sales;


  public SalesEmployeeBase(String name) {
    this.name = name;
    this.sales = new HashMap<>();
  }

  public void addSalesItem(SalesItem item, BigDecimal salesAmount) {
    sales.put(item, salesAmount);
  }


  public abstract T calculateTotalCommission();
}
