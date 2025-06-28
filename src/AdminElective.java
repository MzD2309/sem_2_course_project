public class AdminElective {
    private final int id;
    private final String subject;
    private final String department;

    public AdminElective(int id, String subject, String department) {
        this.id = id;
        this.subject = subject;
        this.department = department;
    }

    public int getId() { return id; }
    public String getSubject() { return subject; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return subject + " (" + department + ")";
    }
}