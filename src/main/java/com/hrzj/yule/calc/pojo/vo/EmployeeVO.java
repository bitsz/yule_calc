package com.hrzj.yule.calc.pojo.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/19 星期五 9:58
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@HeadRowHeight(25)
public class EmployeeVO {

  @ExcelProperty(index = 0)
  @ColumnWidth(15)
  @ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.CENTER)
  private Long id;

  @ExcelProperty(index = 1)
  @ColumnWidth(30)
  @ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.CENTER)
  @ContentFontStyle(bold = BooleanEnum.TRUE)
  private String eName;

  @ExcelProperty(index = 2)
  @ColumnWidth(15)
  @ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.CENTER)
  private String esName;
}
