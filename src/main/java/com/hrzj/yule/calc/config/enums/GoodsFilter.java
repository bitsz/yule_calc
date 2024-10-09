package com.hrzj.yule.calc.config.enums;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-20 9:08
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum GoodsFilter {
  //小吃要过滤出柠檬片（收银系统将柠檬片设置成了小吃只有这样过滤了）
  xiaochi_lmp(10000850L, FoodBigType.xiaochi),
  xiaochi_fm(10000763L, FoodBigType.xiaochi)
  ;

  private Long foodId;

  private FoodBigType foodBigType;


  public static List<Long> get(FoodBigType bigType) {
    List<Long> goodsId = new ArrayList<>();
    GoodsFilter[] values = GoodsFilter.values();
    for (GoodsFilter value : values) {
      if (value.getFoodBigType().equals(bigType)) {
        goodsId.add(value.getFoodId());
      }
    }
    return goodsId;
  }


}
