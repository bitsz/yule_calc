package com.hrzj.yule.calc.service.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hrzj.yule.calc.mapper.mssql.DbMapper;
import com.hrzj.yule.calc.mapper.mssql.EmployeeMapper;
import com.hrzj.yule.calc.mapper.mysql.SalesEmployeeMapper;
import com.hrzj.yule.calc.pojo.entity.mssql.Employee;
import com.hrzj.yule.calc.pojo.entity.mysql.CashEmployee;
import com.hrzj.yule.calc.pojo.vo.EmployeeVO;
import com.hrzj.yule.calc.util.DbContextHolder;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 16:52
 */
@Slf4j
@Component
public class TemplateAppend {

  //追加 员工sheet后的规则 （最新规则数据）
  public static final String outputFilePath = "template\\维也纳待遇表(最新员工).xlsx";
  //提成规则
  private static final String inputFilePath = "template\\维也纳待遇表(初始模板勿动).xlsx";
  @Autowired
  private EmployeeMapper employeeMapper;
  @Autowired
  private SalesEmployeeMapper salesEmployeeMapper;

  @Autowired
  private DbMapper dbMapper;

  public Boolean gen() {
    String db = DbContextHolder.getDB();
    if (null == db) {
      List<String> dbList = dbMapper.findDb();
      if (CollectionUtils.isNotEmpty(dbList)) {
        db = dbList.get(0);
      }
    }

    if (null != db) {
      List<Employee> employees = employeeMapper.findByEEnable(db, Boolean.TRUE);
      List<EmployeeVO> employeeVOList = new ArrayList<>();
      List<CashEmployee> cashEmployeeList = new ArrayList<>();
      for (Employee employee : employees) {
        employeeVOList.add(EmployeeVO.builder().id(employee.getId()).eName(String.valueOf(employee.getId()).concat
          ("|") + employee.getEName()).esName(employee.getEmployeeStation().getEsName()).build());
        cashEmployeeList.add(CashEmployee.builder().id(employee.getId()).
          empName(employee.getEName()).
          empEnable(employee.isEEnable()).
          esId(employee.getEmployeeStation().getId()).
          esName(employee.getEmployeeStation().getEsName()).
          isDj(employee.getEName().contains("DJ")).
          time(DateUtil.now()).
          build());
      }
      int batchSize = 50;

      List<List<CashEmployee>> batches = ListUtil.split(cashEmployeeList, batchSize);
      salesEmployeeMapper.clear();
      for (List<CashEmployee> batch : batches) {
        salesEmployeeMapper.saves(batch);
      }

      // 临时文件
      String tmpFile = "template\\维也纳待遇表(最新员工)_tmp.xlsx";
      Path path = Paths.get(outputFilePath);
      boolean exist = Files.exists(path);
      if (exist) {
        // 文件存在时，创建一个新的 Excel 文件，将原有的文件内容复制到新的文件中，但跳过要覆盖的 sheet
        ExcelReaderBuilder readerBuilder = EasyExcel.read(outputFilePath);
        List<String> sheetNames = new ArrayList<>();
        readerBuilder.build().excelExecutor().sheetList().forEach(sheet -> {
          if (!"员工".equals(sheet.getSheetName())) {
            sheetNames.add(sheet.getSheetName());
          }
        });

        ExcelWriter excelWriter = EasyExcel.write(tmpFile).build();
        for (String sheetName : sheetNames) {
          ExcelReaderSheetBuilder sheetReader = EasyExcel.read(outputFilePath).sheet(sheetName);
          List<Object> data = sheetReader.doReadSync();
          WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
          excelWriter.write(data, writeSheet);
        }
        excelWriter.finish();
      }

      // 写入新的数据到 "员工" sheet
      ExcelWriterBuilder writerBuilder = EasyExcel.write(exist ? tmpFile : outputFilePath, EmployeeVO.class)
        .needHead(false)
        .autoCloseStream(true);

      writerBuilder.withTemplate(inputFilePath).file(exist ? tmpFile : outputFilePath).sheet("员工").doWrite(employeeVOList);
      // 重命名临时文件
      if (exist) {
        FileUtil.rename(Paths.get(tmpFile), "维也纳待遇表(最新员工).xlsx", true);
      }

      DataValidationHandler.handler(outputFilePath);
      return true;
    } else {
      return false;
    }
  }
}
