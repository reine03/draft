package byod.dao;

import byod.model.DeviceLog;
import byod.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceLogDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance();
    }

    /** Log a device IN or OUT event. */
    public void logEvent(DeviceLog log) throws SQLException {
        String sql = "INSERT INTO device_logs (device_id, student_id, action, logged_by, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, log.getDeviceId());
            ps.setString(2, log.getStudentId());
            ps.setString(3, log.getAction().name());
            ps.setInt(4, log.getLoggedBy());
            ps.setString(5, log.getNotes());
            ps.executeUpdate();
        }
    }

    /** Fetch all logs today, joined with student and device info, newest first. */
    public List<DeviceLog> getTodayLogs() throws SQLException {
        String sql = """
            SELECT dl.*, s.full_name AS student_name, d.serial_no,
                   u.full_name AS guard_name
            FROM device_logs dl
            JOIN students s ON dl.student_id = s.student_id
            JOIN devices  d ON dl.device_id  = d.id
            LEFT JOIN users u ON dl.logged_by = u.id
            WHERE DATE(dl.timestamp) = CURDATE()
            ORDER BY dl.timestamp DESC
        """;
        return queryLogs(sql);
    }

    /** Fetch all logs (with join), newest first. */
    public List<DeviceLog> getAllLogs() throws SQLException {
        String sql = """
            SELECT dl.*, s.full_name AS student_name, d.serial_no,
                   u.full_name AS guard_name
            FROM device_logs dl
            JOIN students s ON dl.student_id = s.student_id
            JOIN devices  d ON dl.device_id  = d.id
            LEFT JOIN users u ON dl.logged_by = u.id
            ORDER BY dl.timestamp DESC
            LIMIT 500
        """;
        return queryLogs(sql);
    }

    /** Search logs by student name, student ID, or serial number. */
    public List<DeviceLog> search(String keyword) throws SQLException {
        String sql = """
            SELECT dl.*, s.full_name AS student_name, d.serial_no,
                   u.full_name AS guard_name
            FROM device_logs dl
            JOIN students s ON dl.student_id = s.student_id
            JOIN devices  d ON dl.device_id  = d.id
            LEFT JOIN users u ON dl.logged_by = u.id
            WHERE s.full_name LIKE ? OR s.student_id LIKE ? OR d.serial_no LIKE ?
            ORDER BY dl.timestamp DESC
        """;
        List<DeviceLog> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapJoined(rs));
            }
        }
        return list;
    }

    /** Count entries or exits today. action = "IN" or "OUT". */
    public int countTodayByAction(String action) throws SQLException {
        String sql = "SELECT COUNT(*) FROM device_logs WHERE action = ? AND DATE(timestamp) = CURDATE()";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, action);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Count this month's total log events. */
    public int countThisMonth() throws SQLException {
        String sql = "SELECT COUNT(*) FROM device_logs WHERE MONTH(timestamp) = MONTH(NOW()) AND YEAR(timestamp) = YEAR(NOW())";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<DeviceLog> queryLogs(String sql) throws SQLException {
        List<DeviceLog> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapJoined(rs));
        }
        return list;
    }

    private DeviceLog mapJoined(ResultSet rs) throws SQLException {
        DeviceLog log = new DeviceLog();
        log.setId(rs.getInt("id"));
        log.setDeviceId(rs.getInt("device_id"));
        log.setStudentId(rs.getString("student_id"));
        log.setAction(DeviceLog.Action.valueOf(rs.getString("action")));
        log.setLoggedBy(rs.getInt("logged_by"));
        log.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null) log.setTimestamp(ts.toLocalDateTime());
        log.setStudentName(rs.getString("student_name"));
        log.setSerialNo(rs.getString("serial_no"));
        log.setGuardName(rs.getString("guard_name"));
        return log;
    }
}
