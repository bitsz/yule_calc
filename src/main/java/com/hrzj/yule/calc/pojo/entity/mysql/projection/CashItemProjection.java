package com.hrzj.yule.calc.pojo.entity.mysql.projection;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-06-05 11:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
@HeadRowHeight(30)
@ContentRowHeight(25)
@ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER, borderBottom =
  BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN, borderTop =
  BorderStyleEnum.THIN)
public class CashItemProjection {

  private String index;

  @ExcelProperty(value = "业务日期", index = 0)
  private String businessDay;
  //账单id
  @ExcelProperty(value = "流水号", index = 1)
  private String id;
  private String foodId;
  @ExcelProperty(value = "物品大类", index = 2)
  private String bigTypeName;
  @ExcelProperty(value = "物品小类", index = 3)
  private String foodTypeName;
  @ExcelProperty(value = "物品", index = 4)
  private String fName;
  @ExcelProperty(value = "单位", index = 5)
  private String fuName;
  @ExcelProperty(value = "数量", index = 6)
  private Integer quantity;
  @ExcelProperty(value = "价格", index = 7)
  private BigDecimal money;
  @ExcelProperty(value = "订房人", index = 8)
  private String empName;
  @ExcelProperty(value = "是否赠送", index = 9)
  private String present;
}
