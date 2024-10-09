package com.hrzj.yule.calc.fx.charge;

import com.hrzj.yule.calc.pojo.entity.mysql.ScanRecords;

import de.felixroske.jfxsupport.FXMLController;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-04-30 23:54
 */

@FXMLController
@Slf4j
@ConfigurationProperties(prefix = "hrzj")
public class LoadingViewController implements Initializable {


  @Subscribe(threadMode = ThreadMode.ASYNC)
  public void onMessageEvent(ScanRecords scanRecords) {
    log.info("Loading .....");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }


}
