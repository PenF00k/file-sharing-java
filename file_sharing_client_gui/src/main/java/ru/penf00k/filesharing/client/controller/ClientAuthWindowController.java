package ru.penf00k.filesharing.client.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.penf00k.filesharing.common.AbstractMessage;
import ru.penf00k.filesharing.common.AuthMessage;
import ru.penf00k.filesharing.common.RegisterMessage;
import ru.penf00k.filesharing.network.SocketThread;

import java.io.*;
import java.util.Properties;

public class ClientAuthWindowController implements EventHandler<ActionEvent> {

    private static final String USERNAME_PATTERN = "^[A-Za-z]\\w{2,14}$";
    private static final String PASSWORD_PATTERN = "^\\w{3,15}$";
    private State currentState;
    private SocketThread socketThread;

    @FXML
    private Button btnSelectLogin;
    @FXML
    private Button btnSelectRegister;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfConfirmPassword;
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
        initProperties();
    }

    private void initProperties() {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(ClientMainWindowController.PROPERTIES_FILE)) {
            properties.load(is);
            tfUsername.setText(properties.getProperty(ClientMainWindowController.PROPERTY_USERNAME));
            pfPassword.setText(properties.getProperty(ClientMainWindowController.PROPERTY_PASSWORD));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStageFromState(State state) {
        currentState = state;
        switch (state) {
            case LOGIN:
                pfConfirmPassword.setVisible(false);
                btnProceedLoginRegister.setText("Login");
                lblForgotPassword.setVisible(true);
                break;
            case REGISTER:
                pfConfirmPassword.setVisible(true);
                btnProceedLoginRegister.setText("Register");
                lblForgotPassword.setVisible(false);
                break;
            default:
                throw new RuntimeException("Invalid stage state");
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
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(ClientMainWindowController.PROPERTIES_FILE)) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStream os = new FileOutputStream(ClientMainWindowController.PROPERTIES_FILE)) {
            properties.setProperty(ClientMainWindowController.PROPERTY_USERNAME, tfUsername.getText());
            properties.setProperty(ClientMainWindowController.PROPERTY_PASSWORD, pfPassword.getText());
            properties.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Input ok"); // TODO delete
        //TODO проверить, есть ли в базе такая пара логин-пароль
        AbstractMessage message = null;
        switch (currentState) {
            case LOGIN:
                //TODO
//                socketThread.sendMessageObject(new TextMessage("Text was delivered ok"));
                message = new AuthMessage(tfUsername.getText(), pfPassword.getText());
                break;
            case REGISTER:
                message = new RegisterMessage(tfUsername.getText(), pfPassword.getText());
                break;
            case FORGOT_PASS:
                //TODO
                break;
            default:
                throw new RuntimeException("Invalid stage state");
        }
        if (message != null) socketThread.sendMessageObject(message);
    }

    private boolean isInputValid() {
        //TODO сделать как в android setError
        StringBuilder sbErrorMessage = new StringBuilder();

        if (!tfUsername.getText().matches(USERNAME_PATTERN)) {
            sbErrorMessage.append("Username must start with a letter and have length from 3 to 15 characters\n");
        }
        if (!pfPassword.getText().matches(PASSWORD_PATTERN)) {
            sbErrorMessage.append("Password must contain only letters and digits and have length from 3 to 15 characters\n");
        }
        if (currentState == State.REGISTER && !pfPassword.getText().equals(pfConfirmPassword.getText())) {
            sbErrorMessage.append("Password and Confirm Password fields must be the same\n");
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

    public void setSocketThread(SocketThread socketThread) {
        this.socketThread = socketThread;
    }
}
