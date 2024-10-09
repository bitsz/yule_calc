package com.hrzj.yule.calc.fx.charge;

import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.fx.MainStageView;
import com.hrzj.yule.calc.fx.TimeOutViewManager;
import com.hrzj.yule.calc.pojo.entity.mysql.PhysicalCard;
import com.hrzj.yule.calc.pojo.entity.mysql.ScanRecords;

import de.felixroske.jfxsupport.FXMLController;

import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 */
@FXMLController
@Slf4j
public class ChargeDirController implements Initializable {
  public Label day;
  public Label diskId;
  public FlowPane buttons;

  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  @Autowired
  private TimeOutViewManager timeOutViewManager;


  public void exit(ActionEvent actionEvent) {
    Application.showView(MainStageView.class);
  }


  public void money(MouseEvent actionEvent) {
    Node node = (Node) actionEvent.getSource();
    String money = (String) node.getUserData();
    log.debug("确认数据:{}", money);

    ScanRecords orderInfo = ScanRecords.builder()
      .dir(money)
      .day(day.getText().trim())
      .serialNumber(diskId.getText().trim())
      .build();

    timeOutViewManager.setCurrentLeft(-1);
    Application.showView(ShowDirStageView.class, Modality.WINDOW_MODAL, orderInfo);
    timeOutViewManager.setCurrentLeft(10);
  }

  @PostConstruct
  public void init() {
    timeOutViewManager.register(ChargeDirStageView.class, MainStageView.class, 60);
  }

  @Subscribe
  public void onMessageEvent(PhysicalCard event) throws InterruptedException {
    log.info("硬盘数据信息：{}", event);
    List<String> dirs = new ArrayList<>();
    dirs.add(event.getData());
    executorService.submit(() -> {
      Platform.runLater(() -> {
        day.setText(event.getDay());
        diskId.setText(event.getPhysicalId());
        buttons.getChildren().clear();
        for (String url : dirs) {
          Button button = new Button(url);
          button.setUserData(url);
          button.getStyleClass().add("btn");
          button.getStyleClass().add("btn-lg");
          button.getStyleClass().add("font-20");

          button.setEffect(new DropShadow());

          button.setPrefHeight(100);
          button.setPrefWidth(1000);
          button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
              money(event);
            }
          });
          buttons.getChildren().add(button);
        }
      });
    });

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
