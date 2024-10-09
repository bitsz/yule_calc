package com.hrzj.yule.calc.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lifengquan
 * @description: 正则表达式来提取字符串中的数字
 * @date 2024-05-17 22:51
 */
public class ExtractNumbers {

  public static BigDecimal findOnlyOne(String input) {
    if (StringUtils.isBlank(input)) {
      return null;
    }
    // 定义正则表达式模式 匹配一个或多个连续的数字
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(input);

    // 查找并打印所有匹配的数字
    while (matcher.find()) {
      return new BigDecimal(matcher.group());
    }
    return null;
  }

  public static int findOnlyOneInt(String input) {
    if (StringUtils.isBlank(input)) {
      return 0;
    }
    // 定义正则表达式模式 匹配一个或多个连续的数字
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(input);

    // 查找并打印所有匹配的数字
    while (matcher.find()) {
      return Integer.valueOf(matcher.group());
    }
    return 0;
  }


  public static List<Long> findMany(String input) {
    if (StringUtils.isBlank(input)) {
      return null;
    }
    // 定义正则表达式模式
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(input);

    // 创建一个列表来存储找到的数字
    ArrayList<Long> numbers = new ArrayList<>();

    // 查找所有匹配的数字并添加到列表中
    while (matcher.find()) {
      Long value = Long.valueOf(matcher.group());
      if (value > 1000) {
        numbers.add(value);
      }
    }
    return numbers;
  }
}
