package com.hrzj.yule.calc;

import com.hrzj.yule.calc.fx.MainStageView;
import com.hrzj.yule.calc.fx.TimeOutViewManager;
import com.hrzj.yule.calc.util.CustomSplash;
import com.hrzj.yule.calc.util.ViewEvent;
import com.hrzj.yule.calc.util.usb.HardWareUtils;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.util.CharsetUtil.CHARSET_UTF_8;

@Slf4j
@EnableCaching
@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {


  private static final StopWatch started = StopWatch.createStarted();
  public static ConfigurableApplicationContext context;
  private static EventBus bus = EventBus.builder().build();

  public static void main(String[] args) {
    launch(Application.class, MainStageView.class, new CustomSplash(), args);

  }

  public static boolean isRunningAsJar() {
    String name = Application.class.getClassLoader().getClass().getName();
    String protocol = Application.class.getResource("").getProtocol();
    log.info("classLoader name:{},protocol:{}", name, protocol);
    switch (protocol) {
      case "jar":
        return true;
      default:
        return false;
    }
  }

  public static void showView(final Class<? extends AbstractFxmlView> window, final Modality mode, Object object) {
    final AbstractFxmlView view = context.getBean(window);
    Stage newStage = new Stage();

    Scene newScene;
    if (view.getView().getScene() != null) {
      newScene = view.getView().getScene();
    } else {
      newScene = new Scene(view.getView());
      newScene.setFill(null);
    }

    if (GUIState.getScene().getRoot().getEffect() == null) {
      Lighting value = new Lighting();
      value.setDiffuseConstant(1.0);
      value.setSpecularConstant(2.0);
      value.setSpecularExponent(40.0);
      value.setSurfaceScale(0);
      GUIState.getScene().getRoot().setEffect(value);
    } else {
      Lighting effect = (Lighting) GUIState.getScene().getRoot().getEffect();
      effect.setDiffuseConstant(1.0);
    }

    if (!bus.isRegistered(view.getPresenter()) && hasSubscribe(view.getPresenter())) {
      bus.register(view.getPresenter());
      log.info("registered:{}", view.getPresenter().getClass());
    }


    FXMLView annotation = window.getAnnotation(FXMLView.class);

    newStage.setScene(newScene);
    newStage.initModality(mode);
    newStage.initOwner(getStage());
    newStage.setTitle(annotation.title());
    newStage.initStyle(StageStyle.valueOf(annotation.stageStyle()));


    newStage.setOnShown(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        if (object != null) {
          log.debug("跳转参数:{}", object);
          bus.post(object);
        }
      }
    });

    newStage.showAndWait();
  }

  public static void switchView(Class<? extends AbstractFxmlView> from, Class<? extends AbstractFxmlView> to,
                                Object object) {
    try {
      log.info("从 {} 跳转到 {}", from, to);
      StopWatch started = StopWatch.createStarted();
      AbstractFxmlView fromViewer = context.getBean(from);
      AbstractFxmlView toViewer = context.getBean(to);

      context.getBean(TimeOutViewManager.class).setCurrentView(to);

      if (!bus.isRegistered(fromViewer.getPresenter()) && hasSubscribe(fromViewer.getPresenter())) {
        bus.register(fromViewer.getPresenter());
        log.info("registered:{}", fromViewer.getPresenter().getClass());
      }

      if (!bus.isRegistered(toViewer.getPresenter()) && hasSubscribe(toViewer.getPresenter())) {
        bus.register(toViewer.getPresenter());
        log.info("registered:{}", toViewer.getPresenter().getClass());
      }

      if (bus.isRegistered(fromViewer.getPresenter())) {
        log.info("发布隐藏事件");
        bus.post(new ViewEvent(ViewEvent.ViewEvenType.hide, fromViewer, fromViewer.getPresenter()));
      }

      Platform.runLater(() -> {
        AbstractJavaFxApplicationSupport.showView(to);
        if (bus.isRegistered(toViewer.getPresenter())) {
          log.info("发布显示事件");
          bus.post(new ViewEvent(ViewEvent.ViewEvenType.show, toViewer, toViewer.getPresenter()));
        }

        if (object != null) {
          log.info("跳转参数:{}", object);
          bus.post(object);
        }

        log.info("跳转页面耗时:{}", started.getTime());
      });


    } catch (Exception e) {
      log.error("跳转页面异常", e);
    }
  }

  public static boolean hasSubscribe(Object object) {
    Method[] subscribes = MethodUtils.getMethodsWithAnnotation(object.getClass(), Subscribe.class);
    return subscribes != null && subscribes.length > 0;
  }

  @Bean
  public CommandLineRunner runner() {
    return args -> {
      System.setProperty("file.encoding", CHARSET_UTF_8.displayName());
      Map<String, String> env = System.getenv();
      for (String envName : env.keySet()) {
        log.info("参数:{}={}", envName, env.get(envName));
      }
      log.info("{}系统启动完成.耗时{}", HardWareUtils.getMotherboardSN(), started.getTime());
    };
  }

  @Override
  public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
    Application.context = ctx;

    stage.setWidth(900);
    stage.setHeight(600);
    stage.setTitle("计算助手客户端");
    //简单模式
    //stage.initStyle(StageStyle.UNDECORATED);
    stage.setAlwaysOnTop(false);
    stage.setFullScreen(false);


    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        log.info("清理数据库");
        System.exit(2);
      }
    });

    ConfigurableEnvironment env = context.getEnvironment();
    String active = env.getProperty("spring.profiles.active");
    log.info("\n--------------------------------------------------------------------------------------------\n\t" +
      "启动完成:▼▼▼▼▼\n\t" +
      "启动参数: \t" + active + "\n\t" +
      "--------------------------------------------------------------------------------------------");
  }


  @Override
  public Collection<Image> loadDefaultIcons() {
    return Arrays.asList(new Image(this.getClass().getResourceAsStream("/image/logo.png")));
  }


}
