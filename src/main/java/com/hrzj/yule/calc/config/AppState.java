package com.hrzj.yule.calc.config;

import org.springframework.stereotype.Component;

/**
 * @author lifengquan
 * @description: 全局状态来跟踪用户是否已经登录
 * @date 2024-05-15 13:42
 */
@Component
public class AppState {

  private static boolean isAuthenticated = false;

  public static boolean isAuthenticated() {
    return isAuthenticated;
  }

  public static void setAuthenticated(boolean authenticated) {
    isAuthenticated = authenticated;
  }
}
