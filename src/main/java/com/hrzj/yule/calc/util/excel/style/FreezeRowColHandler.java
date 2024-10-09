package com.hrzj.yule.calc.util.excel.style;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author lifengquan
 * @description: 自定义Handle:（自定义设置导出excel设置冻结列和列以及是否自动加筛选器）
 * @date 2024-05-17 9:14
 */
public class FreezeRowColHandler implements SheetWriteHandler {

  private final FreezeRowColOptions options;

  public FreezeRowColHandler(FreezeRowColOptions options) {
    this.options = options;
  }

  @Override
  public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    Sheet sheet = writeSheetHolder.getSheet();
    sheet.createFreezePane(options.getColSplit(), options.getRowSplit(), options.getLeftmostColumn(),
      options.getTopRow());
    if (null != options.getAutoFilterRange()) {
      sheet.setAutoFilter(CellRangeAddress.valueOf(options.getAutoFilterRange()));
    }
  }

}
