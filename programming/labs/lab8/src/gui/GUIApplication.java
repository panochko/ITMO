package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import language.LocalizationManager;
import SEclasses.Worker;

import java.io.IOException;
import java.util.ResourceBundle;

public class GUIApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        showLoginWindow();
    }

    public static void showLoginWindow() throws IOException {
        ResourceBundle rb = LocalizationManager.getResources();
        FXMLLoader loader = new FXMLLoader(GUIApplication.class.getResource("/fxml/login.fxml"), rb);
        Parent root = loader.load();
        LoginController ctrl = loader.getController();
        Stage stage = new Stage();
        if (ctrl != null) {
            ctrl.setStage(stage);
        }
        stage.setTitle(rb.getString("app.title"));
        stage.setScene(new Scene(root, 1200, 800));
        stage.centerOnScreen();
        stage.show();
    }

    public static void showWorkerFormDialog(Worker worker, String owner, String password, Runnable onSave) {
        try {
            ResourceBundle rb = LocalizationManager.getResources();
            FXMLLoader loader = new FXMLLoader(GUIApplication.class.getResource("/fxml/workerForm.fxml"), rb);
            Parent root = loader.load();
            WorkerFormController ctrl = loader.getController();
            if (ctrl == null) return;
            Stage dialog = new Stage();
            ctrl.setStage(dialog);
            ctrl.setWorker(worker, owner, password);
            ctrl.setOnSaveCallback(onSave);
            String titleKey = (worker == null) ? "button.add" : "button.update";
            dialog.setTitle(rb.getString(titleKey));
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            showAlert();
        }
    }

    private static void showAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText("Не удалось открыть форму");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}