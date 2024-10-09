package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: JD规则
 * @date 2024-05-19 10:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class RuleDJAllField {

  private String index;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "通提", "百分比"}, index = 0)
  private BigDecimal djCommonPoint;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "通提", "条件（万）"}, index = 1)
  private BigDecimal djCommonCondition;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "通提", "是否包含   跟台、小吃"}, index = 2)
  private String djCommonSnack;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "通提", "跟台、小吃（百分比）"}, index = 3)
  private BigDecimal djCommonSnackPoint;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "奖励", "奖励"}, index = 4)
  private BigDecimal djAward;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "奖励", "条件（万）"}, index = 5)
  private BigDecimal djAwardCondition;

  @ExcelProperty(value = {"维也纳DJ部待遇明细", "奖励   是否当月发放"}, index = 6)
  private String djAwardIsHandOut;
}
