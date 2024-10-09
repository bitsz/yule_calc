package com.hrzj.yule.calc.util.excel;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;

import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 16:22
 */
public class ExcelWriteStreamHolder implements AutoCloseable {

  private List<List<Object>> list;
  private WriteSheetHolder writeSheetHolder;

  public ExcelWriteStreamHolder(boolean needHead) {
    this.list = Lists.newArrayList();
    this.writeSheetHolder = new WriteSheetHolder();
    this.writeSheetHolder.setNeedHead(needHead);
  }

  public WriteSheet getSheet() {
    return writeSheetHolder.getWriteSheet();
  }

  public List<List<Object>> getList() {
    return list;
  }

  public void add(List<Object> row) {
    list.add(row);
  }

  @Override
  public void close() throws Exception {
    if (list != null) {
      list.clear();
    }
  }
}
