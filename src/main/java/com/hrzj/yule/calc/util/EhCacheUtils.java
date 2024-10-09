package com.hrzj.yule.calc.util;

import org.springframework.cache.Cache;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: ehcache缓存操作工具类
 * @date 2024-04-30 11:50
 */

@Slf4j
public class EhCacheUtils {

  private static final org.springframework.cache.CacheManager CACHE_MANAGER =
    SpringUtils.getBean(org.springframework.cache.CacheManager.class);


  private EhCacheUtils() {
  }

  /**
   * 获取Cache
   */
  public static Cache getCache() {
    log.info(System.getProperty("java.io.tmpdir"));
    return (Cache) CACHE_MANAGER.getCache("myCache");
  }

  /**
   * 添加缓存数据
   *
   * @param key   键
   * @param value 值
   */
  public static void put(String key, Object value) {
    try {
      Cache cache = getCache();
      cache.put(key, value);
    } catch (Exception e) {
      log.error("添加缓存失败：{}", e.getMessage());
    }
  }

  /**
   * 获取缓存数据
   *
   * @param key 键
   *
   * @return 缓存数据
   */
  public static <T> T get(String key) {
    try {
      Cache cache = getCache();
      return (T) cache.get(key).get();
    } catch (Exception e) {
      log.error("获取缓存数据失败：", e);
      return null;
    }
  }

  /**
   * 删除缓存数据
   *
   * @param key 键
   */
  public static void delete(String key) {
    try {
      Cache cache = getCache();
      cache.evict(key);
    } catch (Exception e) {
      log.error("删除缓存数据失败：", e);
    }
  }

}
