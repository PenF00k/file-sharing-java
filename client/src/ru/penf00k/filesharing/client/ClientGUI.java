package ru.penf00k.filesharing.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.penf00k.filesharing.client.controller.ClientMainWindowController;

public class ClientGUI extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("File sharing client");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/client_main_window.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> System.exit(0));

        ClientMainWindowController mainWindowController = loader.getController();
        mainWindowController.setPrimaryStage(primaryStage);

        primaryStage.show();
    }
}
