package ru.penf00k.filesharing.client.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientAuthWindowController implements EventHandler<ActionEvent> {

    private static final String NICKNAME_PATTERN = "^[A-Za-z]\\w{2,14}$";
    private static final String PASSWORD_PATTERN = "^\\w{3,15}$";

    @FXML
    private Button btnSelectLogin;
    @FXML
    private Button btnSelectRegister;
    @FXML
    private TextField tfNickname;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfConfirmPassword;
    @FXML
    private Button btnProceedLoginRegister;
    @FXML
    private Label lblForgotPassword;

    public ClientAuthWindowController() {
    }

    @FXML
    private void initialize() {
        setStageFromState(State.LOGIN);
        btnSelectLogin.setOnAction(this);
        btnSelectRegister.setOnAction(this);
        btnProceedLoginRegister.setOnAction(this);
    }

    private void setStageFromState(State state) {
        switch (state) {
            case LOGIN:
                tfConfirmPassword.setVisible(false);
                btnProceedLoginRegister.setText("Login");
                lblForgotPassword.setVisible(true);
                break;
            case REGISTER:
                tfConfirmPassword.setVisible(true);
                btnProceedLoginRegister.setText("Register");
                lblForgotPassword.setVisible(false);
                break;
            default: throw new RuntimeException("Invalid stage state");
        }
    }

    // click handler
    @Override
    public void handle(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof Button) {
            String id = ((Button) src).getId();
            System.out.println(id + " was clicked"); //TODO delete
            if (id.equals(btnSelectLogin.getId())) setStageFromState(State.LOGIN);
            if (id.equals(btnSelectRegister.getId())) setStageFromState(State.REGISTER);
            if (id.equals(btnProceedLoginRegister.getId())) tryLogin();
        }
    }

    private void tryLogin() {
        if (!isInputValid()) return;
        System.out.println("Input ok"); // TODO delete
        //TODO проверить, есть ли в базе такая пара логин-пароль
    }

    private boolean isInputValid() {
        //TODO сделать как в android setError
        StringBuilder sbErrorMessage = new StringBuilder();

        if (!tfNickname.getText().matches(NICKNAME_PATTERN)) {
            sbErrorMessage.append("Login must start with a letter and have length from 3 to 15 characters\n");
        }
        if (!tfPassword.getText().matches(PASSWORD_PATTERN)) {
            sbErrorMessage.append("Password must contain only letters and digits and have length from 3 to 15 characters\n");
        }

        if (sbErrorMessage.length() == 0) {
            return true;
        } else showAlertDialog(Alert.AlertType.ERROR,
                    "Errors in fields",
                    "Please correct the invalid fields",
                    sbErrorMessage.toString());

        return false;
    }

    private void showAlertDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
