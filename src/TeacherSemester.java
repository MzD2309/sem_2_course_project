

public class TeacherSemester {
    private final int id;
    private final int semester;
    private double mark;

    public TeacherSemester(int id, int semester, double mark) {
        this.id = id;
        this.semester = semester;
        this.mark = mark;
    }

    public int getId() { return id; }
    public int getSemester() { return semester; }
    public double getMark() { return mark; }
    public void setMark(double mark) { this.mark = mark; }
}