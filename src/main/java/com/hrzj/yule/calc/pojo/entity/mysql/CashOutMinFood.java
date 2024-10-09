package com.hrzj.yule.calc.pojo.entity.mysql;

import com.baomidou.mybatisplus.annotation.TableField;

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
 * @date 2024-05-11 13:26
 */
@Entity
@Table(name = "cash_out_minFood")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashOutMinFood {

  @Id
  @TableField("id")
  private Long sldID;

  private Long cId;

  private BigDecimal cOutMinFoodFee;

  private Long slID;

  private Long sld_fID;

  private String fName;

  private String sldQuantity;

  private BigDecimal sldMoney;

  private BigDecimal sldInMinFee;
}
