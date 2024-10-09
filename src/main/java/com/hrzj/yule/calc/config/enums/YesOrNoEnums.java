package com.hrzj.yule.calc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: yes or no
 * @date 2024-05-09 11:08
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum YesOrNoEnums {

  yes("是"),

  no("否");

  private String value;

  public static YesOrNoEnums get(String value) {
    YesOrNoEnums[] values = YesOrNoEnums.values();
    for (YesOrNoEnums snackEnums : values) {
      if (snackEnums.getValue().equals(value)) {
        return snackEnums;
      }
    }
    return null;
  }
}
