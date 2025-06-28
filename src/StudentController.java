

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import java.sql.SQLException;

public class StudentController {
    private final StudentView view;
    private final StudentModel model;
    private StudentData studentData;

    public StudentController(StudentView view, int studentId) {
        this.view = view;
        this.model = new StudentModel(studentId);
        attachEvents();
        loadData();
        loadPersonalData();
    }

    private void loadPersonalData() {
        try {
            studentData = model.loadStudentData();
            if (studentData != null) {
                view.setStudentData(studentData);
            }
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить личные данные", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void attachEvents() {
        view.getEnrollButton().setOnAction(new EnrollHandler());
        view.getSaveButton().setOnAction(new SaveHandler());
    }

    // Обработчик кнопки "Сохранить"
    private class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            StudentData newData = view.getStudentData();
            newData.setUserId(studentData.getUserId()); // Сохраняем оригинальный ID

            try {
                model.updateStudentData(newData);
                studentData = newData; // Обновляем локальные данные
                view.showAlert("Успех", "Данные успешно обновлены", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось обновить данные: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void loadData() {
        loadEnrolledElectives();
        loadAvailableElectives();
    }

    private void loadEnrolledElectives() {
        try {
            ObservableList<StudentElective> electives = model.getEnrolledElectives();
            view.getEnrolledTable().setItems(electives);
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить ваши факультативы", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadAvailableElectives() {
        try {
            ObservableList<Elective> electives = model.getAvailableElectives();
            view.getAvailableTable().setItems(electives);
            System.out.println("Loaded available electives: " + electives.size());
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить доступные факультативы", Alert.AlertType.ERROR);
            e.printStackTrace(); // Добавьте эту строку
        }
    }

    // Обработчик кнопки "Записаться"
    private class EnrollHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Elective selected = view.getAvailableTable().getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showAlert("Ошибка", "Выберите факультатив для записи", Alert.AlertType.WARNING);
                return;
            }

            try {
                model.enrollToElective(selected.getId(), selected.getSemester());
                view.showAlert("Успех", "Вы успешно записаны на факультатив!", Alert.AlertType.INFORMATION);

                // Обновляем обе таблицы
                loadEnrolledElectives();
                loadAvailableElectives();
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось записаться на факультатив: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}