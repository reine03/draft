package byod.controller;

import byod.dao.DeviceDAO;
import byod.dao.StudentDAO;
import byod.model.Device;
import byod.model.Student;
import byod.util.Validator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class RegisterController {

    // ── Student fields ──────────────────────────────────────────────────────
    @FXML private TextField  fullNameField;
    @FXML private TextField  studentIdField;
    @FXML private ComboBox<String> courseCombo;

    // ── Device fields ───────────────────────────────────────────────────────
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField  brandField;
    @FXML private TextField  modelField;
    @FXML private TextField  serialField;
    @FXML private TextField  colorField;
    @FXML private TextField  macField;

    @FXML private Label      statusLabel;

    private final StudentDAO studentDAO = new StudentDAO();
    private final DeviceDAO  deviceDAO  = new DeviceDAO();

    @FXML
    public void initialize() {
        courseCombo.setItems(FXCollections.observableArrayList(
            "BS Computer Science",
            "BS Information Technology",
            "BS Electronics Engineering",
            "BS Computer Engineering",
            "Other"
        ));
        typeCombo.setItems(FXCollections.observableArrayList(
            "LAPTOP", "TABLET", "SMARTPHONE"
        ));
        courseCombo.getSelectionModel().selectFirst();
        typeCombo.getSelectionModel().selectFirst();
        statusLabel.setText("");
    }

    @FXML
    private void handleSave() {
        String fullName  = fullNameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String course    = courseCombo.getValue();
        String serialNo  = serialField.getText().trim();
        String brand     = brandField.getText().trim();
        String model     = modelField.getText().trim();
        String color     = colorField.getText().trim();
        String mac       = macField.getText().trim();
        String typeStr   = typeCombo.getValue();

        // Validate
        String errors = Validator.validateRegistrationForm(
            studentId, fullName, course, serialNo, brand, model, mac
        );
        if (errors != null) {
            showError(errors);
            return;
        }

        try {
            // Check for duplicate serial number
            if (deviceDAO.findBySerialNo(serialNo) != null) {
                showError("A device with serial number \"" + serialNo + "\" is already registered.");
                return;
            }

            // If student doesn't exist yet, create them
            Student existing = studentDAO.findById(studentId);
            if (existing == null) {
                Student student = new Student(studentId, fullName, course, null);
                studentDAO.addStudent(student);
            }

            // Register the device
            Device device = new Device(
                serialNo, studentId,
                Device.Type.valueOf(typeStr),
                brand, model, color, mac
            );
            deviceDAO.addDevice(device);

            showSuccess("Device registered successfully for " + fullName + ".");
            clearForm();

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        fullNameField.clear();
        studentIdField.clear();
        brandField.clear();
        modelField.clear();
        serialField.clear();
        colorField.clear();
        macField.clear();
        courseCombo.getSelectionModel().selectFirst();
        typeCombo.getSelectionModel().selectFirst();
        statusLabel.setText("");
        fullNameField.requestFocus();
    }

    private void showSuccess(String msg) {
        statusLabel.setText("✓ " + msg);
        statusLabel.setStyle("-fx-text-fill: #1D9E75;");
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #D85A30;");
        showAlert(Alert.AlertType.ERROR, "Validation Error", msg);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
