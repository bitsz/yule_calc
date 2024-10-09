package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/17 星期三 17:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Floor48 {


  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "95折买单"})
  //95折及以上
  private Pay48_95 pay48_95;


  @ExcelProperty(value = {"待遇明细", "公司酒水活动奖励", "48楼", "全单买单"})
  //95折及以上
  private Pay48_100 pay48_100;

}
