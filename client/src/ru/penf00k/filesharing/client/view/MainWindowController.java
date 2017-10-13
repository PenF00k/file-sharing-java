package ru.penf00k.filesharing.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class MainWindowController implements SocketThreadListener {

    private static final String LOGIN_PATTERN = "^[A-Za-z]\\w{2,14}$";
    private static final String PASSWORD_PATTERN = "^\\w{3,15}$";
    private static final String IP_PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    private static final String PORT_PATTERN = "^\\d+$";

    @FXML
    private TextField tfLogin;
    @FXML
    private TextField tfPassword;
    @FXML
    private TextField tfIPAddress;
    @FXML
    private TextField tfPort;
    @FXML
    private Label lblPathToFile;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;

    private Stage primaryStage;

    private Socket socket;
    private SocketThread socketThread;

    private String msg;
    private int port;

    public MainWindowController() {
    }

    @FXML
    private void initialize() {
        System.out.println("ClientGUI controller initialize()"); //TODO
        tfLogin.setText("PenF00k");
        tfPassword.setText("123456");
        tfIPAddress.setText("127.0.0.1");
        tfPort.setText("9000");
    }

    @FXML
    private void connect() {
        System.out.println("connect()"); //TODO
        if (isInputValid()) {
            System.out.println("message: " + tfLogin.getText() + ": " + tfPassword.getText()
                    + ": " + tfIPAddress.getText() + ": " + tfPort.getText()); //TODO
            try {
                socket = new Socket(tfIPAddress.getText(), port);
                socketThread = new SocketThread("SocketThread", this, socket);
                setFieldsEditable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInputValid() {
        //TODO сделать как в android setError
        String errorMessage = "";

        if (!tfLogin.getText().matches(LOGIN_PATTERN)) {
            errorMessage += "Login must start with a letter and have length from 3 to 15 characters\n";
        }
        if (!tfPassword.getText().matches(PASSWORD_PATTERN)) {
            errorMessage += "Password must contain only letters and digits and have length from 3 to 15 characters\n";
        }
        if (!tfIPAddress.getText().matches(IP_PATTERN)) {
            errorMessage += "IP address is not valid\n";
        }
        if (!tfPort.getText().matches(PORT_PATTERN)) {
            errorMessage += "Port must be in range from 1024 to 65535\n";

        } else {
            port = Integer.parseInt(tfPort.getText());
            if (port < 1024 || port > 65535){
                errorMessage += "Port must be in range from 1024 to 65535\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.initOwner();
            alert.setTitle("Errors in fields");
            alert.setHeaderText("Please correct the invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        }

        return false;
    }

    private void setFieldsEditable(boolean editable) {
        tfLogin.setDisable(editable);
        tfPassword.setDisable(editable);
        tfIPAddress.setDisable(editable);
        tfPort.setDisable(editable);

        btnConnect.setDisable(editable);
        btnDisconnect.setDisable(!editable);
    }

    private boolean isEmptyField(TextField tf) {
        //TODO delete
        return tf.getText() == null || tf.getText().length() == 0;
    }

    @FXML
    private void disconnect() {
        System.out.println("disconnect()"); //TODO
        socketThread.close();
        try {
            socket.close();
            setFieldsEditable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectFile() {
        System.out.println("selectFile()"); //TODO
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to upload");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            System.out.println("File path: " + file.getAbsolutePath());
            socketThread.sendMessage("File path: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void sendFile() {
        System.out.println("sendFile()"); //TODO
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // SocketThreadListener
    @Override
    public void onStartSocketThread(SocketThread socketThread) {

    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {

    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {

    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        System.out.println("Client received string: " + value);
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {

    }
}
