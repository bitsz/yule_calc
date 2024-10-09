package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 商务部规则
 * @date 2024-05-17 20:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WineAndBeerRule {

  @ExcelProperty(value = {"待遇明细", "红酒、啤酒额外奖励", "提成点", "提成点"})
  //红酒、啤酒额外奖励提成点
  private BigDecimal wineAndBeerBonusPoint;

  @ExcelProperty(value = {"待遇明细", "红酒、啤酒额外奖励", "提成点", "是否当月发放"})
  //红酒、啤酒额外奖励提成点
  private String thisMonthHandOut;
}
