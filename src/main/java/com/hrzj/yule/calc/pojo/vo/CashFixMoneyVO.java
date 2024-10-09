package com.hrzj.yule.calc.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-17 15:30
 */
@Data
public class CashFixMoneyVO {

  private String index;

  //账单id
  @ExcelProperty(index = 0)
  private Long cashId;

  //实际金额
  @ExcelProperty(index = 1)
  private BigDecimal money;

  //无奖励
  @ExcelProperty(index = 2)
  private String noReward;

  //目标奖励
  @ExcelProperty(index = 3)
  private BigDecimal rewardOther;

  //活动奖励
  @ExcelProperty(index = 4)
  private BigDecimal rewardActive;

  //提成
  @ExcelProperty(index = 5)
  private BigDecimal commission;

  //备注
  @ExcelProperty(index = 6)
  private String remark;

  //vip
  @ExcelProperty(index = 7)
  private String vip;


}
