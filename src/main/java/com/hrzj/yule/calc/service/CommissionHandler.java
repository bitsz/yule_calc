package com.hrzj.yule.calc.service;

import java.math.BigDecimal;

/**
 * @author lifengquan
 * @description: 调用链模式：提成处理器接口
 * @date 2024-04-25 17:19
 */
public interface CommissionHandler {

  BigDecimal handle(BigDecimal salesAmount);

  CommissionHandler getNextHandler();

  void setNextHandler(CommissionHandler nextHandler);
}
