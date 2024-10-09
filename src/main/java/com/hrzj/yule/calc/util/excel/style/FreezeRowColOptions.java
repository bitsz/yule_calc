package com.hrzj.yule.calc.util.excel.style;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 举例：
 * CreateFreezePane(0,1,0,1):冻结第一行,冻结行下侧第一行的左边框显示“2”
 * CreateFreezePane(1,0,1,0):冻结第一列，冻结列右侧的第一列为B列
 * CreateFreezePane(2,0,5,0):冻结左侧两列，冻结列右侧的第一列为F列
 * @date 2024-05-17 9:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreezeRowColOptions {

  /**
   * 表示要冻结的列数
   */
  private Integer colSplit;
  /**
   * 表示要冻结的行数
   */
  private Integer rowSplit;
  /**
   * 表示被固定列右边第一列的列号
   */
  private Integer leftmostColumn;
  /**
   * 表示被固定行下边第一列的行号
   */
  private Integer topRow;
  /**
   * 自动过滤范围
   */
  private String autoFilterRange;

}
