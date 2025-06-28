

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TeacherModel {
    private final int teacherId;
    private ObjectFactory objectFactory;

    public TeacherModel(int teacherId) throws SQLException {
        this.teacherId = teacherId;
    }



    // Получаем факультативы кафедры преподавателя
    public ObservableList<AdminElective> getDepartmentElectives() throws SQLException {
        ObservableList<AdminElective> electives = FXCollections.observableArrayList();
        String sql = "SELECT e.id, e.subject, d.name AS department FROM electives e JOIN departments d ON e.id_departments = d.id " +
                "WHERE e.id_departments IN (SELECT id_department FROM teachers_departments WHERE id_teacher = ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    electives.add(new AdminElective(
                            rs.getInt("id"),
                            rs.getString("subject"),
                            rs.getString("department")
                    ));
                }
            }
        }
        return electives;
    }

    // Получаем студентов на факультативе
    public ObservableList<Student> getStudentsForElective(int electiveId) throws SQLException {
        ObservableList<Student> students = FXCollections.observableArrayList();
        String sql = "SELECT s.id, s.last_name, s.first_name, s.second_name, s.id_semester AS semester " +
                "FROM students_electives_semesters ses " +
                "JOIN students s ON ses.id_student = s.id " +
                "JOIN electives_semesters es ON es.id = ses.id_electives_semesters " +
                "WHERE es.id_elective = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, electiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                            rs.getInt("id"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getInt("semester")
                    ));
                }
            }
        }
        return students;
    }

    // Получаем семестры факультатива с оценками
    public ObservableList<TeacherSemester> getSemestersWithMarks(int electiveId, int studentId) throws SQLException {
        ObservableList<TeacherSemester> semesters = FXCollections.observableArrayList();
        String sql = "SELECT es.id, es.id_semester, ses.mark " +
                "FROM electives_semesters es " +
                "JOIN students_electives_semesters ses " +
                "ON es.id = ses.id_electives_semesters " +
                "AND ses.id_student = ? " +
                "WHERE es.id_elective = ? " +
                "ORDER BY es.id_semester";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, electiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    semesters.add(new TeacherSemester(
                            rs.getInt("id"),
                            rs.getInt("id_semester"),
                            rs.getDouble("mark")
                    ));
                }
            }
        }
        return semesters;
    }

    // Обновляем оценку за семестр
    public void updateSemesterMark(int semesterId, int studentId, double mark) throws SQLException {
        String sql = "INSERT INTO students_electives_semesters " +
                "(id_student, id_electives_semesters, mark) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE mark = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, semesterId);
            stmt.setDouble(3, mark);
            stmt.setDouble(4, mark);
            stmt.executeUpdate();
        }
    }

    public TeacherData loadTeacherData() throws SQLException {
        String sql = "SELECT u.id AS user_id, u.login, u.password, " +
                "t.last_name, t.first_name, t.middle_name, t.phone_number " +
                "FROM teachers t " +
                "JOIN users_teachers ut ON t.id = ut.id_teacher " +
                "JOIN users u ON ut.id_user = u.id " +
                "WHERE t.id = ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TeacherData(
                            rs.getInt("user_id"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getString("middle_name"),
                            rs.getString("phone_number"),
                            rs.getString("login"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public void updateTeacherData(TeacherData data) throws SQLException {
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

            // Обновляем данные преподавателя
            String teacherSql = "UPDATE teachers SET last_name = ?, first_name = ?, middle_name = ?, phone_number = ? WHERE id = ?";
            try (PreparedStatement teacherStmt = conn.prepareStatement(teacherSql)) {
                teacherStmt.setString(1, data.getLastName());
                teacherStmt.setString(2, data.getFirstName());
                teacherStmt.setString(3, data.getMiddleName());
                teacherStmt.setString(4, data.getPhone());
                teacherStmt.setInt(5, teacherId);
                teacherStmt.executeUpdate();
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