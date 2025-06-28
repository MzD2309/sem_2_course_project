import java.sql.*;

public class EnrollmentModel {
    public void enrollStudent(int studentId, int electiveId) throws SQLException {
        String sql = "INSERT INTO students_electives (id_student, id_elective) VALUES (?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, electiveId);
            stmt.executeUpdate();
        }
    }
}