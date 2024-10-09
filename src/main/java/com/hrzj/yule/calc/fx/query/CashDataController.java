package com.hrzj.yule.calc.fx.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hrzj.yule.calc.Application;
import com.hrzj.yule.calc.fx.MainStageView;
import com.hrzj.yule.calc.pojo.entity.mssql.projection.CashProjection;
import com.hrzj.yule.calc.pojo.entity.mysql.Cash;
import com.hrzj.yule.calc.service.CashService;
import com.hrzj.yule.calc.service.calc.Performance;
import com.hrzj.yule.calc.service.export.ExportCashReport;
import com.hrzj.yule.calc.util.ViewEvent;

import de.felixroske.jfxsupport.FXMLController;

import org.apache.commons.lang3.StringUtils;
import org.casic.javafx.control.PaginationPicker;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;


/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024/4/12 星期五 18:11
 */
@FXMLController
@Slf4j
public class CashDataController implements Initializable {

  @FXML
  private StackPane rootPane;

  @FXML
  private HBox headHbox;

  @FXML
  private TextField snTextField;
  @FXML
  private DatePicker startDatePicker;
  @FXML
  private DatePicker endDatePicker;
  @FXML
  private Button searchBut;
  @FXML
  private Button resetBut;
  @FXML
  private Button exportDetailBut;
  @FXML
  private Button exportSummaryBut;

  @FXML
  private TableColumn<CashProjection, String> rowCol;
  @FXML
  private TableColumn<CashProjection, String> cashSnCol;
  @FXML
  private TableColumn<CashProjection, String> roomCol;
  @FXML
  private TableColumn<CashProjection, String> roomTypeCol;
  @FXML
  private TableColumn<CashProjection, Date> businessDayCol;
  @FXML
  private TableColumn<CashProjection, String> esNameCol;
  @FXML
  private TableColumn<?, ?> foodFreeCol;
  @FXML
  private TableColumn<CashProjection, BigDecimal> outMinFoodFreeCol;
  @FXML
  private TableColumn<CashProjection, String> serviceFree;
  @FXML
  private TableColumn<?, ?> serviceFreeMoney;
  @FXML
  private TableColumn<?, ?> trueFreeMoney;

  @FXML
  private TableColumn<?, ?> fixMoney;

  @FXML
  private TableColumn<CashProjection, String> cashEmployee;
  @FXML
  private TableColumn<CashProjection, Date> startTimeCol;
  @FXML
  private TableColumn<CashProjection, Date> endTimeCol;
  /*  @FXML
    private TableColumn<CashProjection, String> cashCommentCol;*/
  @FXML
  private TableColumn<CashProjection, String> roomCommentCol;

  @FXML
  private TableView<CashProjection> tableView;

  @FXML
  private PaginationPicker page;

  @Autowired
  private CashService cashService;

  @Autowired
  private Performance performance;

  @Autowired
  private ExportCashReport exportCashReport;


  @Value("${page.size}")
  private Integer pageSize;

  @Value("${royalty.enable}")
  private Boolean royaltyEnable;

  private StringProperty sn = new SimpleStringProperty();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    log.info("页面初始化");
    rowCol.setSortable(false);
    // 创建一个整数的NumberFormat来格式化输入
    IntegerStringConverter integerStringConverter = new IntegerStringConverter();
    // 定义一个过滤器，只接受数字输入
    UnaryOperator<TextFormatter.Change> filter = change -> {
      String input = change.getText();
      if (input.matches("[0-9]*")) {
        // 如果输入是数字，则接受
        return change;
      }
      // 否则拒绝这次更改
      return null;
    };
    TextFormatter<Integer> textFormatter = new TextFormatter<>(integerStringConverter, null, filter);
    snTextField.setTextFormatter(textFormatter);
    snTextField.textProperty().bindBidirectional(sn);
    deselect(snTextField, startDatePicker, endDatePicker);
    searchBut.setOnAction(event -> query(1));
    resetBut.setOnAction(event -> reset());
    exportDetailBut.setOnAction(event -> exportDetail());
    if (!royaltyEnable) {
      exportSummaryBut.setOnAction(event -> exportSummary());
      headHbox.getChildren().remove(exportSummaryBut);
    }
    rowCol.setPrefWidth(80);
    rowCol.setMaxWidth(100);
    rowCol.setMinWidth(60);
    tableView.getColumns().set(0, rowCol);

    cashSnCol.setPrefWidth(100);
    cashSnCol.setMaxWidth(120);
    cashSnCol.setMinWidth(100);
    tableView.getColumns().set(1, cashSnCol);

    roomCol.setPrefWidth(100);
    roomCol.setMaxWidth(120);
    roomCol.setMinWidth(100);
    tableView.getColumns().set(2, roomCol);

    roomTypeCol.setPrefWidth(100);
    roomTypeCol.setMaxWidth(120);
    roomTypeCol.setMinWidth(100);
    tableView.getColumns().set(3, roomTypeCol);

    businessDayCol.setPrefWidth(100);
    businessDayCol.setMaxWidth(120);
    businessDayCol.setMinWidth(100);
    tableView.getColumns().set(4, businessDayCol);

    outMinFoodFreeCol.setPrefWidth(80);
    outMinFoodFreeCol.setMaxWidth(100);
    outMinFoodFreeCol.setMinWidth(80);
    tableView.getColumns().set(6, outMinFoodFreeCol);

    cashEmployee.setPrefWidth(100);
    cashEmployee.setMaxWidth(120);
    cashEmployee.setMinWidth(100);
    tableView.getColumns().set(14, cashEmployee);


    // 创建打开工作目录按钮
    Button workSpance = new Button("打开工作目录");
    workSpance.setOnAction(event -> new OpenDirectory().openWorkingDirectory("report"));

    // 创建一个可伸缩的Region
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox hBox = new HBox(0, spacer, workSpance);
    hBox.setStyle("-fx-alignment: center; -fx-padding: -5px;");
    headHbox.getChildren().add(spacer);
    headHbox.getChildren().add(workSpance);


  }


  private void deselect(Control... controls) {
    for (Control control : controls) {
      control.setFocusTraversable(false);
    }
  }

  private void exportDetail() {
    log.info("导出业绩明细");
    LocalDate startData = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();
    String cashId = snTextField.getText();
    Stage stage = (Stage) rootPane.getScene().getWindow();
    Loading loading = new Loading(rootPane);
    loading.show();
    AtomicReference<String> path = new AtomicReference<String>();
    new Thread(() -> {
      try {
        if (null == startData || null == endDate) {
          path.set(exportCashReport.export(loading, cashId, null, null));
        } else {
          path.set(exportCashReport.export(loading, cashId,
            startData.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)),
            endDate.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN))));
        }
        Platform.runLater(() -> {
          // 创建弹框
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("提示");
          alert.setHeaderText(path.get());
          alert.setContentText("业绩明细导出完成");
          Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
          s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
          // 显示弹框
          alert.showAndWait();
        });
      } catch (Exception e) {
        e.printStackTrace();
        new OpenDirectory().showErrorAlert("错误", null != e.getCause() ? e.getMessage() + e.getCause().getMessage() :
          e.getMessage());
      } finally {
        loading.close();
      }
    }).start();

  }

  //https://blog.csdn.net/weixin_44105483/article/details/108827400 加载中
  private void exportSummary() {
    log.info("准备导出总业绩");
    String cashId = snTextField.getText();
    if (StringUtils.isNotBlank(cashId)) {
      Alert.AlertType alertType = Alert.AlertType.ERROR;
      Alert alert = new Alert(alertType);
      alert.setTitle("警告");
      alert.setHeaderText("导出汇总业绩条件只能是业务日期范围");
      alert.setContentText("禁止导出");
      // 添加按钮
      alert.getButtonTypes().setAll(ButtonType.CLOSE);
      Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
      s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
      // 显示弹框
      alert.showAndWait();
      return;
    }
    String selectedFilePath = choose();
    if (null == selectedFilePath) {
      return;
    }
    AtomicReference<String> royalty = new AtomicReference<>();
    //Stage stage = (Stage) rootPane.getScene().getWindow();

    Loading loading = new Loading(rootPane);
    loading.show();
    new Thread(() -> {
      try {
        /**
         * 注意：如果期间涉及到更新UI的操作，需要Platform.runLater(() -> {
         */
        LocalDate startData = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        String contentText = "总业绩导出完成";
        String title = "导出成功";
        try {
          if (null == startData || null == endDate) {
            royalty.set(performance.royalty(loading, selectedFilePath, cashId, null, null));
          } else {
            royalty.set(performance.royalty(loading, selectedFilePath, cashId,
              startData.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)),
              endDate.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN))));
          }
        } catch (Exception e) {
          e.printStackTrace();
          alertType = Alert.AlertType.ERROR;
          Throwable cause = e.getCause();

          if (null != cause) {
            contentText = e.getCause().getMessage();
            if (null == contentText) {
              contentText = e.getCause().toString();
            }
          } else if (null != e.getMessage()) {
            contentText = e.getMessage();
          } else {
            contentText = "内部错误";
          }
          title = "导出失败";
        }

        Alert.AlertType finalAlertType = alertType;
        String finalContentText = contentText;
        String finalTitle = title;
        Platform.runLater(() -> {
          // 创建弹框
          Alert alert = new Alert(finalAlertType);
          alert.setTitle(finalTitle);
          alert.setHeaderText(royalty.get());
          alert.setContentText(finalContentText);
          // 添加按钮
          alert.getButtonTypes().setAll(ButtonType.CLOSE);
          Stage s = (Stage) alert.getDialogPane().getScene().getWindow();
          s.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
          // 显示弹框
          alert.showAndWait();
        });
        loading.showMessage("完成");
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        loading.close();
      }
    }).start();

  }

  private String choose() {
    String selectedFilePath = null;
    String name = "账单统计表";

    LocalDate startData = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();

    if (null != startData && null != endDate) {
      String start = startData.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN));
      String end = endDate.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN));
      name = String.format("%s【%s至%s】.xlsx", name, start, end);
    } else {
      name = name.concat(".xlsx");
    }

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("根据业务日期选择账单");

    // 设置初始目录
    fileChooser.setInitialDirectory(new File("report"));

    // 过滤器，只显示.xlsx文件
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("只能选择", name)
    );
    // 显示文件选择器
    File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

    if (selectedFile != null) {
      // 用户选择了文件，处理文件路径
      selectedFilePath = selectedFile.getAbsolutePath();
      log.info("选择了:{}", selectedFilePath);
    } else {
      log.info("取消");
    }
    return selectedFilePath;
  }


  private void reset() {
    sn.setValue(null);
    startDatePicker.setValue(null);
    endDatePicker.setValue(null);
    query(1);
  }

  public void query(int currentPage) {
    String value = sn.getValue();
    LocalDate startData = startDatePicker.getValue();
    LocalDate endDate = endDatePicker.getValue();
    ObservableList<CashProjection> sysDictDatas = FXCollections.observableArrayList();
    List<CashProjection> cash = new ArrayList<>();

    //cashSaveAsService.saves(cashProjectionMapper.findCash(null));

    IPage<Cash> cashRecords =
      cashService.findCashRecords(value, null != startData ?
          startData.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)) :
          null,
        null != endDate ? endDate.format(DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)) : null, currentPage);


    for (Cash record : cashRecords.getRecords()) {
      CashProjection cashProjection = new CashProjection();
      BeanUtil.copyProperties(record, cashProjection);
      cashProjection.setPayMode(record.getCPayMode());
      cashProjection.setStartTime(DateUtil.format(record.getStartTime(), NORM_DATETIME_PATTERN));
      cashProjection.setEndTime(DateUtil.format(record.getEndTime(), NORM_DATETIME_PATTERN));
      cashProjection.setCId(record.getId());
      cashProjection.setRoom(record.getRoomName());
      cashProjection.setRoomType(record.getRoomTypeName());
      cashProjection.setTrueMoney(record.getCTrueMoney().toString());
      cashProjection.setEName(record.getEmpName());
      cashProjection.setCashComment(record.getCCashComment());
      cashProjection.setRoomComment(record.getCRoomComment());
      cashProjection.setFixMoney(record.getFixMoney());
      cash.add(cashProjection);
    }
    sysDictDatas.addAll(cash);

    rowCol.setCellValueFactory(new PropertyValueFactory<>("row"));

    rowCol.setCellFactory(col -> {
      TableCell<CashProjection, String> cell = new TableCell<CashProjection, String>() {
        @Override
        public void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          this.setText(null);
          this.setGraphic(null);
          if (!empty) {
            int rowIndex = this.getIndex() + 1;
            this.setText(String.valueOf(rowIndex));
          }
        }
      };
      return cell;
    });

    cashSnCol.setCellValueFactory(new PropertyValueFactory<>("cId"));
    roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
    roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
    businessDayCol.setCellValueFactory(new PropertyValueFactory<>("businessDay"));
    businessDayCol.setCellFactory(col -> {
      TableCell<CashProjection, Date> cell = new TableCell<CashProjection, Date>() {
        @Override
        public void updateItem(Date item, boolean empty) {
          super.updateItem(item, empty);
          this.setText(null);
          this.setGraphic(null);
          if (!empty) {
            this.setText(DateUtil.format(item, "yyyy-MM-dd"));
          }
        }
      };
      return cell;
    });
    esNameCol.setCellValueFactory(new PropertyValueFactory<>("esName"));
    foodFreeCol.setCellValueFactory(new PropertyValueFactory<>("foodFee"));
    outMinFoodFreeCol.setCellValueFactory(new PropertyValueFactory<>("outMinFoodFee"));
    serviceFree.setCellValueFactory(new PropertyValueFactory<>("serviceFee"));
    serviceFree.setCellFactory(col -> {
      TableCell<CashProjection, String> cell = new TableCell<CashProjection, String>() {
        @Override
        public void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          this.setText(item);
          this.setGraphic(null);
          if (!empty) {
            this.setText(item);
          }
        }
      };
      return cell;
    });
    serviceFreeMoney.setCellValueFactory(new PropertyValueFactory<>("serviceFeeMoney"));
    trueFreeMoney.setCellValueFactory(new PropertyValueFactory<>("trueMoney"));
    fixMoney.setCellValueFactory(new PropertyValueFactory<>("fixMoney"));
    cashEmployee.setCellValueFactory(new PropertyValueFactory<>("eName"));
    startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
    endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

//    cashCommentCol.setCellValueFactory(new PropertyValueFactory<>("cashComment"));
//    cashCommentCol.setCellFactory(TextFieldTableCell.forTableColumn());
    roomCommentCol.setCellValueFactory(new PropertyValueFactory<>("roomComment"));
    roomCommentCol.setCellFactory(col -> {
      TableCell<CashProjection, String> cell = new TableCell<CashProjection, String>() {
        @Override
        public void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
            setTooltip(null);
          } else {
            Text text = new Text(item);
            setGraphic(text);
            text.wrappingWidthProperty().bind(widthProperty());
            text.textProperty().bind(itemProperty());
            setPrefHeight(Control.USE_COMPUTED_SIZE);
          }
        }
      };
      return cell;
    });
    tableView.setItems(sysDictDatas);

    tableView.getSelectionModel().setCellSelectionEnabled(false);
    initPage(currentPage, cashRecords);
    log.info("数据查询完成");
  }

  public void exit(ActionEvent actionEvent) {
    Application.switchView(MainStageView.class, MainStageView.class, null);
  }


  @Subscribe
  public void showEvent(ViewEvent viewEvent) {
    showNewWindow();
  }

  public void showNewWindow() {
    Scene scene = rootPane.getScene();
    if (null == scene) {
      return;
    }
    Stage stage = (Stage) scene.getWindow();
    Loading loading = new Loading(rootPane);
    loading.show();
    new Thread(() -> {
      try {
        Platform.runLater(() -> {
          if (null == rootPane.getScene()) {
            return;
          }
          initializeNewWindow(stage);
        });
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        loading.close();
      }
    }).start();

  }

  private void initializeNewWindow(Stage stage) {
    query(1);
    rootPane.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
          query(1);
        }
      }
    });

  }

  private void initPage(int currentPage, IPage<Cash> cashRecords) {
    Long total = cashRecords.getTotal();
    paginationPicker(total, pageSize, 5, currentPage);
    //监听点击动作事件
    page.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        int currentPage = page.getCurrentPage();
        log.info("当前选择页码：{}", currentPage);
        query(currentPage);

      }
    });
  }


  private void paginationPicker(Long count, int pageSize, int pageButton, int currentPage) {
    page.setTotal(count.intValue());//设置总数据量，默认0
    page.setPageSize(pageSize);//设置每页显示条数，默认30
    page.setPageButtonCount(pageButton);//设置页码按钮的数量，默认7，当总页数超过该值时会折叠，大于等于 5 且小于等于 21 的奇数
    page.setPaginationButtonFontSize(15);//设置分页字体大小，默认10(不小于2)
    page.setCurrentPage(currentPage);//设置当前选择页码，默认第一页（注意：必须放在所有设置条件之后）。不小于0 并且 不大于总页数
  }
}
