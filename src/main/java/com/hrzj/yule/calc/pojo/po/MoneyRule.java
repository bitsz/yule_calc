package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/17 星期三 17:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRule {

  @ExcelProperty(value = {"id"}, index = 0)
  private Long id;
  //姓名
  @ExcelProperty(value = {"姓名"}, index = 1)
  private String name;

  private String teamName;

  private String teamNameWithName;

  //待遇明细
  @ExcelProperty(value = {"待遇明细"})
  private RoyaltyDetail royaltyDetail;


}
