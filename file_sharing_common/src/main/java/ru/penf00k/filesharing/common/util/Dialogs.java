package ru.penf00k.filesharing.common.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import ru.penf00k.filesharing.common.ServerMessage;

import java.util.Optional;

public class Dialogs {

    public static void showErrorDialog(String title, String header, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showErrorDialog(ServerMessage sm) {
        showErrorDialog("Error", "Server error", sm.getResponse().getMessage());
    }

    public static void showConfirmDialog(String title, String header, String message, Runnable onConfirm, Runnable onCancel) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);

            ButtonType btnConfirm = new ButtonType("Yes");
            ButtonType btnCancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(btnConfirm, btnCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == btnConfirm && onConfirm != null) onConfirm.run();
            else if (result.get() == btnCancel && onCancel != null) onCancel.run();
            alert.close();
        });
    }

    public static void showRenameDialog(String name, Rename onRename) {
        Platform.runLater(() -> {
            Utils.FileNameExtension fnm = Utils.fileNameToFNM(name);
//            String extension = name.substring(name.lastIndexOf(".") + 1);
//            String nameNoExtension = name.substring(0, name.lastIndexOf("."));
            TextInputDialog dialog = new TextInputDialog(fnm.getName());
            dialog.setTitle("Rename file");
            dialog.setHeaderText("Enter new name and press OK");
            dialog.setContentText("New name: ");
            Optional<String> result = dialog.showAndWait();
//            result.ifPresent(s -> onRename.onNewFileName(String.format("%s.%s", s, extension)));
            result.ifPresent(s -> {
                fnm.setName(s);
                onRename.onNewFileName(Utils.getFileNameFromFNM(fnm));
            });
        });
    }
}
