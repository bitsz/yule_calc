package com.hrzj.yule.calc.util.usb;

import com.hrzj.yule.calc.util.EhCacheUtils;

import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/9 星期二 14:50
 */
@Slf4j
public class RestoreDatabase {

  public static ConfigurableApplicationContext context;
  private Connection connection;

  public RestoreDatabase(String connectionUrl) {
    if (null == connectionUrl || connectionUrl.contains("127.0.0.1")) {
      connectionUrl = "jdbc:jtds:sqlserver://127.0.0.1:8829/master;user=sa;password=123456";
    }
    try {
      this.connection = DriverManager.getConnection(connectionUrl);
    } catch (SQLException e) {
      log.info("获取数据库连接失败 {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static RestoreDatabase build(String connectionUrl) {
    return new RestoreDatabase(connectionUrl);
  }

  /**
   * @return java.lang.Boolean
   * @Author lifengquan
   * @Description 附加数据库
   * @Date 11:15 2024/4/25 星期四
   * @Param [dataMDF, dataLDF]
   **/
  public Boolean restore(String dataMDF, String dataLDF, String day) throws SQLException {

    try {

      String dbName = "SYGL_Data_".concat(day);
      EhCacheUtils.put("db", dbName);
      Statement statement = this.connection.createStatement();
      String restoreCommand = "CREATE DATABASE  " + dbName + " ON" +
        "  ( FILENAME = " + StrUtil.wrap(dataMDF, "'") + " )," +
        "( FILENAME = " + StrUtil.wrap(dataLDF, "'") + " )" +
        "FOR ATTACH;";
      System.out.println(restoreCommand);
      statement.execute(restoreCommand);
      log.info("Database restored successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
      log.info("Error restoring database: " + e.getMessage());
      return Boolean.FALSE;
    } finally {
      this.connection.close();
    }
    return Boolean.TRUE;
  }

  public String separation(String databaseName) {
    log.info("清理数据");
    try {
      Statement statement = this.connection.createStatement();
      String restoreCommand = "EXEC sp_detach_db @dbname = " + databaseName + ";";
      System.out.println(restoreCommand);
      statement.execute(restoreCommand);
      log.info("Database separation successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
      log.info("Error separation database: " + e.getMessage());
      return e.getMessage();
    } finally {
      try {
        this.connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

}

