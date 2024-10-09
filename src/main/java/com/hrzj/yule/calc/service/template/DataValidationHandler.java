package com.hrzj.yule.calc.service.template;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 给规则模板设置数据验证规则
 * @date 2024-05-11 11:22
 */
@Slf4j
public class DataValidationHandler {


  public static void handler(String file) {

    try {
      InputStream inputStream = new FileInputStream(file);
      Workbook workbook = WorkbookFactory.create(inputStream);
      Sheet sheet = workbook.getSheetAt(0);

      // 设置数据验证规则
      DataValidationHelper validationHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
      //String[] options = {"员工1", "员工2", "员工3"};
      // DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
      //表达式
      String combinedFormula = "=员工!$B:$B";
      DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(combinedFormula);
      CellRangeAddressList addressList = new CellRangeAddressList(6, 65535, 1, 1); // 第二列的下拉列表，从第6行到最后一行
      DataValidation validation = validationHelper.createValidation(constraint, addressList);
      sheet.addValidationData(validation);

      // 写回到文件
      try (OutputStream fileOut = new FileOutputStream(file)) {
        workbook.write(fileOut);
      }
      workbook.close();
    } catch (IOException e) {
      log.error("{},数据验证规则设置失败.{}", file, e.getMessage());
      throw new RuntimeException(e);
    }
    log.info("表格{},数据验证规则已成功设置！", file);
  }


}
