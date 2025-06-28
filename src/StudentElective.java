public class StudentElective {
    private final int id;
    private final String subject;
    private final String department;
    private final int semester;  // изменено на int
    private final double mark;   // изменено на double

    // Изменен порядок параметров: semester и mark поменяны местами
    public StudentElective(int id, String subject, String department,
                           int semester, double mark) {
        this.id = id;
        this.subject = subject;
        this.department = department;
        this.semester = semester;
        this.mark = mark;
    }

    public int getId() { return id; }
    public String getSubject() { return subject; }
    public String getDepartment() { return department; }
    public int getSemester() { return semester; }  // возвращает int
    public double getMark() { return mark; }       // возвращает double
}