package com.hrzj.yule.calc.fx.query;

import com.hrzj.yule.calc.config.CredentialsProperties;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author lifengquan
 * @description: 请添加类注释
 * @date 2024-05-14 18:00
 */
public class LoginDialog extends Dialog<Void> {

  private boolean authenticated = false;

  private CredentialsProperties credentialsProperties;


  public LoginDialog(CredentialsProperties credentialsProperties) {
    this.credentialsProperties = credentialsProperties;
    Stage stage = (Stage) getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));

    setTitle("登录");

    getDialogPane().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    getDialogPane().getStyleClass().add("dialog-pane");
    Label headerLabel = new Label("请输入您的用户名和密码:");
    headerLabel.getStyleClass().add("dialog-header");

    Button loginButton = new Button("登录");
    loginButton.setDefaultButton(true);

    Label usernameLabel = new Label("用户名:");
    TextField usernameField = new TextField();

    Label passwordLabel = new Label("密码:");
    PasswordField passwordField = new PasswordField();

    Label errorMessage = new Label();
    errorMessage.getStyleClass().add("error-message");

    usernameLabel.getStyleClass().add("label");
    passwordLabel.getStyleClass().add("label");
    usernameField.getStyleClass().add("text-field");
    passwordField.getStyleClass().add("password-field");
    loginButton.getStyleClass().add("button");

    GridPane grid = new GridPane();
    grid.getStyleClass().add("grid-pane");
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 10, 10, 10));

    grid.add(headerLabel, 0, 0, 2, 1);
    grid.add(usernameLabel, 0, 1);
    grid.add(usernameField, 1, 1);
    GridPane.setHgrow(usernameField, Priority.ALWAYS);

    grid.add(passwordLabel, 0, 2);
    grid.add(passwordField, 1, 2);
    GridPane.setHgrow(passwordField, Priority.ALWAYS);

    grid.add(loginButton, 1, 3);
    grid.add(errorMessage, 0, 4, 2, 1);

    getDialogPane().setContent(grid);
    getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

    loginButton.setOnAction(e -> {
      String username = usernameField.getText();
      String password = passwordField.getText();
      if (authenticate(username, password)) {
        this.authenticated = true;
        close();
      } else {
        errorMessage.setText("登录失败。请检查您的用户名和密码。");
      }
    });

    initModality(Modality.APPLICATION_MODAL);
  }

  private boolean authenticate(String username, String password) {
    List<CredentialsProperties.Credential> credentials = this.credentialsProperties.getCredentials();
    if (CollectionUtils.isNotEmpty(credentials)) {
      List<CredentialsProperties.Credential> collect =
        credentials.stream().filter(credential -> credential.getUsername().equals(username) && credential.getPassword().equals(password)).collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(collect)) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }
}
