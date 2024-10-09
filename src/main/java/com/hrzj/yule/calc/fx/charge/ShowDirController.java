package com.hrzj.yule.calc.fx.charge;

import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.mapper.mssql.CashProjectionMapper;
import com.hrzj.yule.calc.pojo.entity.mssql.projection.CashProjection;
import com.hrzj.yule.calc.pojo.entity.mysql.CashOutMinFood;
import com.hrzj.yule.calc.pojo.entity.mysql.CashPresentFood;
import com.hrzj.yule.calc.pojo.entity.mysql.ScanRecords;
import com.hrzj.yule.calc.repository.mysql.CashOutMinFoodRepository;
import com.hrzj.yule.calc.repository.mysql.CashPresentFoodRepository;
import com.hrzj.yule.calc.repository.mysql.ScanRecordsRepository;
import com.hrzj.yule.calc.service.CashSaveAsService;
import com.hrzj.yule.calc.service.FoodService;
import com.hrzj.yule.calc.service.template.TemplateAppend;
import com.hrzj.yule.calc.util.DbContextHolder;
import com.hrzj.yule.calc.util.usb.RestoreDatabase;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.lang.UUID;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.CharPool.UNDERLINE;
import static cn.hutool.core.text.StrPool.LF;

/**
 * @author lifengquan
 */
@FXMLController
@Slf4j
@ConfigurationProperties(prefix = "hrzj")
public class ShowDirController implements Initializable {
  public VBox root;
  public Label tradeNO;
  public ImageView qrcode;
  public Button close;
  public Image loadGifImage = new Image(this.getClass().getResourceAsStream("/image/load.gif"));

  public Image errorImage = new Image(this.getClass().getResourceAsStream("/image/error.png"));


  @Autowired
  private ScanRecordsRepository orderInfoRepository;

  @Autowired
  private CashOutMinFoodRepository cashOutMinFoodRepository;

  @Autowired
  private CashPresentFoodRepository cashPresentFoodRepository;

  private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> scheduledFuture;
  @Getter
  private ScanRecords currentOrder;

  @Autowired
  private CashSaveAsService cashSaveAsService;

  @Autowired
  private FoodService foodService;

  @Autowired
  private CashProjectionMapper cashProjectionMapper;

  @Autowired
  private TemplateAppend templateAppend;

  @Value("${mssql.spring.datasource.connectionUrl}")
  private String connectionUrl;

  @Value("${royalty.enable}")
  private Boolean royaltyEnable;


  public void close(ActionEvent actionEvent) {
    root.getScene().getWindow().hide();

    Lighting effect = (Lighting) GUIState.getScene().getRoot().getEffect();
    effect.setDiffuseConstant(2.0);

    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
      scheduledFuture = null;
    }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  public void onMessageEvent(ScanRecords scanRecord) {
    log.info("还原：{}", scanRecord);
    String db = ("SYGL_Data" + UNDERLINE + scanRecord.getDay());
    DbContextHolder.setDb(db);
    this.currentOrder = scanRecord;

    Platform.runLater(() -> {
      close.setVisible(false);
    });
    updateState(loadGifImage, "正在还原数据，请稍候 . . .");
    if (scheduledExecutor.isShutdown()) {
      scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }
    scheduledExecutor.submit(() -> {
      try {
        scanRecord.setOrderId(UUID.fastUUID().toString());
        ScanRecords byMoneyAndUserNO = orderInfoRepository.findByDirAndDay(scanRecord.getDir(),
          scanRecord.getDay());
        if (null == byMoneyAndUserNO) {
          scanRecord.setCreateTime(new Date());
          orderInfoRepository.save(scanRecord);
        }
        String[] data = scanRecord.getDir().split(LF);
        Boolean restore = RestoreDatabase.build(connectionUrl).restore(data[0], data[1], scanRecord.getDay());
        if (!restore) {
          updateState(errorImage, "数据已存在无需还原");
          return;
        }
        scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {

          Boolean flag = saveAs(scanRecord.getDay());
          if (!flag) {
            log.info("还原失败");
            String database = DbContextHolder.getDB();
            RestoreDatabase.build(connectionUrl).separation(database);
            DbContextHolder.clear();
          }
        }, 2000, 1000, TimeUnit.MILLISECONDS);

      } catch (Exception e) {
        log.error("还原数据失败", e);
        updateState(errorImage, "还原数据失败");
      } finally {
        Platform.runLater(() -> {
          close.setVisible(true);
        });
      }
    });

  }

  public void updateState(Image flagImage, String tip) {
    Platform.runLater(() -> {
      log.info(tip);
      tradeNO.setText(tip);
      qrcode.setImage(flagImage);
    });
  }

  private Boolean saveAs(String day) {
    Boolean flag = Boolean.FALSE;
    String db = DbContextHolder.getDB();
    try {
      updateState(loadGifImage, MessageFormat.format("还原{0}数据", day));
      List<CashProjection> cash = cashProjectionMapper.findCash(db, null);
      updateState(loadGifImage, MessageFormat.format("检测到{0}条流水", cash.size()));
      cashSaveAsService.saves(cash);
      updateState(loadGifImage, "流水号还原完成 . . .");
      List<CashOutMinFood> cashOutMinFood = cashProjectionMapper.cOutMinFoodFeeList(db);
      cashOutMinFoodRepository.saveAll(cashOutMinFood);
      log.info("记录赠送的商品");
      List<CashPresentFood> cashPresentFoods = cashProjectionMapper.cPresentFoodFeeList(db);
      cashPresentFoodRepository.saveAll(cashPresentFoods);
      log.info("还原{}条流水", cash.size());
      updateState(loadGifImage, "开始还原商品");
      foodService.saves();
      updateState(loadGifImage, "商品还原完成 ");
      if (royaltyEnable) {
        templateAppend.gen();
        updateState(loadGifImage, "规则模板已同步生成");
      }
      Platform.runLater(() -> {
        close(null);
        Application.switchView(ShowDirStageView.class, ChargeSuccessStageView.class, null);
      });
      flag = Boolean.TRUE;
    } catch (Exception e) {
      log.error("数据另存为失败", e);
      updateState(errorImage, "数据还原失败");
      flag = Boolean.FALSE;
    } finally {
      scheduledExecutor.shutdown();

    }
    return flag;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

}
