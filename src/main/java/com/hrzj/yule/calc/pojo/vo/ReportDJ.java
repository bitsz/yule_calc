package com.hrzj.yule.calc.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-19 12:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
@HeadRowHeight(25)
@ContentRowHeight(20)
public class ReportDJ implements Comparable<ReportDJ> {

  @ExcelProperty(value = "序号", index = 0)
  private Integer index;

  @ExcelProperty(value = "姓名", index = 1)
  private String name;

  @ExcelProperty(value = {"通提", "总业绩"}, index = 2)
  private BigDecimal totalSale;

  @ExcelProperty(value = {"通提", "比例"}, index = 3)
  private BigDecimal commonPoint;

  @ExcelProperty(value = {"通提", "金额"}, index = 4)
  private BigDecimal commonMoney;


  @ExcelProperty(value = {"跟台、小吃", "业绩"}, index = 5)
  private BigDecimal snackSale;


  @ExcelProperty(value = {"跟台、小吃", "比例"}, index = 6)
  private BigDecimal snackPoint;

  @ExcelProperty(value = {"跟台、小吃", "金额"}, index = 7)
  private BigDecimal snackMoeny;


  @ExcelProperty(value = {"奖励", "奖励"}, index = 8)
  private BigDecimal otherMoney;

  @ExcelProperty(value = {"奖励", "是否当月发放"}, index = 9)
  private String thisMonthHandOutOther;

  @ExcelProperty(value = {"合计"}, index = 10)
  private BigDecimal sum;

  @Override
  public int compareTo(@NotNull ReportDJ o) {
    int compareByRowIndex = this.name.compareTo(o.name);
    return compareByRowIndex;
  }
}
