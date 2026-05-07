package byod.dao;

import byod.model.Device;
import byod.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance();
    }

    /** Register a new device. */
    public void addDevice(Device d) throws SQLException {
        String sql = "INSERT INTO devices (serial_no, student_id, type, brand, model, color, mac_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, d.getSerialNo());
            ps.setString(2, d.getStudentId());
            ps.setString(3, d.getType().name());
            ps.setString(4, d.getBrand());
            ps.setString(5, d.getModel());
            ps.setString(6, d.getColor());
            ps.setString(7, d.getMacAddress());
            ps.executeUpdate();
        }
    }

    /** All devices registered. */
    public List<Device> getAllDevices() throws SQLException {
        List<Device> list = new ArrayList<>();
        String sql = "SELECT * FROM devices ORDER BY registered_at DESC";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** All devices belonging to one student. */
    public List<Device> getDevicesByStudent(String studentId) throws SQLException {
        List<Device> list = new ArrayList<>();
        String sql = "SELECT * FROM devices WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Find device by serial number. Returns null if not found. */
    public Device findBySerialNo(String serialNo) throws SQLException {
        String sql = "SELECT * FROM devices WHERE serial_no = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, serialNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Count of devices currently inside campus (last log action = IN). */
    public int countOnCampus() throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM (
                SELECT dl.device_id, dl.action
                FROM device_logs dl
                INNER JOIN (
                    SELECT device_id, MAX(timestamp) AS latest
                    FROM device_logs GROUP BY device_id
                ) latest_log ON dl.device_id = latest_log.device_id
                              AND dl.timestamp = latest_log.latest
                WHERE dl.action = 'IN'
            ) AS inside
        """;
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Total device count. */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM devices";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Delete a device by its DB id. */
    public void deleteDevice(int id) throws SQLException {
        String sql = "DELETE FROM devices WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Device map(ResultSet rs) throws SQLException {
        Device d = new Device();
        d.setId(rs.getInt("id"));
        d.setSerialNo(rs.getString("serial_no"));
        d.setStudentId(rs.getString("student_id"));
        d.setType(Device.Type.valueOf(rs.getString("type")));
        d.setBrand(rs.getString("brand"));
        d.setModel(rs.getString("model"));
        d.setColor(rs.getString("color"));
        d.setMacAddress(rs.getString("mac_address"));
        Timestamp ts = rs.getTimestamp("registered_at");
        if (ts != null) d.setRegisteredAt(ts.toLocalDateTime());
        return d;
    }
}
