package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hrzj.yule.calc.config.enums.Method;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 进口洋酒奖励规则
 * @date 2024/4/17 星期三 17:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportedRule {

  //计算方式
  @ExcelProperty(value = {"待遇明细", "洋酒奖励", "计算方式"})
  private String method;

  @ExcelProperty(value = {"待遇明细", "洋酒奖励", "提成点/固定元"})
  //提成点
  private BigDecimal point;

  @ExcelProperty(value = {"待遇明细", "洋酒奖励", "条件(万)"})
  //条件 单位万元
  private BigDecimal condition;

  @ExcelProperty(value = {"待遇明细", "洋酒奖励", "滤出"})
  private String filter;

  @ExcelProperty(value = {"待遇明细", "洋酒奖励", "是否当月发放"})
  private String thisMonthHandOutImport;


  private Method methodEnum;

  public ImportedRule(String method, BigDecimal point, BigDecimal condition, String filter,
                      String thisMonthHandOutImport) {
    this.method = method;
    this.point = point;
    this.condition = condition;
    this.filter = filter;
    this.thisMonthHandOutImport = thisMonthHandOutImport;
  }

  public Method getMethodEnum() {
    return Method.get(getMethod());
  }
}
