package com.hrzj.yule.calc.fx;

import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.config.AppState;
import com.hrzj.yule.calc.config.CredentialsProperties;
import com.hrzj.yule.calc.fx.charge.ChargeReadCardStageView;
import com.hrzj.yule.calc.fx.query.CashDataView;
import com.hrzj.yule.calc.fx.query.Loading;
import com.hrzj.yule.calc.fx.query.LoginDialog;
import com.hrzj.yule.calc.fx.query.OpenDirectory;
import com.hrzj.yule.calc.mapper.mssql.DbMapper;
import com.hrzj.yule.calc.service.template.TemplateAppend;
import com.hrzj.yule.calc.util.usb.HardWareUtils;
import com.hrzj.yule.calc.util.usb.RestoreDatabase;

import de.felixroske.jfxsupport.FXMLController;

import org.apache.logging.log4j.util.Strings;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 */

@FXMLController
@Slf4j
@ConfigurationProperties(prefix = "hrzj")
public class MainController implements Initializable {

  private static EventBus bus = EventBus.builder().build();
  @FXML
  public Label time;
  @FXML
  public Label date;
  @FXML
  public HBox btn_charge;
  @FXML
  public HBox btn_search;
  @FXML
  public Button exportRuleBut;
  @FXML
  public Button clearBtn;

  public VBox detailBox;
  @Autowired
  public ConfigurableApplicationContext context;
  @FXML
  private VBox root;
  @Setter
  private String serviceTel;
  @Setter
  private String deviceName;
  @Setter
  private String deviceVersion;
  @Autowired
  private TemplateAppend templateAppend;
  @Autowired
  private DbMapper dbMapper;

  @Value("${login.enable}")
  private Boolean enableLogin;

  @Value("${royalty.enable}")
  private Boolean royaltyEnable;

  @Value("${mssql.spring.datasource.connectionUrl}")
  private String connectionUrl;

  @Autowired
  private CredentialsProperties credentialsProperties;


  private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

  @PostConstruct
  public void init() {
    log.info("启动主页");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {


    btn_charge.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Application.switchView(MainStageView.class, ChargeReadCardStageView.class, null);
      }
    });

    btn_search.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Application.switchView(MainStageView.class, CashDataView.class, null);
      }
    });

    scheduledExecutor.scheduleWithFixedDelay(() -> {
      Platform.runLater(() -> {
        LocalDateTime now = LocalDateTime.now();
        time.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        date.setText(now.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEE")));
      });
    }, 1000, 1000, TimeUnit.MILLISECONDS);

//    if (Strings.isNotEmpty(serviceTel)) {
//      Label label = new Label("服务电话 : " + serviceTel);
//      label.setAlignment(Pos.CENTER_RIGHT);
//      label.getStyleClass().add("font-20");
//      detailBox.getChildren().add(label);
//    }

    if (Strings.isNotEmpty(deviceName)) {
      Label label = new Label("终端编号 : " + HardWareUtils.getMotherboardSN());
      label.setAlignment(Pos.CENTER_RIGHT);
      label.getStyleClass().add("font-20");
      detailBox.getChildren().add(label);
    }

    if (Strings.isNotEmpty(deviceVersion)) {
      Label label = new Label("终端版本 : " + deviceVersion);
      label.setAlignment(Pos.CENTER_RIGHT);
      label.getStyleClass().add("font-20");
      detailBox.getChildren().add(label);
    }

    if (!royaltyEnable) {
      detailBox.getChildren().remove(exportRuleBut);
    } else {
      detailBox.getChildren().add(exportRuleBut);
    }

    detailBox.getChildren().remove(clearBtn);
    detailBox.getChildren().add(clearBtn);

    exportRuleBut.setOnAction(event -> exportRule());
    clearBtn.setOnAction(event -> clearDB());

    if (enableLogin) {
      if (!AppState.isAuthenticated()) {
        LoginDialog loginDialog = new LoginDialog(credentialsProperties);
        loginDialog.showAndWait();

        if (!loginDialog.isAuthenticated()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("未登录");
          alert.setHeaderText(null);
          alert.setContentText("登录失败,退出程序");
          alert.showAndWait();
          System.exit(1);
        } else {
          AppState.setAuthenticated(true);
        }
      }
    }


  }

  private void exportRule() {
    log.info("导出规则");
    Stage stage = (Stage) root.getScene().getWindow();

    Loading loading = new Loading(stage);
    loading.showStage();
    new Thread(() -> {
      AtomicReference<String> headerText = new AtomicReference<>(templateAppend.outputFilePath);
      Boolean gen = Boolean.FALSE;
      try {
        gen = templateAppend.gen();
      } catch (Exception e) {
        e.printStackTrace();
        gen = null;
        headerText.set(e.getMessage());
        new OpenDirectory().showErrorAlert("导出失败", e.getMessage());
        loading.closeStage();
        return;
      }
      Boolean finalGen = gen;
      Platform.runLater(() -> {
        // 创建弹框
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        if (null == finalGen) {
          alertType = Alert.AlertType.ERROR;
          alert.setHeaderText(headerText.get());

        } else {
          if (!finalGen) {
            alertType = Alert.AlertType.WARNING;
            headerText.set("数据已被卸载,无法导出");
            log.info("数据已被卸载,无法导出");
          }
        }
        alert.setTitle("提示");
        alert.setHeaderText(headerText.get());
        alert.setAlertType(alertType);
        alert.setContentText("导出完成");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));

        ButtonType btnType = new ButtonType("打开");
        alert.getButtonTypes().setAll(btnType, ButtonType.CLOSE);
        // 显示对话框，并接收选择的按钮
        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(buttonType -> {
          if (buttonType == btnType) {
            new OpenDirectory().openWorkingDirectory("template");
          }
        });
        loading.closeStage();
      });
    }).start();

  }

  public void clearDB() {
    log.info("卸载数据");

    Stage stage = (Stage) root.getScene().getWindow();
    Loading loading = new Loading(stage);
    loading.showStage();
    new Thread(() -> {
      List<String> dbs = dbMapper.findDb();
      for (String db : dbs) {
        RestoreDatabase.build(connectionUrl).separation(db);
        log.info(db);
      }

      Platform.runLater(() -> {
        // 创建弹框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setContentText("数据卸载成功");
        Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
        s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
        // 显示弹框
        alert.showAndWait();
        loading.closeStage();
      });
    }).start();
  }
}
