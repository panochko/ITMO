package gui;

import SEclasses.Coordinates;
import SEclasses.Person;
import SEclasses.Position;
import SEclasses.Status;
import SEclasses.Worker;
import client.ClientNetwork;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class WorkerFormController {

    @FXML private TextField nameField;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Position> positionCombo;
    @FXML private ComboBox<Status> statusCombo;
    @FXML private TextField personWeightField;

    private Stage stage;
    private Worker existingWorker;
    private Runnable onSaveCallback;

    @FXML public void initialize() {
        if (positionCombo != null) positionCombo.getItems().addAll(Position.values());
        if (statusCombo != null) statusCombo.getItems().addAll(Status.values());
    }

    public void setStage(Stage stage) { this.stage = stage; }

    public void setWorker(Worker worker, String owner, String password) {
        this.existingWorker = worker;
        if (worker != null) fillFormData(worker);
    }

    public void setOnSaveCallback(Runnable callback) { this.onSaveCallback = callback; }

    private void fillFormData(Worker worker) {
        if (nameField != null && worker.getName() != null) nameField.setText(worker.getName());
        if (worker.getCoordinates() != null) {
            if (xField != null) xField.setText(String.valueOf(worker.getCoordinates().getX()));
            if (yField != null) yField.setText(String.valueOf(worker.getCoordinates().getY()));
        }
        if (salaryField != null) salaryField.setText(String.valueOf(worker.getSalary()));
        if (positionCombo != null && worker.getPosition() != null) positionCombo.setValue(worker.getPosition());
        if (statusCombo != null && worker.getStatus() != null) statusCombo.setValue(worker.getStatus());
        if (worker.getPerson() != null && personWeightField != null) {
            personWeightField.setText(String.valueOf(worker.getPerson().getWeight()));
        }
    }

    @FXML private void handleSave() {
        try {
            Worker worker = createWorkerFromForm();
            if (existingWorker != null) {
                worker.DBSetId(existingWorker.getId());
                worker.setCreationDate(existingWorker.getCreationDate());
            } else {
                worker.setCreationDate(LocalDateTime.now());
            }
            String response = (existingWorker != null)
                    ? ClientNetwork.updateWorker(worker.getId(), worker)
                    : ClientNetwork.addWorker(worker);
            showInfo(response);
            if (onSaveCallback != null) onSaveCallback.run();
            if (stage != null) stage.close();
        } catch (NumberFormatException e) {
            showError("Проверьте числовые поля");
        } catch (Exception e) {
            showError("Ошибка сохранения");
        }
    }

    private Worker createWorkerFromForm() {
        Worker worker = new Worker();
        if (nameField != null) worker.setName(nameField.getText().trim());
        if (xField != null && yField != null) {
            Integer x = (xField.getText().trim().isEmpty()) ? null : Integer.parseInt(xField.getText().trim());
            float y = (yField.getText().trim().isEmpty()) ? 0 : Float.parseFloat(yField.getText().trim());
            worker.setCoordinates(new Coordinates(x, y));
        }
        if (salaryField != null && !salaryField.getText().trim().isEmpty()) {
            worker.setSalary(Integer.parseInt(salaryField.getText().trim()));
        }
        if (positionCombo != null) worker.setPosition(positionCombo.getValue());
        if (statusCombo != null) worker.setStatus(statusCombo.getValue());
        if (personWeightField != null && !personWeightField.getText().trim().isEmpty()) {
            double weight = Double.parseDouble(personWeightField.getText().trim());
            worker.setPerson(new Person(weight, null));
        }
        return worker;
    }

    @FXML private void handleCancel() {
        if (stage != null) stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setContentText(message);
        alert.showAndWait();
    }
}