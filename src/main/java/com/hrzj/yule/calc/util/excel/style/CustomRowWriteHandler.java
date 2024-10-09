package com.hrzj.yule.calc.util.excel.style;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.hrzj.yule.calc.pojo.entity.mysql.FixCash;
import com.hrzj.yule.calc.pojo.vo.CashReport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lifengquan
 * @description: 给单元格设置样式(给修正过的单元格添加批注与背景颜色)
 * @date 2024-06-07 10:02
 */
public class CustomRowWriteHandler implements RowWriteHandler {

  private final List<CashReport> cashReportList;

  private final List<FixCash> fixCashList;

  private final Map<Integer, String> columnIndices;

  private final Map<String, Integer> columnIndicesReverse;

  public CustomRowWriteHandler(List<CashReport> cashReportList, List<FixCash> fixCashList, Class<?> clazz) {
    this.cashReportList = cashReportList;
    this.fixCashList = fixCashList;
    this.columnIndices = extractColumnIndices(clazz);
    this.columnIndicesReverse = extractColumnIndicesReverse(clazz);
  }

  private Map<Integer, String> extractColumnIndices(Class<?> clazz) {
    Map<Integer, String> indices = new HashMap<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
      if (excelProperty != null) {
        indices.put(excelProperty.index(), excelProperty.value()[0]);
      }
    }
    return indices;
  }

  private Map<String, Integer> extractColumnIndicesReverse(Class<?> clazz) {
    Map<String, Integer> indices = new HashMap<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
      if (excelProperty != null) {
        indices.put(excelProperty.value()[0], excelProperty.index());
      }
    }
    return indices;
  }

  private Comment createComment(Workbook workbook, Cell cell, CreationHelper factory) {

    Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
    // 创建批注
    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 2);
    anchor.setRow1(cell.getRowIndex());
    anchor.setRow2(cell.getRowIndex() + 1);
    Comment comment = drawing.createCellComment(anchor);
    return comment;
  }

  @Override
  public void afterRowDispose(RowWriteHandlerContext context) {
    Row row = context.getRow();
    int rowIndex = context.getRowIndex();
    Sheet sheet = row.getSheet();
    Workbook workbook = sheet.getWorkbook();

    if (context.getHead()) {
      return;
    }

    AtomicReference<Comment> comment = new AtomicReference<>();
    CreationHelper factory = workbook.getCreationHelper();
    CashReport cashReport = cashReportList.get(rowIndex - 1);
    fixCashList.stream().filter(fixCash -> fixCash.getCashId().equals(cashReport.getCashId())).findFirst().ifPresent(fixCash -> {
      for (Cell cell : row) {


        Boolean flag = false;
        int columnIndex = cell.getColumnIndex();
        if (columnIndices.containsKey(columnIndex)) {
          String head = columnIndices.get(columnIndex);

          switch (head) {
            case "跟台":

              if ((null == fixCash.getSubjoinSale() ? BigDecimal.ZERO :
                fixCash.getSubjoinSale()).compareTo((null == cashReport.getSubjoinSale() ? BigDecimal.ZERO :
                cashReport.getSubjoinSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getSubjoinSale() ? "0" :
                  fixCash.getSubjoinSale().toString())));
                flag = true;

                RichTextString c = factory.createRichTextString(null == cashReport.getSubjoinSale() ? null :
                  cashReport.getSubjoinSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "6000+酒":
              if ((null == fixCash.getImportWine6000Sale() ? BigDecimal.ZERO :
                fixCash.getImportWine6000Sale()).compareTo((null == cashReport.getImportWine6000Sale() ?
                BigDecimal.ZERO :
                cashReport.getImportWine6000Sale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getImportWine6000Sale() ? "0" :
                  fixCash.getImportWine6000Sale().toString())));
                flag = true;
                RichTextString c = factory.createRichTextString(null == cashReport.getImportWine6000Sale() ? null :
                  cashReport.getImportWine6000Sale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "洋酒业绩":
              if ((null == fixCash.getImportWineSale() ? BigDecimal.ZERO :
                fixCash.getImportWineSale()).compareTo((null == cashReport.getImportWineSale() ? BigDecimal.ZERO :
                cashReport.getImportWineSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getImportWineSale() ? "0" :
                  fixCash.getImportWineSale().toString())));
                flag = true;
                RichTextString c = factory.createRichTextString(null == cashReport.getImportWineSale() ? null :
                  cashReport.getImportWineSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "红酒业绩":
              if ((null == fixCash.getRedWineSale() ? BigDecimal.ZERO :
                fixCash.getRedWineSale()).compareTo((null == cashReport.getRedWineSale() ? BigDecimal.ZERO :
                cashReport.getRedWineSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getRedWineSale() ? "0" :
                  fixCash.getRedWineSale().toString())));
                flag = true;
                RichTextString c =
                  factory.createRichTextString(null == cashReport.getRedWineSale() ? null :
                    cashReport.getRedWineSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "啤酒业绩":
              if ((null == fixCash.getBeerSale() ? BigDecimal.ZERO :
                fixCash.getBeerSale()).compareTo((null == cashReport.getBeerSale() ? BigDecimal.ZERO :
                cashReport.getBeerSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getBeerSale() ? "0" :
                  fixCash.getBeerSale().toString())));
                flag = true;
                RichTextString c =
                  factory.createRichTextString(null == cashReport.getBeerSale() ? null
                    : cashReport.getBeerSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "小吃":
              if ((null == fixCash.getSnacksSale() ? BigDecimal.ZERO :
                fixCash.getSnacksSale()).compareTo((null == cashReport.getSnacksSale() ? BigDecimal.ZERO :
                cashReport.getSnacksSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getSnacksSale() ? "0" :
                  fixCash.getSnacksSale().toString())));
                flag = true;
                RichTextString c = factory.createRichTextString(null == cashReport.getSnacksSale() ? null :
                  cashReport.getSnacksSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "其他业绩":
              if ((null == fixCash.getOtherSale() ? BigDecimal.ZERO :
                fixCash.getOtherSale()).compareTo((null == cashReport.getOtherSale() ? BigDecimal.ZERO :
                cashReport.getOtherSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getOtherSale() ? "0" :
                  fixCash.getOtherSale().toString())));
                flag = true;
                RichTextString c = factory.createRichTextString(null == cashReport.getOtherSale() ? null :
                  cashReport.getOtherSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "业绩小计":
              if ((null == fixCash.getSumSale() ? BigDecimal.ZERO :
                fixCash.getSumSale()).compareTo((null == cashReport.getSumSale() ? BigDecimal.ZERO :
                cashReport.getSumSale())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getSumSale() ? "0" :
                  fixCash.getSumSale().toString())));
                flag = true;
                RichTextString c =
                  factory.createRichTextString(null == cashReport.getSumSale() ? null :
                    cashReport.getSumSale().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
            case "赔偿":
              if ((null == fixCash.getCompensation() ? BigDecimal.ZERO :
                fixCash.getCompensation()).compareTo((null == cashReport.getCompensation() ? BigDecimal.ZERO :
                cashReport.getCompensation())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getCompensation() ? "0" :
                  fixCash.getCompensation().toString())));
                flag = true;
                RichTextString c =
                  factory.createRichTextString(null == cashReport.getCompensation() ? null :
                    cashReport.getCompensation().toString());
                createComment(workbook, cell, factory).setString(c);
              }
              break;
            case "火腿":
              if ((null == fixCash.getHam() ? BigDecimal.ZERO :
                fixCash.getHam()).compareTo((null == cashReport.getHam() ? BigDecimal.ZERO :
                cashReport.getHam())) != 0) {
                cell.setCellValue(Double.valueOf(new String(null == fixCash.getHam() ? "0" :
                  fixCash.getHam().toString())));
                flag = true;
                RichTextString c =
                  factory.createRichTextString(null == cashReport.getHam() ? null : cashReport.getHam().toString());
                comment.set(createComment(workbook, cell, factory));
                comment.get().setString(c);
              }
              break;
//            case "合计":
//              if ((null == fixCash.getTotal() ? BigDecimal.ZERO :
//                fixCash.getTotal()).compareTo((null == cashReport.getTotal() ? BigDecimal.ZERO :
//                cashReport.getTotal())) != 0) {
//                cell.setCellValue(Double.valueOf(new String(fixCash.getTotal().toString())));
//                flag = true;
//                RichTextString c =
//                  factory.createRichTextString(null == cashReport.getTotal() ? null : cashReport.getTotal()
//                  .toString());
//                comment.set(createComment(workbook, cell, factory));
//                comment.get().setString(c);
//              }
//              break;

//            case "折扣":
//              if ((null == fixCash.getDiscount() ? BigDecimal.ZERO :
//                fixCash.getDiscount()).compareTo((null == cashReport.getDiscount() ? BigDecimal.ZERO :
//                cashReport.getDiscount())) != 0) {
//                cell.setCellValue(Double.valueOf(new String(fixCash.getDiscount().toString())));
//                flag = true;
//                RichTextString c =
//                  factory.createRichTextString(null == cashReport.getDiscount() ? null :
//                    cashReport.getDiscount().toString());
//                comment.set(createComment(workbook, cell, factory));
//                comment.get().setString(c);
//              }
//              break;
            default:
              break;
          }
          if (flag) {


            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(cell.getCellStyle());
            // 设置背景颜色
            newCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(newCellStyle);
            // 将批注赋给单元格
            cell.setCellComment(comment.get());
          }
        }
      }
    });
  }
}









