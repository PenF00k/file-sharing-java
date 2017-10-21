package ru.penf00k.filesharing.network.server_gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.penf00k.filesharing.network.server_gui.model.Person;
import ru.penf00k.filesharing.network.server_gui.controller.ServerMainWindowController;
import ru.penf00k.filesharing.server.SQLAuthManager;

import ru.penf00k.filesharing.server.FileExchangerServer;
import ru.penf00k.filesharing.server.ServerListener;

public class ServerGUI extends Application implements ServerListener {

    private final FileExchangerServer fileExchangerServer = new FileExchangerServer(this, new SQLAuthManager());

    private ObservableList<Person> personData = FXCollections.observableArrayList(); //TODO

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("File exchange fileExchangerServer");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("server_main_window.fxml"));
        Parent root = loader.load();
//        Parent root = FXMLLoader.load(getClass().getResource("controller/server_main_window.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> System.exit(0));

        ServerMainWindowController mainWindowController = loader.getController();
        mainWindowController.setFileExchangerServer(fileExchangerServer);
    }

// TODO
//    public ObservableList<Person> getPersonData() {
//        return personData;
//    }


    @Override
    public void onServerLog(FileExchangerServer fileExchangerServer, String msg) {
        //TODO куда писать логи
        System.out.println(msg);
    }
}
