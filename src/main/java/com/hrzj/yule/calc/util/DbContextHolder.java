package com.hrzj.yule.calc.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-09 9:43
 */
@Slf4j
public class DbContextHolder {

  // 定义一个传输性ThreadLocal
  private static final TransmittableThreadLocal<String> parentThreadLocal = new TransmittableThreadLocal<>();

  // 创建一个支持传递ThreadLocal值的线程池
  private static final ExecutorService executorService =
    TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(10));

  public static void setDb(String db) {
    // 在父线程中设置值
    parentThreadLocal.set(db);
    // 在子线程中执行任务
    executorService.execute(() -> {
      // 子线程可以获取到父线程设置的值
      String value = parentThreadLocal.get();
      log.info("子线程获取到的ThreadLocal值: " + value);
    });
  }

  public static String getDB() {
    String db = parentThreadLocal.get();
    return db;
  }

  public static void clear() {
    parentThreadLocal.remove();
  }
}
