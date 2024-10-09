package com.hrzj.yule.calc.fx.charge;

import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.fx.MainStageView;
import com.hrzj.yule.calc.fx.TimeOutViewManager;
import com.hrzj.yule.calc.fx.query.Loading;
import com.hrzj.yule.calc.pojo.entity.mysql.PhysicalCard;
import com.hrzj.yule.calc.repository.mysql.PhysicalCardRepository;
import com.hrzj.yule.calc.util.ViewEvent;
import com.hrzj.yule.calc.util.usb.FileInfo;
import com.hrzj.yule.calc.util.usb.FindMaxKeyInMap;
import com.hrzj.yule.calc.util.usb.HardWareUtils;
import com.hrzj.yule.calc.util.usb.USBEventListener;

import de.felixroske.jfxsupport.FXMLController;

import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.StrPool.LF;

/**
 * @author lifengquan
 */
@FXMLController
@Slf4j
public class ChargeReadCardController implements Initializable {

  @Autowired
  public PhysicalCardRepository physicalCardRepository;
  public StackPane pleaseCard;
  public VBox root;
  public Label tip;
  @Autowired
  private USBEventListener usbEventListener;
  @Autowired
  private TimeOutViewManager timeOutViewManager;

  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  private StringBuilder cardBuilder = new StringBuilder(32);


  @Override
  public void initialize(URL location, ResourceBundle resources) {

    executorService.scheduleWithFixedDelay(() -> {
      Platform.runLater(() -> {
        pleaseCard.setVisible(!pleaseCard.isVisible());
      });
    }, 1000L, 1000L, TimeUnit.MILLISECONDS);
    root.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
          tip.setText("");
          Stage stage = (Stage) root.getScene().getWindow();
          Loading loading = new Loading(stage);
          loading.showStage();

          new Thread(() -> {
            try {
              String diskId = null;
              List<String> url = new ArrayList<>();
              Map<String, List<FileInfo>> usbMap = usbEventListener.find();
              if (null == usbMap || usbMap.isEmpty()) {
                Platform.runLater(() -> tip.setText("无可用文件"));
                return;
              }
              String maxKey = FindMaxKeyInMap.findMaxKey(usbMap);
              List<FileInfo> fileInfos = usbMap.get(maxKey);
              for (FileInfo fileInfo : fileInfos) {
                url.add(fileInfo.getUrl());
                if (null == diskId) {
                  diskId = HardWareUtils.diskId(fileInfo.getUrl());
                }
              }
              validCard(diskId, maxKey, url);
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              loading.closeStage();
            }
          }).start();
        } else {
          log.info("输入错误按键:{} {}", event.getCode(), event.getText());
          if (event.getCode().isDigitKey() || event.getCode().isLetterKey()) {
            cardBuilder.append(event.getText());
          }
        }
      }
    });

  }

  //自动跳转到主页
  @PostConstruct
  public void init() {
    timeOutViewManager.register(ChargeReadCardStageView.class, MainStageView.class, 60);
  }

  @PreDestroy
  public void destroy() {
    executorService.shutdown();
  }

  @Subscribe
  public void showEvent(ViewEvent viewEvent) {
    if (viewEvent.isPresent(ViewEvent.ViewEvenType.show, this)) {
      Platform.runLater(() -> tip.setText("请连接磁盘,输入enter键"));
    }
  }

  private void validCard(String card, String maxDay, List<String> data) {
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        if (null == card || null == data) {
          Platform.runLater(() -> tip.setText("无效磁盘"));
          return;
        }
        PhysicalCard physicalCard = null;
        try {
          physicalCard = physicalCardRepository.findPhysicalCardByPhysicalIdAndData(card, data.get(0));
        } catch (Exception e) {
          Platform.runLater(() -> tip.setText("服务器内部异常"));
          return;
        }
        if (physicalCard == null) {
          String dir = data.stream().collect(Collectors.joining(LF));
          physicalCard = new PhysicalCard(null, card, card, maxDay, dir);
          physicalCardRepository.save(physicalCard);

        }

        PhysicalCard finalPhysicalCard = physicalCard;
        Platform.runLater(() -> {
          tip.setText(maxDay);
          Application.switchView(ChargeReadCardStageView.class, ChargeDirStageView.class, finalPhysicalCard);
        });
      }
    });
  }


  public void exit(ActionEvent actionEvent) {
    Application.switchView(ChargeReadCardStageView.class, MainStageView.class, null);
  }
}
