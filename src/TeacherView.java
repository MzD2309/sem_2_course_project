import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class TeacherView {
    private final BorderPane root = new BorderPane();
    private final TableView<AdminElective> electivesTable = new TableView<>();
    private final TableView<Student> studentsTable = new TableView<>();
    private final TableView<TeacherSemester> semestersTable = new TableView<>();
    private final Button updateMarksButton = new Button("Обновить оценки");
    private final Button showMarksButton = new Button("Показать оценки");
    private final TabPane tabPane = new TabPane();
    private final Tab mainTab = new Tab("Основное");
    private final Tab personalDataTab = new Tab("Личные данные");

    private final TextField lastNameField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField middleNameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField loginField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button saveButton = new Button("Сохранить");

    public TeacherView() {
        createView(); // Инициализация основного интерфейса
        createMainTab(); // Создание вкладки "Основное"
        createPersonalDataTab(); // Создание вкладки "Личные данные"
        tabPane.getTabs().addAll(mainTab, personalDataTab);
    }

    private void createMainTab() {
        mainTab.setClosable(false);
        mainTab.setContent(root); // Используем root как содержимое основной вкладки
    }

    private void createPersonalDataTab() {
        personalDataTab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
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
    }

    // Геттеры для полей
    public TextField getLastNameField() { return lastNameField; }
    public TextField getFirstNameField() { return firstNameField; }
    public TextField getMiddleNameField() { return middleNameField; }
    public TextField getPhoneField() { return phoneField; }
    public TextField getLoginField() { return loginField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getSaveButton() { return saveButton; }

    // Методы для установки данных
    public void setTeacherData(TeacherData data) {
        lastNameField.setText(data.getLastName());
        firstNameField.setText(data.getFirstName());
        middleNameField.setText(data.getMiddleName());
        phoneField.setText(data.getPhone());
        loginField.setText(data.getLogin());
        passwordField.setText(data.getPassword());
    }

    // Метод для получения данных из формы
    public TeacherData getTeacherData() {
        return new TeacherData(
                0, // ID будет установлен контроллером
                lastNameField.getText(),
                firstNameField.getText(),
                middleNameField.getText(),
                phoneField.getText(),
                loginField.getText(),
                passwordField.getText()
        );
    }

    private void createView() {
        // Таблица факультативов
        TableColumn<AdminElective, Integer> electiveIdCol = new TableColumn<>("ID");
        electiveIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AdminElective, String> electiveSubjectCol = new TableColumn<>("Факультатив");
        electiveSubjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        electiveSubjectCol.setPrefWidth(200);

        // Добавляем колонку для кафедры
        TableColumn<AdminElective, String> departmentCol = new TableColumn<>("Кафедра");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(150);

        electivesTable.getColumns().addAll(electiveIdCol, electiveSubjectCol, departmentCol);

        // Таблица студентов
        TableColumn<Student, Integer> studentIdCol = new TableColumn<>("ID");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Student, String> lastNameCol = new TableColumn<>("Фамилия");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Student, String> firstNameCol = new TableColumn<>("Имя");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Student, String> middleNameCol = new TableColumn<>("Отчество");
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("secondName"));

        TableColumn<Student, Integer> semesterCol = new TableColumn<>("Семестр");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        studentsTable.getColumns().addAll(studentIdCol, lastNameCol, firstNameCol, middleNameCol, semesterCol);

        // Таблица семестров с оценками
        TableColumn<TeacherSemester, Integer> semesterNumCol = new TableColumn<>("Семестр");
        semesterNumCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<TeacherSemester, Double> markCol = new TableColumn<>("Оценка");
        markCol.setCellValueFactory(new PropertyValueFactory<>("mark"));
        markCol.setCellFactory(column -> new EditingCell());

        semestersTable.getColumns().addAll(semesterNumCol, markCol);
        semestersTable.setEditable(true);

        // Панели
        HBox buttonsPanel = new HBox(10, showMarksButton, updateMarksButton);
        buttonsPanel.setAlignment(Pos.CENTER);
        buttonsPanel.setPadding(new Insets(10));

        VBox leftPanel = new VBox(10, new Label("Факультативы кафедры:"), electivesTable);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(300);

        VBox centerPanel = new VBox(10,
                new Label("Студенты факультатива:"),
                studentsTable,
                new Label("Оценки по семестрам:"),
                semestersTable,
                buttonsPanel
        );
        centerPanel.setPadding(new Insets(10));

        HBox mainPanel = new HBox(10, leftPanel, centerPanel);
        root.setCenter(mainPanel);
    }



    // Ячейка для редактирования оценок
    private static class EditingCell extends TableCell<TeacherSemester, Double> {
        private final TextField textField = new TextField();

        public EditingCell() {
            textField.setOnAction(event -> commitEdit(Double.parseDouble(textField.getText())));
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitEdit(Double.parseDouble(textField.getText()));
                }
            });
        }

        @Override
        protected void updateItem(Double value, boolean empty) {
            super.updateItem(value, empty);
            if (empty) {
                setGraphic(null);
            } else {
                textField.setText(value.toString());
                setGraphic(textField);
            }
        }

        @Override
        public void commitEdit(Double newValue) {
            super.commitEdit(newValue);
            if (getTableRow() != null && getTableRow().getItem() != null) {
                ((TeacherSemester) getTableRow().getItem()).setMark(newValue);
            }
        }
    }

    // Важно: возвращаем TabPane как основной интерфейс
    public TabPane getTabPane() {
        return tabPane;
    }

    // Геттеры для таблиц и кнопок
    public TableView<AdminElective> getElectivesTable() { return electivesTable; }
    public TableView<Student> getStudentsTable() { return studentsTable; }
    public TableView<TeacherSemester> getSemestersTable() { return semestersTable; }
    public Button getShowMarksButton() { return showMarksButton; }
    public Button getUpdateMarksButton() { return updateMarksButton; }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}