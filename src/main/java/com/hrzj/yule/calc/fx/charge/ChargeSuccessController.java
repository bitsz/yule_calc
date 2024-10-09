package com.hrzj.yule.calc.fx.charge;

import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.fx.MainStageView;
import com.hrzj.yule.calc.fx.TimeOutViewManager;

import de.felixroske.jfxsupport.FXMLController;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import javafx.event.ActionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 */
@FXMLController
@Slf4j
public class ChargeSuccessController {

  @Autowired
  private TimeOutViewManager timeOutViewManager;

  public void exit(ActionEvent actionEvent) {
    Application.showView(MainStageView.class);
  }

  @PostConstruct
  public void init() {
    timeOutViewManager.register(ChargeSuccessStageView.class, MainStageView.class, 10);
  }
}
