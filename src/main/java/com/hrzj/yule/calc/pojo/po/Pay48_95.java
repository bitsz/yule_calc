package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/17 星期三 17:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pay48_95 {

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "95折买单", "计算方式"})
  //计算方式
  private String method;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "95折买单", "提成点"})
  //提成点
  private BigDecimal point;
}
