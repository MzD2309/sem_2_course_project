public class Student {
    private final int id;
    private final String lastName;
    private final String firstName;
    private final String secondName;
    private final int semester;

    public Student(int id, String lastName, String firstName, String secondName, int semester) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.semester = semester;
    }

    public int getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getSecondName() { return secondName; }
    public String getFullName() {
        return lastName + " " + firstName + " " + secondName;
    }
    public int getSemester(){return semester;}
}