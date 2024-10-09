package com.hrzj.yule.calc.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author lifengquan
 * @Description 读取excel数据，然后读取ExcelProperty注解，注意必须加上index，通过反射机制读取数据
 * @Date 16:00 2024/4/18 星期四
 **/
public class ExcelMergeHelper<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExcelMergeHelper.class);

  /**
   * 返回解析后的List
   *
   * @return java.util.List<T> 解析后的List
   * @param: fileName 文件名
   * @param: clazz Excel对应属性名
   * @param: sheetNo 要解析的sheet
   * @param: headRowNumber 正文起始行
   */
  public List<T> getList(String fileName, Class<T> clazz, Integer sheetNo, Integer headRowNumber) {
    FieldEasyExcelListener<T> listener = new FieldEasyExcelListener<>(headRowNumber);
    try {
      EasyExcel.read(fileName, clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
    if (CollectionUtils.isEmpty(extraMergeInfoList)) {
      return listener.getData();
    }
    List<T> data = explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber);
    return data;
  }

  /**
   * 处理合并单元格
   *
   * @param data               解析数据
   * @param extraMergeInfoList 合并单元格信息
   * @param headRowNumber      起始行
   *
   * @return 填充好的解析数据
   */
  public List<T> explainMergeData(List<T> data, List<CellExtra> extraMergeInfoList, Integer headRowNumber) {
    //循环所有合并单元格信息
    extraMergeInfoList.forEach(cellExtra -> {
      int firstRowIndex = cellExtra.getFirstRowIndex() - headRowNumber;
      int lastRowIndex = cellExtra.getLastRowIndex() - headRowNumber;
      int firstColumnIndex = cellExtra.getFirstColumnIndex();
      int lastColumnIndex = cellExtra.getLastColumnIndex();
      //获取初始值
      Object initValue = getInitValueFromList(firstRowIndex, firstColumnIndex, data);
      //设置值
      for (int i = firstRowIndex; i <= lastRowIndex; i++) {
        for (int j = firstColumnIndex; j <= lastColumnIndex; j++) {
          setInitValueToList(initValue, i, j, data);
        }
      }
    });
    return data;
  }

  /**
   * 设置合并单元格的值
   *
   * @param filedValue  值
   * @param rowIndex    行
   * @param columnIndex 列
   * @param data        解析数据
   */
  public void setInitValueToList(Object filedValue, Integer rowIndex, Integer columnIndex, List<T> data) {
    T object = data.get(rowIndex);

    for (Field field : object.getClass().getDeclaredFields()) {
      //提升反射性能，关闭安全检查
      field.setAccessible(true);
      ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
      if (annotation != null) {
        if (annotation.index() == columnIndex) {
          try {
            field.set(object, filedValue);
            break;
          } catch (IllegalAccessException e) {
            LOGGER.error("设置合并单元格的值异常：" + e.getMessage());
          }
        }
      }
    }
  }


  /**
   * 获取合并单元格的初始值
   * rowIndex对应list的索引
   * columnIndex对应实体内的字段
   *
   * @param firstRowIndex    起始行
   * @param firstColumnIndex 起始列
   * @param data             列数据
   *
   * @return 初始值
   */
  private Object getInitValueFromList(Integer firstRowIndex, Integer firstColumnIndex, List<T> data) {
    Object filedValue = null;
    T object = data.get(firstRowIndex);
    for (Field field : object.getClass().getDeclaredFields()) {
      //提升反射性能，关闭安全检查
      field.setAccessible(true);
      ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
      if (annotation != null) {
        if (annotation.index() == firstColumnIndex) {
          try {
            filedValue = field.get(object);
            break;
          } catch (IllegalAccessException e) {
            LOGGER.error("设置合并单元格的初始值异常：" + e.getMessage());
          }
        }
      }
    }
    return filedValue;
  }
}
