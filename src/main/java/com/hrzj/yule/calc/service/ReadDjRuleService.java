package com.hrzj.yule.calc.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.hrzj.yule.calc.pojo.po.RuleDJAllField;
import com.hrzj.yule.calc.util.excel.ExcelMergeHelper;
import com.hrzj.yule.calc.util.excel.FieldEasyExcelListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;

import cn.hutool.core.io.FileUtil;

/**
 * @author lifengquan
 * @description: 读取dj规则
 * @date 2024-05-19 10:41
 */
public class ReadDjRuleService {

  public static final String line = "\\|";

  //最新规则数据
  private static final String read_file = "template\\维也纳待遇表(最新员工).xlsx";

  public static List<RuleDJAllField> obtainExcelData() {
    BufferedInputStream inputStream = FileUtil.getInputStream(new File(read_file));
    Integer sheetNo = 1;
    Integer headRowNumber = 3;

    FieldEasyExcelListener easyExcelListener = new FieldEasyExcelListener(headRowNumber);
    ExcelReaderBuilder read =
      EasyExcel.read(inputStream, RuleDJAllField.class, easyExcelListener).autoCloseStream(true);
    read
      .sheet(sheetNo)
      .headRowNumber(headRowNumber)
      .doRead();
    List<CellExtra> extraMergeInfoList = easyExcelListener.getExtraMergeInfoList();
    List<RuleDJAllField> data = new ExcelMergeHelper().explainMergeData(easyExcelListener.getData(), extraMergeInfoList,
      headRowNumber);
    return data;

  }
}
