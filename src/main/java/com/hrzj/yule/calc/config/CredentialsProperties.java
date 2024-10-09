package com.hrzj.yule.calc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.Data;


/**
 * @author lifengquan
 * @description: 登录信息
 * @date 2024-05-27 14:12
 */
@Data
@Component
@ConfigurationProperties(prefix = "login")
public class CredentialsProperties {

  private List<Credential> credentials;


  @Data
  public static class Credential {

    private String username;
    private String password;
  }

}
