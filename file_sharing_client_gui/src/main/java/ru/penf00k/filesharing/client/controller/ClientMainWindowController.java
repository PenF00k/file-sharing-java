package ru.penf00k.filesharing.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.penf00k.filesharing.common.*;
import ru.penf00k.filesharing.network.SocketThread;
import ru.penf00k.filesharing.network.SocketThreadListener;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;

public class ClientMainWindowController implements SocketThreadListener {

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5; // in bytes
    static final String PROPERTIES_FILE = "./fs.properties";
    static final String PROPERTY_USERNAME = "username";
    static final String PROPERTY_PASSWORD = "password";

    @FXML
    private Label lblPathToFile;
    @FXML
    private Label lblServerMessage;
    @FXML
    private Button btnOpenAuthWindow;
    @FXML
    private Button btnChooseFile;
    @FXML
    private Button btnSendFile; //TODO убрать эту кнопку, не нужна она

    private Stage primaryStage;

    private Socket socket;
    private SocketThread socketThread;

    private final FileChooser fileChooser = new FileChooser();
    private File file;
    private String ipAddress;
    private int port;
    private String username;
    private String password;
    private boolean isAuthorised;

    public ClientMainWindowController() {
    }

    @FXML
    private void initialize() {
        System.out.println("ClientGUI controller initialize()"); //TODO
        setFieldsDisabled(false);
        btnSendFile.setDisable(true);
        initProperties();
        connect();
//        tryAuthorise();
    }

    private void initProperties() {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(is);
            ipAddress = properties.getProperty("ip-address");
            port = Integer.parseInt(properties.getProperty("port"));
            username = properties.getProperty(PROPERTY_USERNAME);
            password = properties.getProperty(PROPERTY_PASSWORD);
            System.out.println("username: " + username + ", password: " + password);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> lblServerMessage.
                    setText("Could't read properties from file 'fs.properties'. Check if it exists"));
        }
    }

    private void connect() {
        System.out.println("connect()"); //TODO
        if (isInputValid()) {
            try {
                socket = new Socket(ipAddress, port);
                socketThread = new SocketThread("SocketThread", this, socket);
                setFieldsDisabled(true);
                lblServerMessage.setText("Connected to server successfully");
//                socketThread.sendMessageObject(new TextMessage("abracadabra"));
            } catch (IOException e) {
                e.printStackTrace();
                if (e.getMessage().equals("Connection refused: connect"))
                    lblServerMessage.setText("Server is offline"); //TODO сделать диалог с ошибкой
            }
        }
    }

    private void tryAuthorise() {
        if (username != null && password != null) {
            socketThread.sendMessageObject(new AuthMessage(username, password));
        } else showAuthWindow();
    }

    private boolean isInputValid() {
        //TODO сделать как в android setError
        String errorMessage = "";

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
        btnChooseFile.setDisable(!disabled);
        btnSendFile.setDisable(disabled);
    }

    private boolean isEmptyField(TextField tf) {
        //TODO delete
        return tf.getText() == null || tf.getText().length() == 0;
    }

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

    private Stage authStage;

    @FXML
    private void showAuthWindow() {
        Platform.runLater(() -> {
            try {
                if (authStage == null) {
                    authStage = new Stage();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("client_auth_window.fxml"));
                    Parent root = loader.load();
                    ClientAuthWindowController controller = loader.getController();
                    controller.setSocketThread(socketThread);
                    authStage.setTitle("Authentication");
                    authStage.initModality(Modality.WINDOW_MODAL);
                    authStage.initOwner(primaryStage);
                    authStage.setScene(new Scene(root));
                }
                authStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
        //TODO сделать диалог при отвале сервера и убрать закрытие stage
        Platform.runLater(() -> {
            if (authStage != null) authStage.close();
        });
        setFieldsDisabled(false);
        //TODO disconnect this client with error
    }

    @Override
    public void onReadySocketThread(SocketThread socketThread, Socket socket) {
        tryAuthorise();
    }

    @Override
    public void onReceiveString(SocketThread socketThread, Socket socket, String value) {

    }

    @Override
    public void onReceiveObjectMessage(SocketThread socketThread, Socket socket, AbstractMessage message) {
        if (message instanceof TextMessage) {
            TextMessage tm = (TextMessage) message;
            String text = tm.getText();
            System.out.println("TextMessage from server: " + text);
            Platform.runLater(() -> lblServerMessage.setText(text));
        } else if (message instanceof ServerMessage) {
            ServerMessage sm = (ServerMessage) message;
            Platform.runLater(() -> lblServerMessage.setText(sm.getResponse().getMessage()));
            switch (sm.getResponse()) {
                case AUTHORIZATION_OK:
                    if (authStage != null) Platform.runLater(() -> authStage.close());
                    break;
                case NEED_AUTHORIZATION:
                    showAuthWindow();
                    break;
//                case SERVER_STOP:
//                    Platform.runLater(() -> lblServerMessage.setText(sm.getResponse().getMessage()));
//                    break;
            }
        }
    }

    @Override
    public void onReceiveFile(SocketThread socketThread, Socket socket, ObjectInputStream ois) {
        //TODO
    }

    @Override
    public void onExceptionSocketThread(SocketThread socketThread, Socket socket, Exception e) {
        e.printStackTrace();
    }
}
