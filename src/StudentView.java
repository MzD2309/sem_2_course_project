

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class StudentView {
    private final TabPane tabPane = new TabPane();
    private final TableView<StudentElective> enrolledTable = new TableView<>();
    private final TableView<Elective> availableTable = new TableView<>();
    private final Button enrollButton = new Button("Записаться");

    private final Tab personalDataTab = new Tab("Личные данные");
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField middleNameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField loginField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button saveButton = new Button("Сохранить");



    public StudentView() {
        createEnrolledElectivesTab();
        createAvailableElectivesTab();
        createPersonalDataTab();
    }

    private void createEnrolledElectivesTab() {
        Tab enrolledTab = new Tab("Мои факультативы");
        enrolledTab.setClosable(false);

        // ID факультатива (можно скрыть)
        TableColumn<StudentElective, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setVisible(true); // или false, если хотите скрыть

        // Название факультатива
        TableColumn<StudentElective, String> subjectCol = new TableColumn<>("Предмет");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setPrefWidth(200);

        // Кафедра
        TableColumn<StudentElective, String> departmentCol = new TableColumn<>("Кафедра");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(150);

        // Семестр (теперь Integer)
        TableColumn<StudentElective, Integer> semesterCol = new TableColumn<>("Семестр");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        semesterCol.setPrefWidth(80);

        // Оценка (теперь Double)
        TableColumn<StudentElective, Double> markCol = new TableColumn<>("Оценка");
        markCol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        markCol.setPrefWidth(80);

        enrolledTable.getColumns().addAll(
                idCol, subjectCol, departmentCol, semesterCol, markCol
        );

        VBox vbox = new VBox(enrolledTable);
        vbox.setPadding(new Insets(10));
        enrolledTab.setContent(vbox);
        tabPane.getTabs().add(enrolledTab);
    }





    private void createAvailableElectivesTab() {
        Tab availableTab = new Tab("Доступные факультативы");
        availableTab.setClosable(false);

        TableColumn<Elective, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Elective, String> subjectCol = new TableColumn<>("Предмет");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));

        TableColumn<Elective, String> departmentCol = new TableColumn<>("Кафедра");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<Elective, Integer> semesterCol = new TableColumn<>("Семестр");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        availableTable.getColumns().addAll(idCol, subjectCol, departmentCol, semesterCol);

        HBox buttonPanel = new HBox(10, enrollButton);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10));

        VBox vbox = new VBox(10, availableTable, buttonPanel);
        vbox.setPadding(new Insets(10));
        availableTab.setContent(vbox);
        tabPane.getTabs().add(availableTab);
    }

    private void createPersonalDataTab() {
        personalDataTab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(new Label("Имя:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Фамилия:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Телефон:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Логин:"), 0, 4);
        grid.add(loginField, 1, 4);
        grid.add(new Label("Пароль:"), 0, 5);
        grid.add(passwordField, 1, 5);

        HBox buttonBox = new HBox(10, saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 6, 2, 1);

        personalDataTab.setContent(grid);
        tabPane.getTabs().add(personalDataTab);
    }

    // Геттеры для полей
    public TextField getFirstNameField() { return firstNameField; }
    public TextField getLastNameField() { return lastNameField; }
    public TextField getMiddleNameField() { return middleNameField; }
    public TextField getPhoneField() { return phoneField; }
    public TextField getLoginField() { return loginField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getSaveButton() { return saveButton; }

    // Методы для установки данных
    public void setStudentData(StudentData data) {
        firstNameField.setText(data.getFirstName());
        lastNameField.setText(data.getLastName());
        middleNameField.setText(data.getMiddleName());
        phoneField.setText(data.getPhone());
        loginField.setText(data.getLogin());
        passwordField.setText(data.getPassword());
    }

    // Метод для получения данных из формы
    public StudentData getStudentData() {
        return new StudentData(
                0, // ID будет установлен контроллером
                firstNameField.getText(),
                lastNameField.getText(),
                middleNameField.getText(),
                phoneField.getText(),
                loginField.getText(),
                passwordField.getText()
        );
    }


    public TabPane getView() { return tabPane; }
    public TableView<StudentElective> getEnrolledTable() { return enrolledTable; }
    public TableView<Elective> getAvailableTable() { return availableTable; }
    public Button getEnrollButton() { return enrollButton; }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}