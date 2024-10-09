package com.hrzj.yule.calc.util.excel.style;


import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lifengquan
 * @description: 自适应列宽实现类
 * @date 2024-05-10 15:52
 */
public class ExcelCellWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {

  // 可以根据这里的最大宽度，按自己需要进行调整,搭配单元格样式实现类中的，自动换行，效果更好
  private static final int MAX_COLUMN_WIDTH = 50;
  private Map<Integer, Map<Integer, Integer>> CACHE = new HashMap(8);
  private Boolean runningAsJar;

  public ExcelCellWidthStyleStrategy(Boolean runningAsJar) {
    this.runningAsJar = runningAsJar;
  }

  @Override
  protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
                                Head head, Integer relativeRowIndex, Boolean isHead) {

    boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
    if (needSetWidth) {
      Map<Integer, Integer> maxColumnWidthMap = (Map) CACHE.get(writeSheetHolder.getSheetNo());
      if (maxColumnWidthMap == null) {
        maxColumnWidthMap = new HashMap(16);
        CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
      }

      Integer columnWidth = this.dataLength(cellDataList, cell, isHead, runningAsJar);
      if (columnWidth >= 0) {
        if (columnWidth > MAX_COLUMN_WIDTH) {
          columnWidth = MAX_COLUMN_WIDTH;
        }

        Integer maxColumnWidth = (Integer) ((Map) maxColumnWidthMap).get(cell.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
          ((Map) maxColumnWidthMap).put(cell.getColumnIndex(), columnWidth);
          writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
        }

      }
    }
  }


  private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead, boolean runningAsJar) {
    if (isHead) {
      if (runningAsJar) {
        //放大1.6倍
        return (int) Math.round((cell.getStringCellValue().getBytes().length) * 1.7);
      } else {
        return cell.getStringCellValue().getBytes().length;
      }
    } else {
      CellData cellData = (CellData) cellDataList.get(0);
      CellDataTypeEnum type = cellData.getType();
      if (type == null) {
        return -1;
      } else {
        switch (type) {
          case STRING:
            return cellData.getStringValue().getBytes().length;
          case BOOLEAN:
            return cellData.getBooleanValue().toString().getBytes().length;
          case NUMBER:
            return cellData.getNumberValue().toString().getBytes().length;
          default:
            return -1;
        }
      }
    }
  }

}
