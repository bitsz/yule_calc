package com.hrzj.yule.calc.pojo.entity.mssql.projection;

import com.hrzj.yule.calc.config.enums.FoodBigType;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 12:46
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopListDetailCashProjection {

  private Long id;
  private Long cashId;
  private int slType;
  private Long foodId;
  private String foodName;
  private Long ftId;
  private String ftName;
  private FoodBigType foodBigType;
  private Long smId;
  private String smName;
  private String quantity;
  private BigDecimal money;
  private Long empId;
  private String empName;
  private BigDecimal price;


  public FoodBigType getFoodBigType() {
    FoodBigType type = FoodBigType.valueOf(smId);
    return this.foodBigType = type;
  }

  public void setFoodBigType(FoodBigType foodBigType) {
    FoodBigType type = FoodBigType.valueOf(smId);
    this.foodBigType = type;
  }
}
