package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 公司酒水活动奖励规则
 * @date 2024/4/17 星期三 17:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyActivityRule {

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "3-4楼"})
  //3-4楼
  private Floor3_4 floor3_4;


  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼"})
  //48楼
  private Floor48 floor48;
}
