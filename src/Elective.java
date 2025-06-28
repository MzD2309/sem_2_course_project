public class Elective {
    private final int id;
    private final String subject;
    private final String department;
    private final int semester;

    public Elective(int id, String subject, String department, int semester) {
        this.id = id;
        this.subject = subject;
        this.department = department;
        this.semester = semester;
    }

    public int getId() { return id; }
    public String getSubject() { return subject; }
    public String getDepartment() { return department; }
    public int getSemester(){return semester;}

    @Override
    public String toString() {
        return subject + " (" + department + ")";
    }
}