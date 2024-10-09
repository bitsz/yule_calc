package com.hrzj.yule.calc.pojo.entity.mysql;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-06-05 18:39
 */
@Entity
@Table(name = "fix_cash")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixCash {

  //流水号
  @Id
  @TableField("cashId")
  @TableId
  @Column(name = "cashId")
  private Long cashId;

  @TableField("businessDay")
  private String businessDay;

  //跟台
  @TableField("subjoinSale")
  private BigDecimal subjoinSale;

  //6000+酒
  @TableField("importWine6000Sale")
  private BigDecimal importWine6000Sale;

  //洋酒业绩
  @TableField("importWineSale")
  private BigDecimal importWineSale;

  //红酒业绩
  @TableField("redWineSale")
  private BigDecimal redWineSale;

  //啤酒业绩
  @TableField("beerSale")
  private BigDecimal beerSale;

  //小吃
  @TableField("snacksSale")
  private BigDecimal snacksSale;

  //其他业绩
  @TableField("otherSale")
  private BigDecimal otherSale;

  //业绩小计
  @TableField("sumSale")
  private BigDecimal sumSale;

  //赔偿
  @TableField("compensation")
  private BigDecimal compensation;

  //火腿
  @TableField("ham")
  private BigDecimal ham;

  //合计
  @TableField("total")
  private BigDecimal total;

  //折扣
  @TableField("discount")
  private BigDecimal discount;


}
