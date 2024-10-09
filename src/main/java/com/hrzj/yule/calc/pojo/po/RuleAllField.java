package com.hrzj.yule.calc.pojo.po;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/18 星期四 11:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class RuleAllField {


  private String index;

  @ExcelProperty(index = 0)
  private String id;

  //姓名/组名
  @ExcelProperty(index = 1)
  private String nameWithGroup;

  //通提提成点(百分比)
  @ExcelProperty(index = 2)
  private BigDecimal commonPoint;

  //排除
  @ExcelProperty(index = 3)
  private String commonFilter;

  //6000+酒水提成（百分比）
  @ExcelProperty(index = 4)
  private BigDecimal common6000Point;

  //是否包含跟台、小吃
  @ExcelProperty(index = 5)
  private String commonSnack;

  //跟台、小吃提成点(百分比)
  @ExcelProperty(index = 6)
  private BigDecimal commonSnackPoint;

  //红酒、啤酒额外奖励(百分比) 提成点
  @ExcelProperty(index = 7)
  private BigDecimal wineAndBeerBonusPoint;

  //红酒、啤酒额外奖励(百分比) 是否当月发放
  @ExcelProperty(index = 8)
  private String thisMonthHandOutRedBeer;

  //合并业绩
  @ExcelProperty(index = 9)
  private String mergeOtherGroup;


  //计算方式
  @ExcelProperty(index = 10)
  private String otherRuleMethod;

  //提成点
  @ExcelProperty(index = 11)
  private BigDecimal otherRulePoint;

  //条件(万)
  @ExcelProperty(index = 12)
  private BigDecimal otherRuleCondition = BigDecimal.ZERO;

  //是否当月发放
  @ExcelProperty(index = 13)
  private String thisMonthHandOutOther;

  //计算方式
  @ExcelProperty(index = 14)
  private String importedRuleMethod;

  //提成点
  @ExcelProperty(index = 15)
  private BigDecimal importedRulePoint;

  //条件(万)
  @ExcelProperty(index = 16)
  private BigDecimal importedRuleCondition;

  //滤出(排除)
  @ExcelProperty(index = 17)
  private String importedRuleFilter;

  //是否当月发放
  @ExcelProperty(index = 18)
  private String thisMonthHandOutImport;

  //提成方式
  @ExcelProperty(index = 19)
  private String companyActivityRuleFloor3_4Pay_95Method;

  //提成点
  @ExcelProperty(index = 20)
  private BigDecimal companyActivityRuleFloor3_4Pay_95Point;

  //是否包含跟台、小吃
  @ExcelProperty(index = 21)
  private String companyActivityRuleFloor3_4Pay_95Snack;

  //排除
  @ExcelProperty(index = 22)
  private String companyActivityRuleFloor3_4Pay_95Filter;

  @ExcelProperty(index = 23)
  private String companyActivityRuleFloor3_4Pay_100Method;

  @ExcelProperty(index = 24)
  private BigDecimal companyActivityRuleFloor3_4Pay_100Point;

  //是否包含跟台、小吃
  @ExcelProperty(index = 25)
  private String companyActivityRuleFloor3_4Pay_100Snack;

  //排除
  @ExcelProperty(index = 26)
  private String companyActivityRuleFloor3_4Pay_100Filter;

  @ExcelProperty(index = 27)
  private String companyActivityRuleFloor48Pay_95Method;

  @ExcelProperty(index = 28)
  private BigDecimal companyActivityRuleFloor48Pay_95Point;

  @ExcelProperty(index = 29)
  private String companyActivityRuleFloor48Pay_100Method;

  @ExcelProperty(index = 30)
  private BigDecimal companyActivityRuleFloor48Pay_100Point;

  //是否包含跟台、小吃
  @ExcelProperty(index = 31)
  private String companyActivityRuleFloor48Pay_100Snack;

  //滤出(排除)
  @ExcelProperty(index = 32)
  private String companyActivityRuleFilter;


}
