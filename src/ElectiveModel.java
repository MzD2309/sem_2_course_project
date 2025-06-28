
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ElectiveModel {
    private ObjectFactory objectFactory;
    public ObservableList<AdminElective> getAllCourses() throws SQLException {
        ObservableList<AdminElective> courses = FXCollections.observableArrayList();
        String sql = "SELECT c.id, c.subject, d.name as department " +
                "FROM electives c JOIN departments d ON c.id_departments = d.id";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new AdminElective(
                        rs.getInt("id"),
                        rs.getString("subject"),
                        rs.getString("department")
                ));
            }
        }
        return courses;
    }

    public void addCourse(String subject, int departmentId) throws SQLException {
        String sql = "INSERT INTO electives (subject, id_departments) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject);
            stmt.setInt(2, departmentId);
            stmt.executeUpdate();
        }
    }

    public void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM electives WHERE id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public ObservableList<String> getAllDepartments() throws SQLException {
        ObservableList<String> departments = FXCollections.observableArrayList();
        String sql = "SELECT name FROM departments";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(rs.getString("name"));
            }
        }
        return departments;
    }

    public int getDepartmentId(String name) throws SQLException {
        String sql = "SELECT id FROM departments WHERE name = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }
}