package byod.controller;

import byod.dao.DeviceDAO;
import byod.dao.DeviceLogDAO;
import byod.dao.StudentDAO;
import byod.model.Device;
import byod.model.DeviceLog;
import byod.model.Student;
import byod.session.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

/**
 * Handles the Guard screen where devices are logged IN or OUT.
 * Guard scans / types the serial number or student ID to log an event.
 */
public class IngressEgressController {

    @FXML private TextField  serialOrIdField;
    @FXML private Label      studentNameLabel;
    @FXML private Label      deviceInfoLabel;
    @FXML private Label      lastStatusLabel;
    @FXML private TextArea   notesField;
    @FXML private Button     btnIn;
    @FXML private Button     btnOut;
    @FXML private Label      feedbackLabel;

    private final DeviceDAO    deviceDAO    = new DeviceDAO();
    private final DeviceLogDAO deviceLogDAO = new DeviceLogDAO();
    private final StudentDAO   studentDAO   = new StudentDAO();

    private Device  currentDevice;
    private Student currentStudent;

    @FXML
    public void initialize() {
        clearState();
    }

    @FXML
    private void handleLookup() {
        String input = serialOrIdField.getText().trim();
        if (input.isBlank()) return;

        try {
            // Try to find device by serial number first
            currentDevice = deviceDAO.findBySerialNo(input);

            if (currentDevice == null) {
                // Try treating input as student ID — find first device
                currentStudent = studentDAO.findById(input);
                if (currentStudent != null) {
                    var devices = deviceDAO.getDevicesByStudent(input);
                    if (!devices.isEmpty()) currentDevice = devices.get(0);
                }
            } else {
                currentStudent = studentDAO.findById(currentDevice.getStudentId());
            }

            if (currentDevice == null) {
                feedbackLabel.setText("No device or student found for: " + input);
                feedbackLabel.setStyle("-fx-text-fill: #D85A30;");
                clearState();
                return;
            }

            studentNameLabel.setText(currentStudent != null ? currentStudent.getFullName() : "Unknown");
            deviceInfoLabel.setText(currentDevice.getBrand() + " " + currentDevice.getModel()
                + " · " + currentDevice.getSerialNo());

            btnIn.setDisable(false);
            btnOut.setDisable(false);
            feedbackLabel.setText("");

        } catch (SQLException e) {
            feedbackLabel.setText("Database error: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #D85A30;");
        }
    }

    @FXML
    private void handleIn() {
        logEvent(DeviceLog.Action.IN);
    }

    @FXML
    private void handleOut() {
        logEvent(DeviceLog.Action.OUT);
    }

    private void logEvent(DeviceLog.Action action) {
        if (currentDevice == null) return;

        try {
            DeviceLog log = new DeviceLog(
                currentDevice.getId(),
                currentDevice.getStudentId(),
                action,
                SessionManager.getCurrentUserId(),
                notesField.getText().trim()
            );
            deviceLogDAO.logEvent(log);

            String msg = (action == DeviceLog.Action.IN ? "✓ Logged IN" : "✓ Logged OUT")
                + " — " + (currentStudent != null ? currentStudent.getFullName() : currentDevice.getSerialNo());
            feedbackLabel.setText(msg);
            feedbackLabel.setStyle("-fx-text-fill: #1D9E75;");
            clearState();
            serialOrIdField.requestFocus();

        } catch (SQLException e) {
            feedbackLabel.setText("Error logging event: " + e.getMessage());
            feedbackLabel.setStyle("-fx-text-fill: #D85A30;");
        }
    }

    private void clearState() {
        currentDevice  = null;
        currentStudent = null;
        studentNameLabel.setText("—");
        deviceInfoLabel.setText("—");
        lastStatusLabel.setText("—");
        serialOrIdField.clear();
        if (notesField != null) notesField.clear();
        if (btnIn  != null) btnIn.setDisable(true);
        if (btnOut != null) btnOut.setDisable(true);
    }
}
