package com.hrzj.yule.calc.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.hrzj.yule.calc.util.ExtractNumbers;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-16 13:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
@HeadRowHeight(30)
@ContentRowHeight(25)
@ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER, borderBottom =
  BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN, borderTop =
  BorderStyleEnum.THIN)
public class CashReport implements Comparable<CashReport> {

  public final static String[] depts = {"DJ", "商务部", "散客", "会员卡", "免单,未点单,客离"};
  private String index;
  @ExcelProperty(value = "日期", index = 0)
  private String businessDay;
  @ExcelProperty(value = "流水号", index = 1)
  private Long cashId;
  @ExcelProperty(value = "房间", index = 2)
  private String room;
  @ExcelProperty(value = "订房人", index = 3)
  private String empName;
  @ExcelProperty(value = "部门", index = 4)
  private String dept;
  @ExcelProperty(value = "跟台", index = 5)
  private BigDecimal subjoinSale;
  @ExcelProperty(value = "6000+酒", index = 6)
  private BigDecimal importWine6000Sale;
  @ExcelProperty(value = "洋酒业绩", index = 7)
  private BigDecimal importWineSale;
  @ExcelProperty(value = "红酒业绩", index = 8)
  private BigDecimal redWineSale;
  @ExcelProperty(value = "啤酒业绩", index = 9)
  private BigDecimal beerSale;
  @ExcelProperty(value = "小吃", index = 10)
  private BigDecimal snacksSale;
  @ExcelProperty(value = "其他业绩", index = 11)
  private BigDecimal otherSale;
  @ExcelProperty(value = "业绩小计", index = 12)
  private BigDecimal sumSale;
  @ExcelProperty(value = "赔偿", index = 13)
  private BigDecimal compensation;
  @ExcelProperty(value = "火腿", index = 14)
  private BigDecimal ham;
  @ExcelProperty(value = "合计", index = 15)
  private BigDecimal total;
  @ExcelProperty(value = "折扣", index = 16)
  private BigDecimal discount;
  @ExcelProperty(value = "无奖励", index = 17)
  private String noReward;
  @ExcelProperty(value = "目标奖励", index = 18)
  private BigDecimal rewardOther;
  @ExcelProperty(value = "活动奖励", index = 19)
  private BigDecimal rewardActive;
  @ExcelProperty(value = "提成", index = 20)
  private BigDecimal commission;
  @ExcelProperty(value = "消费合计", index = 21)
  private BigDecimal cTrueMoney;
  @ExcelProperty(value = "实收(刷卡)", index = 22)
  private BigDecimal fixMoneyCard;
  @ExcelProperty(value = "实收(现金)", index = 23)
  private BigDecimal fixMoneyCash;
  @ExcelProperty(value = "小费", index = 24)
  private BigDecimal tipMoney;
  @ExcelProperty(value = "手续费", index = 25)
  private BigDecimal handlingFee;

  @ExcelProperty(value = "备注", index = 26)
  private String remark;
  private String vip;

  @Override
  public int compareTo(@NotNull CashReport o) {
    int index = this.cashId.compareTo(o.cashId);
    return index;
  }

  public BigDecimal getTotal() {
    if (null != getSumSale()) {
      total = getSumSale().add(null == getHam() ? BigDecimal.ZERO : getHam()).add(null == getCompensation() ?
        BigDecimal.ZERO : getCompensation());
    }
    return total;
  }

  public BigDecimal getSumSale() {
    BigDecimal result = BigDecimal.ZERO;
    if (null != getSubjoinSale()) {
      result = result.add(getSubjoinSale());
    }
    if (null != getImportWine6000Sale()) {
      result = result.add(getImportWine6000Sale());
    }
    if (null != getImportWineSale()) {
      result = result.add(getImportWineSale());
    }
    if (null != getRedWineSale()) {
      result = result.add(getRedWineSale());
    }
    if (null != getBeerSale()) {
      result = result.add(getBeerSale());
    }
    if (null != getSnacksSale()) {
      result = result.add(getSnacksSale());
    }
    if (null != getOtherSale()) {
      result = result.add(getOtherSale());
    }
    return result;
  }

  public String getDept() {

    if (StringUtils.isNotBlank(getVip()) && getVip().trim().equals("是")) {
      return depts[3];
    }

    List<String> names = new ArrayList<String>() {
      {
        add("贵宾");
        add("总办");
      }
    };
    if ((null != getFixMoneyCash() && getFixMoneyCash().compareTo(BigDecimal.ZERO) == 0) || (null != getRemark() && getRemark().contains(depts[3]))) {
      BigDecimal onlyOne = ExtractNumbers.findOnlyOne(getRemark());
      if (null != onlyOne) {
        return depts[3];
      } else {
        return depts[4];
      }
    }
    if (getEmpName().contains(depts[0])) {
      return depts[0];
    }
    for (String name : names) {
      if (getEmpName().contains(name)) {
        return depts[2];
      }
    }
    return depts[1];
  }
}
