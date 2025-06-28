import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectFactory {
    private static ObjectFactory instance;

    private ObjectFactory() {}


    // 1. StudentElective
    // 2. Elective
    // 3. Student
    // 4. AdminElective
    // 5. TeacherSemester
    // 6. User

    public StudentElective createStudentElective(ResultSet rs) throws SQLException {
        return new StudentElective(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getString("department"),
                rs.getInt("semester"),
                rs.getDouble("mark")
        );
    }

    public Elective createElective(ResultSet rs) throws SQLException {
        return new Elective(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getString("department"),
                rs.getInt("semester")
        );
    }

    public Student createStudent(ResultSet rs) throws SQLException{
        return new Student(
                rs.getInt("id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("second_name"),
                rs.getInt("semester")
        );
    }

    public TeacherSemester createTeacherSemester(ResultSet rs) throws SQLException{
        return new TeacherSemester(
                rs.getInt("id"),
                rs.getInt("id_semester"),
                rs.getDouble("mark")
        );
    }

    public AdminElective createAdminElective(ResultSet rs) throws SQLException{
        return new AdminElective(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getString("department")
        );
    }

    public User createUser(ResultSet rs) throws SQLException{
        return new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("role")
        );
    }

    public StudentData createStudentData(ResultSet rs) throws SQLException{
        return new StudentData(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("middle_name"),
                rs.getString("phone_number"),
                rs.getString("login"),
                rs.getString("password")
        );
    }
}