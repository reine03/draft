package byod.controller;

import byod.dao.DeviceDAO;
import byod.dao.DeviceLogDAO;
import byod.dao.StudentDAO;
import byod.model.DeviceLog;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardController {

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalDevicesLabel;
    @FXML private Label onCampusLabel;
    @FXML private Label todayEntriesLabel;
    @FXML private Label todayExitsLabel;

    @FXML private TableView<DeviceLog>        logTable;
    @FXML private TableColumn<DeviceLog, String> colTime;
    @FXML private TableColumn<DeviceLog, String> colStudent;
    @FXML private TableColumn<DeviceLog, String> colSerial;
    @FXML private TableColumn<DeviceLog, String> colAction;
    @FXML private TableColumn<DeviceLog, String> colGuard;

    @FXML private TextField searchField;

    private final StudentDAO   studentDAO   = new StudentDAO();
    private final DeviceDAO    deviceDAO    = new DeviceDAO();
    private final DeviceLogDAO deviceLogDAO = new DeviceLogDAO();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
    private ScheduledExecutorService scheduler;

    @FXML
    public void initialize() {
        setupTable();
        loadStats();
        loadLogs();
        startAutoRefresh();
    }

    private void setupTable() {
        colTime.setCellValueFactory(cell -> {
            DeviceLog log = cell.getValue();
            String ts = log.getTimestamp() != null ? log.getTimestamp().format(dtf) : "";
            return new javafx.beans.property.SimpleStringProperty(ts);
        });
        colStudent.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getStudentName()));
        colSerial.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getSerialNo()));
        colAction.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getAction().name()));
        colGuard.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getGuardName()));

        // Color rows by action
        logTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DeviceLog item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getAction() == DeviceLog.Action.IN) {
                    setStyle("-fx-background-color: #EAF3DE;");
                } else {
                    setStyle("-fx-background-color: #FCEBEB;");
                }
            }
        });
    }

    private void loadStats() {
        try {
            totalStudentsLabel.setText(String.valueOf(studentDAO.getTotalCount()));
            totalDevicesLabel.setText(String.valueOf(deviceDAO.getTotalCount()));
            onCampusLabel.setText(String.valueOf(deviceDAO.countOnCampus()));
            todayEntriesLabel.setText(String.valueOf(deviceLogDAO.countTodayByAction("IN")));
            todayExitsLabel.setText(String.valueOf(deviceLogDAO.countTodayByAction("OUT")));
        } catch (SQLException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }

    private void loadLogs() {
        try {
            List<DeviceLog> logs = deviceLogDAO.getTodayLogs();
            logTable.setItems(FXCollections.observableArrayList(logs));
        } catch (SQLException e) {
            System.err.println("Error loading logs: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isBlank()) {
            loadLogs();
            return;
        }
        try {
            List<DeviceLog> results = deviceLogDAO.search(keyword);
            logTable.setItems(FXCollections.observableArrayList(results));
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadStats();
        loadLogs();
    }

    /** Auto-refresh stats + logs every 30 seconds. */
    private void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "byod-refresh");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() ->
            Platform.runLater(this::handleRefresh), 30, 30, TimeUnit.SECONDS
        );
    }

    /** Call this when the window closes so the thread shuts down cleanly. */
    public void shutdown() {
        if (scheduler != null) scheduler.shutdownNow();
    }
}
