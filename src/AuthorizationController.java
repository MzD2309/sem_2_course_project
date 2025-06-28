import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;

public class AuthorizationController {
    private final AuthorizationView view;
    private final AuthorizationModel model;
    private final Stage stage;

    //Конструктор
    public AuthorizationController(AuthorizationView view, AuthorizationModel model, Stage stage) {
        this.view = view;
        this.model = model;
        this.stage = stage;
        attachEvents();
    }

    // Привязка обработчиков событий для кнопок
    private void attachEvents() {
        view.getLoginButton().setOnAction(new LoginHandler());
        view.getRegistrationButton().setOnAction(new RegistrationHandler());
    }

    // Класс для обработки нажатия на кнопку регистрации
    private class RegistrationHandler implements EventHandler<ActionEvent>{

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
            roleCombo.getItems().addAll("teacher", "student");
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
        private final RegistrationHandler parentHandler; // Ссылка на родительский обработчик

        public ExecuteAddUserHandler(TextField loginField, PasswordField passwordField,
                                     ComboBox<String> roleCombo, Stage dialog,
                                     RegistrationHandler parentHandler) {
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
                        return;
                    }
                } else if ("teacher".equals(role)) {
                    if (lastName.isEmpty() || firstName.isEmpty() || middleName.isEmpty() || phone.isEmpty()) {
                        return;
                    }
                }

                model.addUser(
                        login, password, role,
                        lastName, firstName, middleName,
                        phone, address, semester
                );

                dialog.close();
            } catch (SQLException e) {
                e.printStackTrace();
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


    private class LoginHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String login = view.getLoginField().getText();
            String password = view.getPasswordField().getText();

            if (login.isEmpty() || password.isEmpty()) {
                view.getErrorLabel().setText("Логин или пароль не могут быть пустыми");
                return;
            }

            try {
                System.out.println("Authenticating user: " + login);
                User user = model.authenticate(login, password);

                if (user != null) {
                    System.out.println("User authenticated. Role: " + user.getRole() + ", ID: " + user.getId());

                    switch (user.getRole()) {
                        case "admin":
                            openAdminWindow(user.getId());
                            break;
                        case "student":
                            openStudentWindow(user.getId());
                            break;
                        case "teacher":
                            openTeacherWindow(user.getId());
                            break;
                        default:
                            view.getErrorLabel().setText("Неизвестная роль: " + user.getRole());
                    }
                } else {
                    System.out.println("Authentication failed for: " + login);
                    view.getErrorLabel().setText("Неверный логин или пароль");
                }
            } catch (SQLException e) {
                System.err.println("Authentication SQL error: " + e.getMessage());
                e.printStackTrace();
                view.getErrorLabel().setText("Ошибка подключения к базе данных: " + e.getMessage());
            }
        }
    }

    private void openAdminWindow(int userId) {
        System.out.println("Opening admin window for user: " + userId);
        Platform.runLater(new OpenAdminWindowHandler(userId));
    }

    private void openStudentWindow(int userId) {
        System.out.println("Opening student window for user: " + userId);
        try {
            int studentId = StudentModel.getStudentId(userId);
            System.out.println("Student ID: " + studentId);

            if (studentId == -1) {
                return;
            }

            Platform.runLater(new OpenStudentWindowRunnable(studentId));
        } catch (SQLException e) {
            System.err.println("Error getting student ID: " + e.getMessage());
            e.printStackTrace();
            view.getErrorLabel().setText("Ошибка получения данных студента: " + e.getMessage());
        }
    }

    private class OpenAdminWindowHandler implements Runnable {
        private final int userId;

        public OpenAdminWindowHandler(int userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            try {
                System.out.println("Closing authorization window");
                stage.close();

                Stage adminStage = new Stage();
                System.out.println("Creating AdminView...");
                AdminView adminView = new AdminView();

                System.out.println("Creating AdminController...");
                new AdminController(adminView, userId);

                adminStage.setScene(new Scene(adminView.getView(), 800, 600));
                adminStage.setTitle("Панель администратора (ID: " + userId + ")");

                System.out.println("Showing admin window");
                adminStage.show();
            } catch (Exception e) {
                System.err.println("Error opening admin window: ");
                e.printStackTrace();
                Platform.runLater(new ShowErrorRunnable(
                        "Ошибка открытия панели администратора: " + e.getMessage()
                ));
            }
        }
    }

    private class OpenStudentWindowRunnable implements Runnable {
        private final int studentId;

        public OpenStudentWindowRunnable(int studentId) {
            this.studentId = studentId;
        }

        @Override
        public void run() {
            try {
                System.out.println("Closing authorization window for student");
                stage.close();

                Stage studentStage = new Stage();
                System.out.println("Creating StudentView...");
                StudentView studentView = new StudentView();

                System.out.println("Creating StudentController...");
                new StudentController(studentView, studentId);

                System.out.println("Setting up student scene...");
                studentStage.setScene(new Scene(studentView.getView(), 900, 600));
                studentStage.setTitle("Панель студента");

                System.out.println("Showing student window");
                studentStage.show();
            } catch (Exception e) {
                System.err.println("Error opening student window: ");
                e.printStackTrace();

                Platform.runLater(new ShowErrorRunnable(
                        "Ошибка открытия панели студента: " + e.getMessage()
                ));
            }
        }
    }
    private void openTeacherWindow(int userId) {
        try {
            int teacherId = model.getTeacherId(userId);
            if (teacherId == -1) {
                return;
            }

            Platform.runLater(() -> {
                stage.close();
                Stage teacherStage = new Stage();
                TeacherView teacherView = new TeacherView();
                new TeacherController(teacherView, teacherId);

                // Используем getTabPane() вместо getView()
                teacherStage.setScene(new Scene(teacherView.getTabPane(), 900, 600));
                teacherStage.setTitle("Панель преподавателя");
                teacherStage.show();
            });
        } catch (SQLException e) {
            view.getErrorLabel().setText("Ошибка получения данных преподавателя");
            e.printStackTrace();
        }
    }



    private class ShowErrorRunnable implements Runnable {
        private final String errorMessage;

        public ShowErrorRunnable(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public void run() {
            view.getErrorLabel().setText(errorMessage);
        }
    }
}