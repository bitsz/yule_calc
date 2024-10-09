package com.hrzj.yule.calc.pojo.entity.mysql;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-29 13:00
 */
@Entity
@Table(name = "cash_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class CashItem {

  @Id
  private Long id;
  private Long cId;
  //0是买单 1赠送 2退
  private int slType;
  private Long foodId;
  private String foodName;
  private Long foodTypeId;
  private String foodTypeName;
  private Long bigType;
  private String bigTypeName;
  private Integer quantity;
  private BigDecimal money;
  private Long empId;
  private String empName;
  private Date time;
  private BigDecimal fPrice;
  private BigDecimal fixMoney;
  private Long unitId;


}
