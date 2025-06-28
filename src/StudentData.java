public class StudentData {
    private int userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String login;
    private String password;

    public StudentData(int userId, String firstName, String lastName, String middleName,
                       String phone, String login, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.login = login;
        this.password = password;
    }

    // Геттеры и сеттеры
    public int getUserId() { return userId; }
    public void setUserId(int userId){this.userId = this.userId;}
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}