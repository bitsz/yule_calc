package com.hrzj.yule.calc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-10 13:33
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SalesType {

  common("通提"),

  common6000("6000+酒水提成"),

  gentai("跟台、小吃"),

  redWine("红酒、啤酒额外奖励"),

  other("其他额外奖励"),

  importedWine("洋酒奖励"),

  company("公司酒水活动奖励"),

  ;

  private String value;

  public static SalesType get(String value) {
    SalesType[] values = SalesType.values();
    for (SalesType salesType : values) {
      if (salesType.getValue().equals(value)) {
        return salesType;
      }
    }
    return null;
  }
}
