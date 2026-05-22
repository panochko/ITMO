package gui;

import client.ClientNetwork;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import language.LocalizationManager;

import java.io.IOException;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ChoiceBox<String> languageChoice;
    private Stage stage;

    @FXML public void initialize() {
        setupLanguageChoice();
        if (usernameField != null) usernameField.textProperty().addListener((o, ov, nv) -> hideError());
        if (passwordField != null) passwordField.textProperty().addListener((o, ov, nv) -> hideError());
    }

    public void setStage(Stage stage) { this.stage = stage; }

    private void setupLanguageChoice() {
        if (languageChoice == null) return;
        languageChoice.getItems().addAll("Русский", "Íslenska", "Français", "Español");
        updateLanguageSelection();
        languageChoice.setOnAction(e -> changeLanguage());
    }

    private void updateLanguageSelection() {
        String currentLang = switch (LocalizationManager.getResources().getLocale().getLanguage()) {
            case "is" -> "Íslenska";
            case "fr" -> "Français";
            case "es" -> "Español";
            default -> "Русский";
        };
        languageChoice.setValue(currentLang);
    }

    private void changeLanguage() {
        if (languageChoice == null) return;
        int selectedIndex = languageChoice.getSelectionModel().getSelectedIndex();
        LocalizationManager.setLocaleByIndex(selectedIndex);
        updateLanguageSelection();
        reloadScene();
    }

    private void reloadScene() {
        try {
            ResourceBundle rb = LocalizationManager.getResources();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), rb);
            Parent root = loader.load();
            LoginController ctrl = loader.getController();
            if (ctrl != null) ctrl.setStage(stage);
            Scene scene = new Scene(root, 1200, 800);
            if (stage != null) {
                stage.setTitle(rb.getString("app.title"));
                stage.setScene(scene);
            }
        } catch (IOException e) {
            showError("Ошибка интерфейса");
        }
    }

    @FXML private void handleLogin() {
        if (usernameField == null || passwordField == null) return;
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        if (!user.matches("s\\d{6}")) { showError("Формат ISU: sXXXXXX"); return; }
        if (pass.isEmpty()) { showError("Введите пароль"); return; }
        try {
            if (ClientNetwork.authorize(user, pass)) {
                openMainWindow(user, pass);
            } else {
                showError("Неверные данные");
            }
        } catch (Exception e) {
            showError("Ошибка сети");
        }
    }

    private void showError(String msg) {
        if (errorLabel != null) { errorLabel.setText(msg); errorLabel.setVisible(true); }
    }

    private void hideError() {
        if (errorLabel != null) errorLabel.setVisible(false);
    }

    private void openMainWindow(String user, String pass) {
        try {
            ResourceBundle rb = LocalizationManager.getResources();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"), rb);
            Parent root = loader.load();
            MainWindowController ctrl = loader.getController();
            if (ctrl != null) ctrl.setCurrentUser(user, pass);
            Scene scene = new Scene(root, 1400, 900);
            if (stage != null) {
                stage.setTitle(rb.getString("app.title"));
                stage.setScene(scene);
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            showError("Ошибка загрузки");
        }
    }
}