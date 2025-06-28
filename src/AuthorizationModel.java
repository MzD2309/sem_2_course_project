import java.sql.*;

public class AuthorizationModel {


    // Метод, срабатывающий после нажатия кнопки войти, проверяет введенные в поля пользователем данные
    public User authenticate(String login, String password) throws SQLException {
        String sql = "SELECT id, role, login FROM users WHERE login=? AND password=?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }


    // Метод, обрабатывающий регистрацию проверяет логин и пароль, роль, дальше смотрит какая роль и вызывает метод AddStudent или AddTeacher
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


    // Регистрирует студента в базу данных
    private void addStudent(int userId, String lastName, String firstName, String middleName,
                            String phone, String address, int semester, Connection conn) throws SQLException {
        // 1. Создаем запись студента
        String studentSql = "INSERT INTO students (last_name, first_name, second_name, phone_number, adress, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        int studentId;
        try (PreparedStatement studentStmt = conn.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS)) {
            studentStmt.setString(1, lastName);
            studentStmt.setString(2, firstName);
            studentStmt.setString(3, middleName);
            studentStmt.setString(4, phone);
            studentStmt.setString(5, address);
            studentStmt.setInt(6, semester);
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


    // Регистрирует учителя в базу данных
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

    public int getTeacherId(int userId) throws SQLException {
        String sql = "SELECT id_teacher FROM users_teachers WHERE id_user = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_teacher") : -1;
            }
        }
    }

}