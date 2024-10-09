package com.hrzj.yule.calc.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-15 18:07
 */
public class BeanCopyTool {

  public static <T> T convertCopyBean(Object source, Class<T> clazz) {
    if (source == null) {
      return null;
    }
    try {
      T target = clazz.newInstance();
      BeanUtils.copyProperties(source, target);
      return target;
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex.getMessage());
    }
  }

  public static <T, E> List<T> convertCopyList(Collection<E> oldList, Class<T> clazz) {
    List<T> newList = new ArrayList<>();
    if (oldList != null && !oldList.isEmpty()) {
      try {
        for (Object item : oldList) {
          T newObj = clazz.newInstance();
          BeanUtils.copyProperties(item, newObj);
          newList.add(newObj);
        }
      } catch (Exception ex) {
        throw new IllegalArgumentException(ex.getMessage());
      }
    }
    return newList;
  }
}



