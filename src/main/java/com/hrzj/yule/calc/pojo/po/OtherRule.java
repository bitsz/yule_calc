package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hrzj.yule.calc.config.enums.Method;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 其他额外奖励
 * @date 2024/4/17 星期三 17:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtherRule {

  @ExcelProperty(value = {"待遇明细", "其他额外奖励", "合并业绩"})
  private String mergeOtherGroup;

  @ExcelProperty(value = {"待遇明细", "其他额外奖励", "计算方式"})
  //计算方式
  private String method;

  private Method methodEnum;

  @ExcelProperty(value = {"待遇明细", "其他额外奖励", "提成点"})
  //提成点
  private BigDecimal point;

  @ExcelProperty(value = {"待遇明细", "其他额外奖励", "条件(万)"})
  //条件 单位万元
  private BigDecimal condition = BigDecimal.ZERO;

  @ExcelProperty(value = {"待遇明细", "其他额外奖励", "是否当月发放"})
  private String thisMonthHandOutOther;

  public OtherRule(String method, BigDecimal point, BigDecimal condition) {
    this.method = method;
    this.point = point;
    this.condition = condition;
  }

  public OtherRule(String mergeOtherGroup, String method, BigDecimal point, BigDecimal condition,
                   String thisMonthHandOutOther) {
    this.mergeOtherGroup = mergeOtherGroup;
    this.method = method;
    this.point = point;
    this.condition = condition;
    this.thisMonthHandOutOther = thisMonthHandOutOther;
  }

  public Method getMethodEnum() {
    return Method.get(getMethod());
  }

}
