package com.hrzj.yule.calc.util.usb;

import java.util.Map;

import cn.hutool.core.map.MapUtil;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/10 星期三 16:25
 */
public class FindMaxKeyInMap {


  public static <K extends Comparable<K>, V> K findMaxKey(Map<K, V> map) {
    if (MapUtil.isEmpty(map)) {
      return null;
    }

    K maxKey = null;
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (maxKey == null || entry.getKey().compareTo(maxKey) > 0) {
        maxKey = entry.getKey();
      }
    }

    return maxKey;
  }
}
