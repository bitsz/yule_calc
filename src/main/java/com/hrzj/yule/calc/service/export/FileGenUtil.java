package com.hrzj.yule.calc.service.export;

import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.util.excel.style.ExcelCellWidthStyleStrategy;
import com.hrzj.yule.calc.util.excel.style.StyleUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.StrPool.BACKSLASH;

/**
 * @author lifengquan
 * @description: 生成文件
 * @date 2024-05-17 13:54
 */
@Slf4j
public class FileGenUtil {

  public static OutputStream genFile(String fileName) {
    File file = new File("report".concat(BACKSLASH).concat(fileName));
    OutputStream outputStream = null;
    try {
      outputStream = Files.newOutputStream(file.toPath());
      return outputStream;
    } catch (IOException e) {
      log.error("导出失败:{}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static ExcelCellWidthStyleStrategy getExcelCellWidthStyleStrategy() {
    boolean runningAsJar = Application.isRunningAsJar();
    // 列宽策略设置
    ExcelCellWidthStyleStrategy widthStyleStrategy = new ExcelCellWidthStyleStrategy(runningAsJar);
    return widthStyleStrategy;
  }

  public static HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
    // 设置单元格样式
    HorizontalCellStyleStrategy horizontalCellStyleStrategy =
      new HorizontalCellStyleStrategy(StyleUtils.getHeadStyle(), StyleUtils.getContentStyle());
    return horizontalCellStyleStrategy;
  }
}
