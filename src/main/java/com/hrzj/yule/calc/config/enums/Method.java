package com.hrzj.yule.calc.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-30 13:55
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Method {

  //百分比
  PERCENTAGE("百分比"),

  //固定值
  FIXED("固定值");

  private String method;


  public static Method get(String method) {
    Method[] values = Method.values();
    for (Method value : values) {
      if (value.getMethod().equals(method)) {
        return value;
      }
    }
    return null;
  }

}
