package com.hrzj.yule.calc.fx;

import com.hrzj.yule.calc.Application;

import de.felixroske.jfxsupport.AbstractFxmlView;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lifengquan
 */
@Component
@Slf4j
public class TimeOutViewManager implements Runnable {


  private Class<? extends AbstractFxmlView> currentView;
  private Integer currentLeft = -1;
  private List<TimeOutView> timeOutViewList = new ArrayList<>();
  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public void register(Class<? extends AbstractFxmlView> from, Class<? extends AbstractFxmlView> to,
                       Integer timeOutSec) {
    timeOutViewList.add(new TimeOutView(from, to, timeOutSec));
  }

  @PostConstruct
  public void init() {
    executorService.scheduleWithFixedDelay(this, 1000, 1000, TimeUnit.MILLISECONDS);
  }

  public void setCurrentView(Class<? extends AbstractFxmlView> currentView) {
    this.currentView = currentView;
    this.currentLeft =
      this.timeOutViewList.stream().filter(f -> f.getFrom() == currentView).map(m -> m.getTimeOutSec()).findFirst().orElse(-1);
  }

  public void setCurrentLeft(Integer currentLeft) {
    this.currentLeft = currentLeft;
  }

  @Override
  public void run() {
    log.debug("超时检查 当前{} 超时时间{}", currentView, currentLeft);
    for (TimeOutView timeOutView : timeOutViewList) {
      if (timeOutView.getFrom() == currentView) {
        if (currentLeft == 0) {
          log.info("在 {} 己停留 {} 秒,将自动跳转到 {}", currentView, timeOutView.timeOutSec, timeOutView.getTo());
          Application.switchView(timeOutView.getFrom(), timeOutView.getTo(), null);
          return;
        }
        if (currentLeft > 0) {
          currentLeft--;
          log.debug("在 {} 己停留 1 秒,还剩 {} 秒", currentView, currentLeft);
        }
      }
    }
  }

  @AllArgsConstructor
  @Getter
  public class TimeOutView {
    private Class<? extends AbstractFxmlView> from;
    private Class<? extends AbstractFxmlView> to;
    private Integer timeOutSec;
  }
}
