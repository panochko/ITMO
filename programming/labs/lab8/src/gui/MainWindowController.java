package gui;

import SEclasses.*;
import client.ClientNetwork;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import language.LocalizationManager;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class MainWindowController {

    @FXML private Label currentUserLabel;
    @FXML private TableView<Worker> workersTable;
    @FXML private TableColumn<Worker, Integer> idColumn;
    @FXML private TableColumn<Worker, String> nameColumn;
    @FXML private TableColumn<Worker, String> coordinatesColumn;
    @FXML private TableColumn<Worker, Integer> salaryColumn;
    @FXML private TableColumn<Worker, String> positionColumn;
    @FXML private TableColumn<Worker, String> statusColumn;
    @FXML private TableColumn<Worker, String> ownerColumn;
    @FXML private TextField filterField;
    @FXML private Canvas visualizationCanvas;
    @FXML private ChoiceBox<String> languageChoice;

    private ObservableList<Worker> workersData;
    private FilteredList<Worker> filteredData;
    public SortedList<Worker> sortedData;
    private GraphicsContext graphicsContext;
    private String currentUser;
    private String currentPassword;
    private final Map<String, Color> userColorCache;
    private Stage stage;

    public MainWindowController() {
        this.userColorCache = new HashMap<>();
    }

    @FXML public void initialize() {
        workersData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(workersData, p -> true);
        sortedData = new SortedList<>(filteredData);
        if (workersTable != null) {
            sortedData.comparatorProperty().bind(workersTable.comparatorProperty());
            workersTable.setItems(sortedData);
        }
        if (visualizationCanvas != null) {
            visualizationCanvas.setWidth(600);
            visualizationCanvas.setHeight(500);
            graphicsContext = visualizationCanvas.getGraphicsContext2D();
            visualizationCanvas.setOnMouseClicked(this::handleCanvasClick);
        }
        setupTableColumns();
        if (filterField != null) filterField.textProperty().addListener((o, ov, nv) -> applyFilter(nv));
        setupLanguageChoice();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setupTableColumns() {
        if (workersTable == null) return;
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        coordinatesColumn.setCellValueFactory(c -> {
            Coordinates coordinates = c.getValue().getCoordinates();
            if (coordinates != null) {
                double x = coordinates.getX() != null ? coordinates.getX().doubleValue() : 0.0;
                double y = coordinates.getY();
                return new javafx.beans.property.SimpleStringProperty(String.format("(%.1f,%.1f)", x, y));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        positionColumn.setCellValueFactory(c -> {
            Position p = c.getValue().getPosition();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.toString() : "");
        });
        statusColumn.setCellValueFactory(c -> {
            Status s = c.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(s != null ? s.toString() : "");
        });
        ownerColumn.setCellValueFactory(c -> {
            String owner = c.getValue().getOwner();
            return new javafx.beans.property.SimpleStringProperty(owner != null ? owner : "");
        });
        ownerColumn.setPrefWidth(120);
    }

    private void setupLanguageChoice() {
        if (languageChoice != null) {
            languageChoice.getItems().addAll("Русский", "Íslenska", "Français", "Español");
            updateLanguageSelection();
            languageChoice.setOnAction(e -> changeLanguage());
        }
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
        reloadMainWindow();
        reloadMainWindow();
    }

    private void reloadMainWindow() {
        try {
            ResourceBundle rb = LocalizationManager.getResources();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"), rb);
            Parent root = loader.load();

            MainWindowController newCtrl = loader.getController();
            if (newCtrl != null && stage != null) {
                newCtrl.setStage(stage);
                newCtrl.currentUser = this.currentUser;
                newCtrl.currentPassword = this.currentPassword;
                newCtrl.workersData = this.workersData;
                newCtrl.filteredData = this.filteredData;
                newCtrl.sortedData = this.sortedData;
                newCtrl.graphicsContext = this.graphicsContext;
                newCtrl.userColorCache.putAll(this.userColorCache);
                newCtrl.initialize();
                newCtrl.loadWorkers();

                stage.setTitle(rb.getString("app.title"));
                stage.setScene(new Scene(root, 1400, 900));
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сменить язык");
        }
    }

    public void setCurrentUser(String username, String password) {
        this.currentUser = username;
        this.currentPassword = password;
        if (currentUserLabel != null) {
            String lbl = LocalizationManager.getString("user.current");
            currentUserLabel.setText(lbl + ": " + username);
        }
        loadWorkers();
    }

    private void loadWorkers() {
        try {
            List<Worker> list = ClientNetwork.showWorkers();
            workersData.setAll(list);
            drawVisualization();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить данные");
        }
    }

    private void applyFilter(String text) {
        if (filteredData == null) return;
        if (text == null || text.isEmpty()) {
            filteredData.setPredicate(p -> true);
            return;
        }
        String lower = text.toLowerCase();
        filteredData.setPredicate(w ->
                (w.getName() != null && w.getName().toLowerCase().contains(lower)) ||
                        String.valueOf(w.getSalary()).contains(lower) ||
                        (w.getPosition() != null && w.getPosition().toString().toLowerCase().contains(lower)) ||
                        (w.getStatus() != null && w.getStatus().toString().toLowerCase().contains(lower))
        );
    }

    @FXML private void handleRefresh() {
        loadWorkers();
        showAlert(Alert.AlertType.INFORMATION, "Обновлено", "Данные обновлены");
    }

    @FXML private void handleSort() {
        List<Worker> sortedList = workersData.stream()
                .sorted(Comparator.comparingInt(Worker::getId))
                .toList();
        workersData.clear();
        workersData.addAll(sortedList);
    }

    @FXML private void handleLogout() {
        Platform.exit();
    }

    @FXML private void handleAdd() {
        GUIApplication.showWorkerFormDialog(null, currentUser, currentPassword, this::loadWorkers);
    }

    @FXML private void handleUpdate() {
        Worker selected = workersTable != null ? workersTable.getSelectionModel().getSelectedItem() : null;
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите работника для обновления");
            return;
        }
        GUIApplication.showWorkerFormDialog(selected, currentUser, currentPassword, this::loadWorkers);
    }

    @FXML private void handleDelete() {
        Worker selected = workersTable != null ? workersTable.getSelectionModel().getSelectedItem() : null;
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Внимание", "Выберите работника для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление работника");
        confirm.setContentText("Вы уверены, что хотите удалить \"" + selected.getName() + "\" (ID: " + selected.getId() + ")?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String res = ClientNetwork.deleteWorker(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Успех", res);
                    loadWorkers();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось удалить: " + e.getMessage());
                }
            }
        });
    }

    @FXML private void handleClear() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Очистка коллекции");
        confirm.setContentText("Вы уверены, что хотите удалить ВСЕХ своих работников?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String res = ClientNetwork.clearWorkers();
                    showAlert(Alert.AlertType.INFORMATION, "Успех", res);
                    loadWorkers();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось очистить: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void drawVisualization() {
        if (graphicsContext == null || visualizationCanvas == null) return;
        graphicsContext.clearRect(0, 0, visualizationCanvas.getWidth(), visualizationCanvas.getHeight());
        if (workersData == null || workersData.isEmpty()) return;

        double width = visualizationCanvas.getWidth();
        double height = visualizationCanvas.getHeight();
        graphicsContext.setStroke(Color.LIGHTGRAY);
        graphicsContext.setLineWidth(0.5);
        for (double x = 0; x < width; x += 50) graphicsContext.strokeLine(x, 0, x, height);
        for (double y = 0; y < height; y += 50) graphicsContext.strokeLine(0, y, width, y);

        int index = 0;
        for (Worker w : workersData) drawWorkerWithAnimation(w, index++);
    }

    private void drawWorkerWithAnimation(Worker w, int index) {
        if (w == null || w.getCoordinates() == null) return;
        Coordinates coordinates = w.getCoordinates();
        double canvasWidth = visualizationCanvas.getWidth();
        double canvasHeight = visualizationCanvas.getHeight();
        double x = normalizeCoordinate(coordinates.getX(), canvasWidth);
        double y = normalizeCoordinate(coordinates.getY(), canvasHeight);
        double size = 30;
        Color color = getUserColor(w.getOwner() != null ? w.getOwner() : String.valueOf(index));

        graphicsContext.setFill(color);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(2);
        graphicsContext.fillOval(x - size/2, y - size/2, size, size);
        graphicsContext.strokeOval(x - size/2, y - size/2, size, size);
        graphicsContext.setFill(Color.BLACK);
        if (w.getName() != null) graphicsContext.fillText(w.getName(), x - size/2, y + size + 15);
    }

    private void drawWorker(Worker w, int idx) { drawWorkerWithAnimation(w, idx); }

    private double normalizeCoordinate(Number value, double max) {
        if (value == null) return max / 2;
        double d = value.doubleValue();
        double normalized = ((d + 500) / 1000) * max;
        return Math.max(20, Math.min(max - 20, normalized));
    }

    private Color getUserColor(String owner) {
        return userColorCache.computeIfAbsent(owner, k -> {
            int hash = k.hashCode();
            return Color.color(
                    (hash & 0xFF) / 255.0 * 0.5 + 0.3,
                    ((hash >> 8) & 0xFF) / 255.0 * 0.5 + 0.3,
                    ((hash >> 16) & 0xFF) / 255.0 * 0.5 + 0.3
            );
        });
    }

    private void handleCanvasClick(MouseEvent e) {
        if (visualizationCanvas == null || workersData == null) return;
        double clickX = e.getX(), clickY = e.getY();
        double canvasWidth = visualizationCanvas.getWidth(), canvasHeight = visualizationCanvas.getHeight();
        for (Worker w : workersData) {
            if (w == null || w.getCoordinates() == null) continue;
            Coordinates coordinates = w.getCoordinates();
            double workerX = normalizeCoordinate(coordinates.getX(), canvasWidth);
            double workerY = normalizeCoordinate(coordinates.getY(), canvasHeight);
            double size = 30;
            if (Math.hypot(clickX - workerX, clickY - workerY) <= size / 2) {
                showWorkerInfo(w);
                return;
            }
        }
    }

    private void showWorkerInfo(Worker w) {
        if (w == null) return;
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(w.getId()).append("\n");
        info.append("Имя: ").append(w.getName()).append("\n");
        if (w.getCoordinates() != null) info.append("Координаты: ").append(w.getCoordinates()).append("\n");
        info.append("Зарплата: ").append(w.getSalary()).append("\n");
        if (w.getPosition() != null) info.append("Должность: ").append(w.getPosition()).append("\n");
        if (w.getStatus() != null) info.append("Статус: ").append(w.getStatus()).append("\n");
        if (w.getOwner() != null) info.append("Владелец: ").append(w.getOwner()).append("\n");
        showAlert(Alert.AlertType.INFORMATION, "Информация о работнике", info.toString());
    }
}