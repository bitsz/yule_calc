package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-16 17:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pay48_100 {

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "全单买单", "计算方式"})
  //计算方式
  private String method;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "全单买单", "提成点"})
  //提成点
  private BigDecimal point;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "全单买单", "是否包含跟台、小吃"})
  //是否包含跟台、小吃
  private String snack;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "全单买单", "排出"})
  //提成点
  private String filter;
}
