

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentModel {
    private final int studentId;
    private ObjectFactory objectFactory;
    public StudentModel(int studentId) {
        this.studentId = studentId;
    }

    public static int getStudentId(int userId) throws SQLException {
        String sql = "SELECT id_student FROM users_students WHERE id_user = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_student") : -1;
            }
        }
    }

    public ObservableList<StudentElective> getEnrolledElectives() throws SQLException {
        ObservableList<StudentElective> electives = FXCollections.observableArrayList();

        String sql = "SELECT e.id, e.subject, d.name AS department, " +
                "es.id_semester AS semester, ses.mark " +
                "FROM students_electives_semesters ses " +
                "JOIN electives_semesters es ON ses.id_electives_semesters = es.id " +
                "JOIN electives e ON es.id_elective = e.id " +
                "JOIN departments d ON e.id_departments = d.id " +
                "WHERE ses.id_student = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    electives.add(new StudentElective(
                            rs.getInt("id"),
                            rs.getString("subject"),
                            rs.getString("department"),
                            rs.getInt("semester"),
                            rs.getDouble("mark")
                    ));
                }
            }
        }
        return electives;
    }



    public ObservableList<Elective> getAvailableElectives() throws SQLException {
        ObservableList<Elective> electives = FXCollections.observableArrayList();

        String sql = "SELECT e.id , e.subject AS subject, d.name AS department, es.id_semester AS semester " +
        "FROM students s JOIN student_department sd ON s.id = sd.id_student JOIN departments d ON sd.id_department = d.id " +
        "JOIN electives e ON d.id = e.id_departments JOIN electives_semesters es ON e.id = es.id_elective " +
        "WHERE s.id = ? AND e.id NOT IN (SELECT es.id_elective FROM students_electives_semesters ses " +
                "JOIN electives_semesters es " +
                "ON ses.id_electives_semesters = es.id " +
                "WHERE ses.id_student = ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    electives.add(new Elective(
                            rs.getInt("id"),
                            rs.getString("subject"),
                            rs.getString("department"),
                            rs.getInt("semester")
                    ));
                }
            }
        }
        return electives;
    }

    public void enrollToElective(int electiveId, int semester) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Записываем на факультатив
            String enrollSql = "INSERT INTO students_electives (id_student, id_elective) VALUES (?, ?)";
            try (PreparedStatement enrollStmt = conn.prepareStatement(enrollSql)) {
                enrollStmt.setInt(1, studentId);
                enrollStmt.setInt(2, electiveId);
                enrollStmt.executeUpdate();
            }

            // 2. Записываем в семестр
            String semesterSql = "INSERT INTO students_electives_semesters (id_student, id_electives_semesters) " +
                    "VALUES (?, (SELECT id FROM electives_semesters WHERE id_elective = ? AND id_semester = ?))";
            try (PreparedStatement semesterStmt = conn.prepareStatement(semesterSql)) {
                semesterStmt.setInt(1, studentId);
                semesterStmt.setInt(2, electiveId);
                semesterStmt.setInt(3, semester);
                semesterStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public StudentData loadStudentData() throws SQLException {
        String sql = "SELECT s.id, u.login, u.password, s.first_name, s.last_name, s.second_name, s.phone_number " +
                "FROM users u " +
                "JOIN users_students us ON u.id = us.id_user " +
                "JOIN students s ON us.id_student = s.id " +
                "WHERE us.id_student = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentData(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("second_name"),
                            rs.getString("phone_number"),
                            rs.getString("login"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public void updateStudentData(StudentData data) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Обновляем данные пользователя
            String userSql = "UPDATE users SET login = ?, password = ? WHERE id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, data.getLogin());
                userStmt.setString(2, data.getPassword());
                userStmt.setInt(3, data.getUserId());
                userStmt.executeUpdate();
            }

            // Обновляем данные студента
            String studentSql = "UPDATE students SET first_name = ?, last_name = ?, second_name = ?, phone_number = ? WHERE id = ?";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                studentStmt.setString(1, data.getFirstName());
                studentStmt.setString(2, data.getLastName());
                studentStmt.setString(3, data.getMiddleName());
                studentStmt.setString(4, data.getPhone());
                studentStmt.setInt(5, data.getUserId());
                studentStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }
}