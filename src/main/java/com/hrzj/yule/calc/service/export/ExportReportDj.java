package com.hrzj.yule.calc.service.export;

import com.alibaba.excel.EasyExcelFactory;
import com.hrzj.yule.calc.pojo.vo.ReportDJ;
import com.hrzj.yule.calc.util.excel.style.AloneWidthStyle;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColConstant;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColHandler;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.StrPool.BACKSLASH;

/**
 * @author lifengquan
 * @description: DJ业绩
 * @date 2024-05-19 12:57
 */
@Slf4j
public class ExportReportDj {

  public static String export(List<ReportDJ> dataList, String fileName) {
    Collections.sort(dataList);
    AtomicInteger index = new AtomicInteger(1);
    for (ReportDJ report : dataList) {
      report.setIndex(index.getAndIncrement());
    }
    fileName = String.format("%s.xlsx", fileName);

    OutputStream outputStream = FileGenUtil.genFile(fileName);

    EasyExcelFactory.write(outputStream, ReportDJ.class).
      registerWriteHandler(FileGenUtil.getHorizontalCellStyleStrategy()).
      registerWriteHandler(FileGenUtil.getExcelCellWidthStyleStrategy()).
      registerWriteHandler(new AloneWidthStyle(new ArrayList<AloneWidthStyle.Column>() {
        {
          add(AloneWidthStyle.Column.builder().columnIndex(1).width(15).build());
          add(AloneWidthStyle.Column.builder().columnIndex(6).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(8).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(10).width(15).build());
        }
      })).
      registerWriteHandler(new FreezeRowColHandler(FreezeRowColConstant.noRange)).
      autoCloseStream(Boolean.TRUE).sheet(fileName).
      doWrite(dataList);

    return "report".concat(BACKSLASH).concat(fileName);

  }
}
