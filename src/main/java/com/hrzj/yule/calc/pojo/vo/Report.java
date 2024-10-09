package com.hrzj.yule.calc.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.hrzj.yule.calc.config.enums.YesOrNoEnums;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-10 11:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
@HeadRowHeight(25)
@ContentRowHeight(20)
//属性注释在最下
//@ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER, borderBottom =
//  BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN, borderTop =
//  BorderStyleEnum.THIN)
public class Report implements Comparable<Report> {

  private Long empId;

  @ExcelProperty(value = "序号", index = 0)
  private Integer index;

  @ExcelProperty(value = "姓名", index = 1)
  private String nameWithGroup;

  @ExcelProperty(value = {"通提", "总业绩"}, index = 2)
  private BigDecimal totalSale;

  @ExcelProperty(value = {"通提", "比例"}, index = 3)
  private BigDecimal commonPoint;

  @ExcelProperty(value = {"通提", "金额"}, index = 4)
  private BigDecimal commonMoney;

  @ExcelProperty(value = {"备注单提成", "总业绩"}, index = 5)
  private BigDecimal totalSale2;

  @ExcelProperty(value = {"备注单提成", "金额"}, index = 6)
  private BigDecimal commonMoney2;


  @ExcelProperty(value = {"6000+酒水", "业绩"}, index = 7)
  private BigDecimal wine6000Sale;

  @ExcelProperty(value = {"6000+酒水", "比例"}, index = 8)
  private BigDecimal wine6000Point;

  @ExcelProperty(value = {"6000+酒水", "金额"}, index = 9)
  private BigDecimal wine6000Moeny;

  @ExcelProperty(value = {"跟台、小吃", "业绩"}, index = 10)
  private BigDecimal snackSale;


  @ExcelProperty(value = {"跟台、小吃", "比例"}, index = 11)
  private BigDecimal snackPoint;

  @ExcelProperty(value = {"跟台、小吃", "金额"}, index = 12)
  private BigDecimal snackMoeny;


  @ExcelProperty(value = {"红酒啤酒", "业绩"}, index = 13)
  private BigDecimal redWineSale;

  @ExcelProperty(value = {"红酒啤酒", "比例"}, index = 14)
  private BigDecimal redWinePoint;

  @ExcelProperty(value = {"红酒啤酒", "金额"}, index = 15)
  private BigDecimal redWineMoney;

  @ExcelProperty(value = {"红酒啤酒", "发放"}, index = 16)
  private String thisMonthHandOutRedBeer;

  @ExcelProperty(value = {"其他额外奖励", "绩效奖励"}, index = 17)
  private BigDecimal otherMoney;

  @ExcelProperty(value = {"其他额外奖励", "备注单奖励"}, index = 18)
  private BigDecimal specialMoney;

  @ExcelProperty(value = {"其他额外奖励", "发放"}, index = 19)
  private String thisMonthHandOutOther;


  @ExcelProperty(value = {"洋酒奖励", "业绩"}, index = 20)
  private BigDecimal importedWineSale;

  @ExcelProperty(value = {"洋酒奖励", "比例/元"}, index = 21)
  private String importedWinePoint;

  @ExcelProperty(value = {"洋酒奖励", "洋酒金额"}, index = 22)
  private BigDecimal importedWineMoney;

  //是否当月发放
  @ExcelProperty(value = {"洋酒奖励", "发放"}, index = 23)
  private String thisMonthHandOutImport;


  @ExcelProperty(value = {"公司酒水活动奖励", "金额"}, index = 24)
  private BigDecimal companyActiveMoney;

  @ExcelProperty(value = {"公司酒水活动奖励", "备注单奖励"}, index = 25)
  private BigDecimal companyActiveSpecialMoney;

  @ExcelProperty(value = {"合计", "不发合计"}, index = 26)
  @ContentFontStyle(bold = BooleanEnum.TRUE)
  private BigDecimal noPaymentMoney;

  @ExcelProperty(value = {"合计", "应发合计"}, index = 27)
  @ContentFontStyle(bold = BooleanEnum.TRUE)
  private BigDecimal sum;


  @Override
  public int compareTo(Report o) {

    int compareByRowIndex = this.nameWithGroup.compareTo(o.nameWithGroup);
    return compareByRowIndex;
  }

  public BigDecimal noPaymentMoney() {
    BigDecimal noPaymentMoney = BigDecimal.ZERO;
    if (StringUtils.isNotBlank(getThisMonthHandOutRedBeer()) && getThisMonthHandOutRedBeer().equals(YesOrNoEnums.no.getValue())) {
      BigDecimal redWineMoney = getRedWineMoney();
      noPaymentMoney = noPaymentMoney.add(redWineMoney);
    }
    if (StringUtils.isNotBlank(getThisMonthHandOutOther()) && getThisMonthHandOutOther().equals(YesOrNoEnums.no.getValue())) {
      BigDecimal otherMoney = getOtherMoney();
      noPaymentMoney = noPaymentMoney.add(otherMoney);
    }
    if (StringUtils.isNotBlank(getThisMonthHandOutImport()) && getThisMonthHandOutImport().equals(YesOrNoEnums.no.getValue())) {
      BigDecimal importedWineMoney = getImportedWineMoney();
      noPaymentMoney = noPaymentMoney.add(importedWineMoney);
    }
    return noPaymentMoney;
  }


}
