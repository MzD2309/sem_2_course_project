

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AdminController {
    private final AdminView view;
    private final int userId;
    private final UserModel userModel = new UserModel();
    private final ElectiveModel courseModel = new ElectiveModel();
    private final EnrollmentModel enrollmentModel = new EnrollmentModel();

    public AdminController(AdminView view, int userId) {
        this.view = view;
        this.userId = userId;
        attachEvents();
        loadData();
    }

    private void attachEvents() {
        view.getAddUserButton().setOnAction(new AddUserHandler());
        view.getDeleteUserButton().setOnAction(new DeleteUserHandler());
        view.getAddCourseButton().setOnAction(new AddCourseHandler());
        view.getDeleteCourseButton().setOnAction(new DeleteCourseHandler());
        view.getAddCourseToStudentButton().setOnAction(new AddCourseToStudentHandler());
    }

    // Внутренние классы-обработчики
    private class AddUserHandler implements EventHandler<ActionEvent> {
        // Переносим инициализацию полей на уровень класса

        private final TextField lastNameField = new TextField();
        private final TextField firstNameField = new TextField();
        private final TextField middleNameField = new TextField();
        private final TextField phoneField = new TextField();
        private final TextField addressField = new TextField();
        private final Spinner<Integer> semesterSpinner = new Spinner<>(1, 12, 1);
        private VBox additionalFieldsBox;

        {
            semesterSpinner.setEditable(true);
        }

        @Override
        public void handle(ActionEvent event) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Добавление нового пользователя");

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            // Основные поля
            TextField loginField = new TextField();
            PasswordField passwordField = new PasswordField();
            ComboBox<String> roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll("admin", "teacher", "student");
            roleCombo.setValue("student");

            // Дополнительные поля
            additionalFieldsBox = new VBox(5);
            additionalFieldsBox.setPadding(new Insets(5));
            updateAdditionalFields(roleCombo.getValue()); // Инициализируем поля

            // Обработчик изменения роли
            roleCombo.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> obs, String oldVal, String newVal) {
                            updateAdditionalFields(newVal);
                        }
                    }
            );

            Button addButton = new Button("Добавить");
            // Передаем текущий экземпляр this в обработчик
            addButton.setOnAction(new ExecuteAddUserHandler(
                    loginField, passwordField, roleCombo, dialog, this
            ));

            Button cancelButton = new Button("Отмена");
            cancelButton.setOnAction(new CancelHandler(dialog));

            // Добавление элементов в сетку
            grid.add(new Label("Логин:"), 0, 0);
            grid.add(loginField, 1, 0);
            grid.add(new Label("Пароль:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Роль:"), 0, 2);
            grid.add(roleCombo, 1, 2);
            grid.add(additionalFieldsBox, 0, 3, 2, 1);

            HBox buttonBox = new HBox(10, addButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);
            grid.add(buttonBox, 0, 4, 2, 1);

            dialog.setScene(new Scene(grid, 400, 500));
            dialog.showAndWait();
        }

        private void updateAdditionalFields(String role) {
            additionalFieldsBox.getChildren().clear();

            if ("student".equals(role)) {
                additionalFieldsBox.getChildren().addAll(
                        new Label("Фамилия:"), lastNameField,
                        new Label("Имя:"), firstNameField,
                        new Label("Отчество:"), middleNameField,
                        new Label("Телефон:"), phoneField,
                        new Label("Адрес:"), addressField,
                        new Label("Семестр:"), semesterSpinner
                );
            } else if ("teacher".equals(role)) {
                additionalFieldsBox.getChildren().addAll(
                        new Label("Фамилия:"), lastNameField,
                        new Label("Имя:"), firstNameField,
                        new Label("Отчество:"), middleNameField,
                        new Label("Телефон:"), phoneField
                );
            }
        }
    }

    private class ExecuteAddUserHandler implements EventHandler<ActionEvent> {
        private final TextField loginField;
        private final PasswordField passwordField;
        private final ComboBox<String> roleCombo;
        private final Stage dialog;
        private final AddUserHandler parentHandler; // Ссылка на родительский обработчик

        public ExecuteAddUserHandler(TextField loginField, PasswordField passwordField,
                                     ComboBox<String> roleCombo, Stage dialog,
                                     AddUserHandler parentHandler) {
            this.loginField = loginField;
            this.passwordField = passwordField;
            this.roleCombo = roleCombo;
            this.dialog = dialog;
            this.parentHandler = parentHandler; // Сохраняем ссылку
        }

        @Override
        public void handle(ActionEvent event) {
            String login = loginField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleCombo.getValue();

            if (login.isEmpty() || password.isEmpty()) {
                view.showAlert("Ошибка", "Логин и пароль обязательны!", Alert.AlertType.WARNING);
                return;
            }

            try {
                String lastName = parentHandler.lastNameField.getText().trim();
                String firstName = parentHandler.firstNameField.getText().trim();
                String middleName = parentHandler.middleNameField.getText().trim();
                String phone = parentHandler.phoneField.getText().trim();
                String address = "";
                Integer semester = null;

                if ("student".equals(role)) {
                    address = parentHandler.addressField.getText().trim();
                    semester = parentHandler.semesterSpinner.getValue();

                    if (lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() ||
                            phone.isEmpty() || address.isEmpty()) {
                        view.showAlert("Ошибка", "Заполните все поля для студента", Alert.AlertType.WARNING);
                        return;
                    }
                } else if ("teacher".equals(role)) {
                    if (lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() || phone.isEmpty()) {
                        view.showAlert("Ошибка", "Заполните все поля для преподавателя", Alert.AlertType.WARNING);
                        return;
                    }
                }

                userModel.addUser(
                        login, password, role,
                        lastName, firstName, middleName,
                        phone, address, semester
                );

                dialog.close();
                loadUserData();
            } catch (SQLException e) {
                view.showAlert("Ошибка базы данных", "Ошибка при создании пользователя: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private class DeleteUserHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            User selected = view.getUserTable().getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showAlert("Ошибка", "Выберите пользователя", Alert.AlertType.WARNING);
                return;
            }

            if (selected.getId() == userId) {
                view.showAlert("Ошибка", "Нельзя удалить себя", Alert.AlertType.ERROR);
                return;
            }

            try {
                userModel.deleteUser(selected.getId());
                loadUserData();
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Ошибка удаления: " + e.getMessage(), Alert.AlertType.ERROR);

            }
        }
    }

    private class AddCourseHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Добавление нового факультатива");

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            TextField subjectField = new TextField();
            ComboBox<String> departmentCombo = new ComboBox<>();

            try {
                departmentCombo.getItems().addAll(courseModel.getAllDepartments());
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось загрузить кафедры", Alert.AlertType.ERROR);
            }

            Button addButton = new Button("Добавить");
            addButton.setOnAction(new ExecuteAddCourseHandler(
                    subjectField, departmentCombo, dialog));

            Button cancelButton = new Button("Отмена");
            cancelButton.setOnAction(new CancelHandler(dialog));

            grid.add(new Label("Название предмета:"), 0, 0);
            grid.add(subjectField, 1, 0);
            grid.add(new Label("Кафедра:"), 0, 1);
            grid.add(departmentCombo, 1, 1);

            HBox buttonBox = new HBox(10, addButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);
            grid.add(buttonBox, 0, 2, 2, 1);

            dialog.setScene(new Scene(grid));
            dialog.showAndWait();
        }
    }

    private class ExecuteAddCourseHandler implements EventHandler<ActionEvent> {
        private final TextField subjectField;
        private final ComboBox<String> departmentCombo;
        private final Stage dialog;

        public ExecuteAddCourseHandler(TextField subjectField,
                                       ComboBox<String> departmentCombo,
                                       Stage dialog) {
            this.subjectField = subjectField;
            this.departmentCombo = departmentCombo;
            this.dialog = dialog;
        }

        @Override
        public void handle(ActionEvent event) {
            String subject = subjectField.getText().trim();
            String departmentName = departmentCombo.getValue();

            if (subject.isEmpty() || departmentName == null) {
                view.showAlert("Ошибка", "Заполните все поля", Alert.AlertType.WARNING);
                return;
            }

            try {
                int departmentId = courseModel.getDepartmentId(departmentName);
                if (departmentId == -1) {
                    view.showAlert("Ошибка", "Неверная кафедра", Alert.AlertType.ERROR);
                    return;
                }

                courseModel.addCourse(subject, departmentId);
                dialog.close();
                loadCourseData();
            } catch (SQLException e) {
                view.showAlert("Ошибка базы данных", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private class DeleteCourseHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            AdminElective selected = view.getCourseTable().getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showAlert("Ошибка", "Выберите курс", Alert.AlertType.WARNING);
                return;
            }

            try {
                courseModel.deleteCourse(selected.getId());
                loadCourseData();
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Ошибка удаления", Alert.AlertType.ERROR);
            }
        }
    }

    private class AddCourseToStudentHandler implements EventHandler<ActionEvent> {
        private Stage dialog;
        private ComboBox<AdminElective> courseCombo;
        private User selectedUser;

        @Override
        public void handle(ActionEvent event) {
            selectedUser = view.getUserTable().getSelectionModel().getSelectedItem();

            if (selectedUser == null) {
                view.showAlert("Ошибка", "Выберите пользователя", Alert.AlertType.WARNING);
                return;
            }

            // Проверяем, что выбранный пользователь - студент
            if (!"student".equals(selectedUser.getRole())) {
                view.showAlert("Ошибка", "Только студенты могут быть записаны на факультативы", Alert.AlertType.WARNING);
                return;
            }

            createDialog();
            setupUI();
            showDialog();
        }

        private void createDialog() {
            dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Добавление факультатива студенту");
        }

        private void setupUI() {
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(20));

            Label title = new Label("Добавление факультатива для: " + selectedUser.getLogin());
            courseCombo = new ComboBox<>();

            try {
                // Загружаем все доступные факультативы
                courseCombo.setItems(courseModel.getAllCourses());
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось загрузить факультативы", Alert.AlertType.ERROR);
                return;
            }

            Button addButton = new Button("Добавить");
            addButton.setOnAction(new ExecuteAddCourseToStudentHandler());

            Button cancelButton = new Button("Отмена");
            cancelButton.setOnAction(new CancelAddCourseHandler());

            HBox buttons = new HBox(10, addButton, cancelButton);
            vbox.getChildren().addAll(title, courseCombo, buttons);

            dialog.setScene(new Scene(vbox));
        }

        private void showDialog() {
            dialog.showAndWait();
        }

        // Обработчик для добавления факультатива
        private class ExecuteAddCourseToStudentHandler implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent e) {
                AdminElective selectedCourse = courseCombo.getValue();
                if (selectedCourse == null) {
                    view.showAlert("Ошибка", "Выберите факультатив", Alert.AlertType.WARNING);
                    return;
                }

                try {
                    // Получаем ID студента
                    int studentId = userModel.getStudentIdByUserId(selectedUser.getId());

                    // Записываем студента на факультатив
                    enrollmentModel.enrollStudent(studentId, selectedCourse.getId());

                    view.showAlert("Успех", "Студент успешно записан на факультатив", Alert.AlertType.INFORMATION);
                    dialog.close();
                } catch (SQLException ex) {
                    view.showAlert("Ошибка базы данных", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }

        // Обработчик отмены
        private class CancelAddCourseHandler implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent e) {
                dialog.close();
            }
        }
    }

    private class CancelHandler implements EventHandler<ActionEvent> {
        private final Stage dialog;

        public CancelHandler(Stage dialog) {
            this.dialog = dialog;
        }

        @Override
        public void handle(ActionEvent event) {
            dialog.close();
        }
    }

    // Вспомогательные методы
    private void loadData() {
        loadUserData();
        loadCourseData();
    }

    private void loadUserData() {
        try {
            ObservableList<User> users = userModel.getAllUsers();
            view.getUserTable().setItems(users);
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Ошибка загрузки пользователей", Alert.AlertType.ERROR);
        }
    }

    private void loadCourseData() {
        try {
            ObservableList<AdminElective> courses = courseModel.getAllCourses();
            view.getCourseTable().setItems(courses);
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Ошибка загрузки курсов", Alert.AlertType.ERROR);
        }
    }
}