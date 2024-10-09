package com.hrzj.yule.calc.fx.query;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-30 23:17
 */
public class Loading {

  protected Stage stage;
  private StackPane overlay;
  private Label messageLb;

  public Loading(Pane owner) {
    ImageView loadingView = new ImageView(new Image("/image/loading.gif")); // 可替换

    messageLb = new Label("处理中...");
    messageLb.setFont(Font.font(30));

    StackPane content = new StackPane();
    content.getChildren().addAll(loadingView, messageLb);

    overlay = new StackPane();
    overlay.setMouseTransparent(false);
    overlay.setPrefSize(owner.getWidth(), owner.getHeight());
    overlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null)));
    overlay.getChildren().add(content);

    // 监听确保overlay与owner大小一致
    owner.widthProperty().addListener((obs, oldVal, newVal) -> overlay.setPrefWidth(newVal.doubleValue()));
    owner.heightProperty().addListener((obs, oldVal, newVal) -> overlay.setPrefHeight(newVal.doubleValue()));

    Platform.runLater(() -> {
      owner.getChildren().add(overlay);
    });
  }

  public Loading(Stage owner) {
    ImageView loadingView = new ImageView(
      new Image("/image/loading.gif"));// 可替换

    messageLb = new Label("处理中...");
    messageLb.setFont(Font.font(30));

    overlay = new StackPane();
    overlay.setMouseTransparent(true);
    overlay.setPrefSize(owner.getWidth(), owner.getHeight());
    overlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.4), null, null)));
    overlay.getChildren().addAll(loadingView, messageLb);

    Scene scene = new Scene(overlay);
    scene.setFill(Color.TRANSPARENT);

    stage = new Stage();
    stage.setScene(scene);
    stage.setResizable(false);
    stage.initOwner(owner);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.getIcons().addAll(owner.getIcons());
    stage.setX(owner.getX());
    stage.setY(owner.getY());
    stage.setHeight(owner.getHeight());
    stage.setWidth(owner.getWidth());
  }


  // 更改信息
  public void showMessage(String message) {
    Platform.runLater(() -> messageLb.setText(message));
  }

  // 显示
  public void show() {
    Platform.runLater(() -> overlay.setVisible(true));
  }

  // 关闭
  public void close() {
    Platform.runLater(() -> overlay.setVisible(false));
  }


  // 显示
  public void showStage() {
    Platform.runLater(() -> stage.show());
  }

  // 关闭
  public void closeStage() {
    Platform.runLater(() -> stage.close());
  }
}
