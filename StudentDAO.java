package byod.dao;

import byod.model.Student;
import byod.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance();
    }

    /** Insert a new student. Throws if student_id already exists. */
    public void addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (student_id, full_name, course, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getEmail());
            ps.executeUpdate();
        }
    }

    /** Fetch all students. */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Search by name or student ID (partial match). */
    public List<Student> search(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? OR student_id LIKE ? ORDER BY full_name";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Find by exact student_id. Returns null if not found. */
    public Student findById(String studentId) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Update student details. */
    public void updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET full_name=?, course=?, email=? WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getCourse());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getStudentId());
            ps.executeUpdate();
        }
    }

    /** Delete student (cascade deletes devices + logs must be handled via FK). */
    public void deleteStudent(String studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
        }
    }

    /** Count total registered students. */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setCourse(rs.getString("course"));
        s.setEmail(rs.getString("email"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }
}
