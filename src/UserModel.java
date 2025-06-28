import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserModel {




    public ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT id, login, role FROM users";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("role")
                ));
            }
        }
        return users;
    }





    public void addUser(String login, String password, String role,
                        String lastName, String firstName, String middleName,
                        String phone, String address, Integer semester) throws SQLException {
        Connection conn = null;
        boolean originalAutoCommit = true;
        boolean transactionStarted = false;

        try {
            conn = DataBaseConnection.getConnection();
            // Сохраняем исходное состояние autocommit
            originalAutoCommit = conn.getAutoCommit();

            // Начинаем транзакцию только если нужно
            if (originalAutoCommit) {
                conn.setAutoCommit(false);
                transactionStarted = true;
            }

            // 1. Добавляем пользователя
            String userSql = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, login);
                userStmt.setString(2, password);
                userStmt.setString(3, role);
                userStmt.executeUpdate();

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            // 2. Создаем связанную запись
            if ("student".equals(role)) {
                addStudent(userId, lastName, firstName, middleName, phone, address, semester, conn);
            } else if ("teacher".equals(role)) {
                addTeacher(userId, lastName, firstName, middleName, phone, conn);
            }

            // Фиксируем транзакцию если мы ее начали
            if (transactionStarted) {
                conn.commit();
            }
        } catch (SQLException e) {
            // Откатываем только если мы начали транзакцию
            if (conn != null && transactionStarted) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    // Восстанавливаем исходное состояние autocommit
                    if (transactionStarted) {
                        conn.setAutoCommit(originalAutoCommit);
                    }
                } finally {
                    conn.close();
                }
            }
        }
    }

    private void addStudent(int userId, String lastName, String firstName, String middleName,
                            String phone, String address, int semester, Connection conn) throws SQLException {
        // 1. Создаем запись студента
        String studentSql = "INSERT INTO students (last_name, first_name, second_name, phone_number, adress) " +
                "VALUES (?, ?, ?, ?, ?)";
        int studentId;
        try (PreparedStatement studentStmt = conn.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS)) {
            studentStmt.setString(1, lastName);
            studentStmt.setString(2, firstName);
            studentStmt.setString(3, middleName);
            studentStmt.setString(4, phone);
            studentStmt.setString(5, address);
            studentStmt.executeUpdate();

            try (ResultSet generatedKeys = studentStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    studentId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
        }

        // 2. Связываем пользователя и студента
        String linkSql = "INSERT INTO users_students (id_user, id_student) VALUES (?, ?)";
        try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
            linkStmt.setInt(1, userId);
            linkStmt.setInt(2, studentId);
            linkStmt.executeUpdate();
        }
    }

    private void addTeacher(int userId, String lastName, String firstName, String middleName,
                            String phone, Connection conn) throws SQLException {
        // 1. Создаем запись преподавателя
        String teacherSql = "INSERT INTO teachers (last_name, first_name, middle_name, phone_number) " +
                "VALUES (?, ?, ?, ?)";
        int teacherId;
        try (PreparedStatement teacherStmt = conn.prepareStatement(teacherSql, Statement.RETURN_GENERATED_KEYS)) {
            teacherStmt.setString(1, lastName);
            teacherStmt.setString(2, firstName);
            teacherStmt.setString(3, middleName);
            teacherStmt.setString(4, phone);
            teacherStmt.executeUpdate();

            try (ResultSet generatedKeys = teacherStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    teacherId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating teacher failed, no ID obtained.");
                }
            }
        }

        // 2. Связываем пользователя и преподавателя
        String linkSql = "INSERT INTO users_teachers (id_user, id_teacher) VALUES (?, ?)";
        try (PreparedStatement linkStmt = conn.prepareStatement(linkSql)) {
            linkStmt.setInt(1, userId);
            linkStmt.setInt(2, teacherId);
            linkStmt.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // Начинаем транзакцию

            // 1. Получаем информацию о пользователе
            String role = getUserRole(id, conn);

            // 2. Удаляем связанные записи
            if ("student".equals(role)) {
                deleteStudentLinks(id, conn);
            } else if ("teacher".equals(role)) {
                deleteTeacherLinks(id, conn);
            }

            // 3. Удаляем самого пользователя
            String userSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setInt(1, id);
                userStmt.executeUpdate();
            }

            conn.commit(); // Фиксируем транзакцию
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Откатываем при ошибке
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Восстанавливаем авто-коммит
                conn.close();
            }
        }
    }

    private String getUserRole(int userId, Connection conn) throws SQLException {
        String sql = "SELECT role FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        }
        return null;
    }

    private void deleteStudentLinks(int userId, Connection conn) throws SQLException {
        // 1. Получаем ID студента
        String selectSql = "SELECT id_student FROM users_students WHERE id_user = ?";
        int studentId;
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    studentId = rs.getInt("id_student");
                } else {
                    return; // Связь не найдена
                }
            }
        }

        // 2. Удаляем связи студента
        String deleteLinksSql = "DELETE FROM users_students WHERE id_student = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteLinksSql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        }

        // 3. Удаляем самого студента
        String deleteStudentSql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteStudentSql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        }
    }

    private void deleteTeacherLinks(int userId, Connection conn) throws SQLException {
        // 1. Получаем ID преподавателя
        String selectSql = "SELECT id_teacher FROM users_teachers WHERE id_user = ?";
        int teacherId;
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    teacherId = rs.getInt("id_teacher");
                } else {
                    return; // Связь не найдена
                }
            }
        }

        // 2. Удаляем связи преподавателя
        // Удаляем связь с кафедрами
        String deleteDeptSql = "DELETE FROM teachers_departments WHERE id_teacher = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteDeptSql)) {
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
        }

        // Удаляем связь пользователь-преподаватель
        String deleteLinkSql = "DELETE FROM users_teachers WHERE id_teacher = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteLinkSql)) {
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
        }

        // 3. Удаляем самого преподавателя
        String deleteTeacherSql = "DELETE FROM teachers WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteTeacherSql)) {
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
        }
    }

    public int getStudentIdByUserId(int userId) throws SQLException {
        String sql = "SELECT id_student FROM users_students WHERE id_user = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_student");
                }
            }
        }
        throw new SQLException("Student not found for user ID: " + userId);
    }
}