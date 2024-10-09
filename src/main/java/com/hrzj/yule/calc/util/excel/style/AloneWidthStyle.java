package com.hrzj.yule.calc.util.excel.style;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;

import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 单独设置列宽
 * @date 2024-05-18 11:49
 */
public class AloneWidthStyle extends AbstractColumnWidthStyleStrategy {


  private List<Column> columns;

  private int columnIndex;
  private int width;

  public AloneWidthStyle(List<Column> columns) {
    this.columns = columns;
  }

  @Override
  protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                Head head, Integer relativeRowIndex, Boolean isHead) {
    for (Column column : columns) {
      Map<Integer, Integer> columnWidthMap = new HashMap<>();
      columnWidthMap.put(column.getColumnIndex(), column.getWidth());
      // 256是因为EasyExcel中宽度的单位不是字符宽度，而是256的倍数
      writeSheetHolder.getSheet().setColumnWidth(column.getColumnIndex(), (short) (column.getWidth() * 256));
    }

  }

  @Builder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Column {
    private int columnIndex;
    private int width;
  }

}
