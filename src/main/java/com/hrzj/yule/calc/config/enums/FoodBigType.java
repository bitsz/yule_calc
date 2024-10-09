package com.hrzj.yule.calc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-28 12:56
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FoodBigType {

  xiaochi(10000016L, "小吃"),
  ruanyin(10000015L, "软饮"),
  pijiu(10000014L, "啤酒"),
  hongjiu(10000013L, "红酒"),
  yangjiu(10000012L, "洋酒"),
  gentai(10000011L, "跟台"),
  xuejia(10000010L, "雪茄"),
  NO(10000009L, "不算业绩 不算提成(如:烟 赔偿 雪茄 三只松鼠 冰淇淋 服务费 )"),//不算业绩 不算提成
  jiushui(10000008L, "酒水"),

  ;

  private Long smId;
  private String smName;

  public static FoodBigType valueOf(Long smId) {
    if (null == smId) {
      return null;
    } else {
      FoodBigType[] values = FoodBigType.values();
      for (FoodBigType value : values) {
        if (smId.compareTo(value.getSmId()) == 0) {
          return value;
        }
      }
      return null;
    }
  }
}
