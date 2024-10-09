package com.hrzj.yule.calc.fx.query;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-23 9:36
 */
public class OpenDirectory {

  public void openWorkingDirectory(String directory) {
    // 获取当前程序的工作目录
    String workingDir = System.getProperty("user.dir").concat("/" + directory);
    File dir = new File(workingDir);

    // 使用 Desktop 类打开工作目录
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      try {
        desktop.open(dir);
      } catch (IOException e) {
        e.printStackTrace();
        showErrorAlert("错误", "无法打开目录");
      }
    } else {
      showErrorAlert("不支持的操作", "您的系统不支持桌面操作.");
    }
  }

  public void showErrorAlert(String title, String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
      s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    });

  }
}
