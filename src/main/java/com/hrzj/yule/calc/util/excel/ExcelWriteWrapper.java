package com.hrzj.yule.calc.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.File;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 16:21
 */
public class ExcelWriteWrapper implements AutoCloseable {

  private ExcelWriteStreamHolder writeHolder;

  public ExcelWriteWrapper() {
    this.writeHolder = new ExcelWriteStreamHolder(false);
  }

  public WriteSheet getSheet() {
    return writeHolder.getSheet();
  }

  public void writeToFile(File file) throws Exception {
    EasyExcel.write(file).sheet();
  }

  @Override
  public void close() throws Exception {
    if (writeHolder != null) {
      writeHolder.close();
    }
  }
}
