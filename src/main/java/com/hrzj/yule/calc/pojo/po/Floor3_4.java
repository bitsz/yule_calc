package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 3-4楼
 * @date 2024/4/17 星期三 17:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Floor3_4 {

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "3-4楼", "95折买单"})
  //95折买单
  private Pay3_4_95 pay95s;

  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "3-4楼", "全单买单"})
  //全单买单
  private Pay3_4_100 pay100s;
}
