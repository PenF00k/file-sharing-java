package ru.penf00k.filesharing.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.penf00k.filesharing.common.AbstractMessage;
import ru.penf00k.filesharing.common.FileMessage;
import ru.penf00k.filesharing.common.TextMessage;
import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.io.*;
import java.net.Socket;

public class ClientMainWindowController implements SocketThreadListener {

    private static final String LOGIN_PATTERN = "^[A-Za-z]\\w{2,14}$";
    private static final String PASSWORD_PATTERN = "^\\w{3,15}$";
    private static final String IP_PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    private static final String PORT_PATTERN = "^\\d+$";
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5; // in bytes

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
    @FXML
    private Button btnChooseFile;
    @FXML
    private Button btnSendFile; //TODO убрать эту кнопку, не нужна она

    private Stage primaryStage;

    private Socket socket;
    private SocketThread socketThread;

    private final FileChooser fileChooser = new FileChooser();
    private File file;
    private String msg;
    private int port;

    public ClientMainWindowController() {
    }

    @FXML
    private void initialize() {
        System.out.println("ClientGUI controller initialize()"); //TODO
        tfLogin.setText("PenF00k");
        tfPassword.setText("123456");
        tfIPAddress.setText("127.0.0.1");
        tfPort.setText("9000");
        setFieldsDisabled(false);
        btnSendFile.setDisable(true);
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
                setFieldsDisabled(true);
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

    private void setFieldsDisabled(boolean disabled) {
        tfLogin.setDisable(disabled);
        tfPassword.setDisable(disabled);
        tfIPAddress.setDisable(disabled);
        tfPort.setDisable(disabled);

        btnConnect.setDisable(disabled);
        btnDisconnect.setDisable(!disabled);
        btnChooseFile.setDisable(!disabled);
        btnSendFile.setDisable(disabled);
    }

    private boolean isEmptyField(TextField tf) {
        //TODO delete
        return tf.getText() == null || tf.getText().length() == 0;
    }

    @FXML
    private void disconnect() {
        System.out.println("disconnect()"); //TODO
        file = null;
        lblPathToFile.setText("");
        socketThread.close();
        try {
            socket.close();
            setFieldsDisabled(false);
            btnSendFile.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final File defaultDirectory = new File("C:\\Users\\curly\\Desktop");

    @FXML
    private void chooseFile() {
        fileChooser.setTitle("Choose a file to upload");
        do {
            fileChooser.setInitialDirectory(file == null ? defaultDirectory : file.getParentFile());
            file = fileChooser.showOpenDialog(primaryStage);
            if (file == null) return;
            System.out.println("file length = " + file.length());
            if (file.length() <= MAX_FILE_SIZE) break;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File size is too large");
            alert.setHeaderText("Please choose another file");
            alert.setContentText("Please choose a file with size less than 5 mb");
            alert.showAndWait();
        } while (file.length() > MAX_FILE_SIZE);
        lblPathToFile.setText(file.getAbsolutePath());
        btnSendFile.setDisable(false);
    }

    @FXML
    private void sendFile() {
        if (file != null) {
            FileMessage fileMessage = new FileMessage(file, (int) file.length());
            socketThread.sendMessageObject(fileMessage);
            socketThread.sendFile(file);
            file = null;
            lblPathToFile.setText("");
            btnSendFile.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No file chosen");
            alert.setHeaderText("Please choose a file");
            alert.setContentText("Please choose a file");
            alert.showAndWait();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // SocketThreadListener
    @Override
    public void onStartSocketThread(SocketThread socketThread) {
        System.out.println("SocketThread started");
    }

    @Override
    public void onStopSocketThread(SocketThread socketThread) {
        System.out.println("SocketThread stopped");
        //TODO disconnect this client with error
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {

    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {
        System.out.println("Client received string: " + value);
    }

    @Override
    public void onReceiveObjectMessage(SocketThread socketThread, Socket socket, AbstractMessage message) {
        int type;
        if (message instanceof TextMessage) {
            TextMessage tm = (TextMessage) message;
            type = tm.getType();
            String text = tm.getText();
            System.out.println("TextMessage from server: " + text);
        }
    }

    @Override
    public void onReceiveFile(SocketThread socketThread, Socket socket, byte[] fileBytes) {
        //TODO
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
