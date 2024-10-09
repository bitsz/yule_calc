package com.hrzj.yule.calc.service.export;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hrzj.yule.calc.pojo.vo.Report;
import com.hrzj.yule.calc.pojo.vo.ReportDJ;
import com.hrzj.yule.calc.util.excel.style.AloneWidthStyle;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColHandler;
import com.hrzj.yule.calc.util.excel.style.FreezeRowColOptions;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.StrPool.BACKSLASH;

/**
 * @author lifengquan
 * @description: 导出业绩汇总报表
 * @date 2024-05-10 12:54
 */
@Slf4j
public class ExportReport {

  public static String export(List<Report> dataList, List<ReportDJ> djData, String fileName) {
    Collections.sort(dataList);
    AtomicInteger index = new AtomicInteger(1);
    for (Report report : dataList) {
      report.setIndex(index.getAndIncrement());
    }
    fileName = String.format("%s.xlsx", fileName);

    OutputStream outputStream = FileGenUtil.genFile(fileName);
    ExcelWriter excelWriter = EasyExcelFactory.write(outputStream).autoCloseStream(Boolean.TRUE).build();
    WriteSheet sheet1 = EasyExcelFactory.writerSheet(0, "商务部").head(Report.class).
      registerWriteHandler(FileGenUtil.getHorizontalCellStyleStrategy()).
      registerWriteHandler(FileGenUtil.getExcelCellWidthStyleStrategy()).
      registerWriteHandler(new AloneWidthStyle(new ArrayList<AloneWidthStyle.Column>() {
        {
          add(AloneWidthStyle.Column.builder().columnIndex(1).width(14).build());
          add(AloneWidthStyle.Column.builder().columnIndex(5).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(6).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(8).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(9).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(12).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(13).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(14).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(16).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(18).width(15).build());
          add(AloneWidthStyle.Column.builder().columnIndex(19).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(20).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(23).width(6).build());
        }
      })).
      registerWriteHandler(new FreezeRowColHandler(new FreezeRowColOptions(1, 2, 0, 0, null))).build();
    excelWriter.write(dataList, sheet1);


    Collections.sort(dataList);
    AtomicInteger indexDJ = new AtomicInteger(1);
    for (ReportDJ report : djData) {
      report.setIndex(indexDJ.getAndIncrement());
    }

    WriteSheet sheet2 = EasyExcelFactory.writerSheet(1, "DJ").head(ReportDJ.class)
      .registerWriteHandler(FileGenUtil.getHorizontalCellStyleStrategy())
      .registerWriteHandler(FileGenUtil.getExcelCellWidthStyleStrategy())
      .registerWriteHandler(new AloneWidthStyle(new ArrayList<AloneWidthStyle.Column>() {
        {
          add(AloneWidthStyle.Column.builder().columnIndex(1).width(15).build());
          add(AloneWidthStyle.Column.builder().columnIndex(6).width(6).build());
          add(AloneWidthStyle.Column.builder().columnIndex(8).width(10).build());
          add(AloneWidthStyle.Column.builder().columnIndex(10).width(15).build());
        }
      }))
      .registerWriteHandler(new FreezeRowColHandler(new FreezeRowColOptions(1, 2, 0, 0, null))).build();
    excelWriter.write(djData, sheet2);
    excelWriter.finish();

    return "report".concat(BACKSLASH).concat(fileName);

  }


}
