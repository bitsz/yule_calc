package com.hrzj.yule.calc.pojo.entity.mysql;

import java.math.BigDecimal;

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
 * @date 2024-05-15 17:36
 */
@Entity
@Table(name = "cash_food")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashFood {

  @Id
  private Long fID;

  private String fName;

  private String fSearchCode;

  private Long f_ftID;

  private Long f_fuId;

  private int fEnable;
  private int fOutStore;
  private int fPresentLevel;
  private String fBarCode;
  private int fNoPrice;
  private BigDecimal fPrice1;
  private BigDecimal fPrice2;
  private BigDecimal fPrice3;
  private BigDecimal fPrice4;
  private BigDecimal fPrice5;
  private int fRuleMode;
  private int fRuleNumber;
  private int fFreePresent;
  private int fNeedAudit;
  private int f_countFTID;
  private BigDecimal fVipPrice1;
  private BigDecimal fVipPrice2;
  private int fReplaceID;
  private String fEnglishName;
  private int fCanAgio;
  private int fOutStoreNext;
  private int fOutStoreQuantity;
  private int fSortFlag;
  private BigDecimal fBonus;


}
