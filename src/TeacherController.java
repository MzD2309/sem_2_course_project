
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import java.sql.SQLException;

public class TeacherController {
    private final TeacherView view;
    private final TeacherModel model;
    private int currentElectiveId = -1;
    private int currentStudentId = -1;
    private TeacherData teacherData;

    public TeacherController(TeacherView view, int teacherId) {
        this.view = view;
        try {
            this.model = new TeacherModel(teacherId);
            attachEvents();
            loadElectives();
            loadPersonalData(); // Загружаем личные данные
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка инициализации модели преподавателя", e);
        }
    }

    private void loadPersonalData() {
        try {
            teacherData = model.loadTeacherData();
            if (teacherData != null) {
                view.setTeacherData(teacherData);
            }
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить личные данные", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    // Обработчик кнопки "Сохранить"
    private class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            TeacherData newData = view.getTeacherData();
            newData.setUserId(teacherData.getUserId()); // Сохраняем оригинальный ID пользователя

            try {
                model.updateTeacherData(newData);
                teacherData = newData; // Обновляем локальные данные
                view.showAlert("Успех", "Данные успешно обновлены", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось обновить данные: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void attachEvents() {
        view.getElectivesTable().getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        currentElectiveId = newSelection.getId();
                        loadStudentsForElective();
                    }
                });

        view.getStudentsTable().setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    currentStudentId = row.getItem().getId();
                }
            });
            return row;
        });

        view.getShowMarksButton().setOnAction(new ShowMarksHandler());
        view.getUpdateMarksButton().setOnAction(new UpdateMarksHandler());
        view.getSaveButton().setOnAction(new SaveHandler());
    }

    private void loadElectives() {
        try {
            ObservableList<AdminElective> electives = model.getDepartmentElectives();
            view.getElectivesTable().setItems(electives);

            if (!electives.isEmpty()) {
                view.getElectivesTable().getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить факультативы", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadStudentsForElective() {
        if (currentElectiveId == -1) return;

        try {
            ObservableList<Student> students = model.getStudentsForElective(currentElectiveId);
            view.getStudentsTable().setItems(students);
            view.getSemestersTable().getItems().clear();
        } catch (SQLException e) {
            view.showAlert("Ошибка", "Не удалось загрузить студентов", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private class ShowMarksHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (currentStudentId == -1 || currentElectiveId == -1) {
                view.showAlert("Ошибка", "Выберите студента и факультатив", Alert.AlertType.WARNING);
                return;
            }

            try {
                ObservableList<TeacherSemester> semesters =
                        model.getSemestersWithMarks(currentElectiveId, currentStudentId);
                view.getSemestersTable().setItems(semesters);
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Не удалось загрузить оценки", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private class UpdateMarksHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (currentStudentId == -1) {
                view.showAlert("Ошибка", "Выберите студента", Alert.AlertType.WARNING);
                return;
            }

            ObservableList<TeacherSemester> semesters = view.getSemestersTable().getItems();
            if (semesters.isEmpty()) {
                view.showAlert("Ошибка", "Нет данных для обновления", Alert.AlertType.WARNING);
                return;
            }

            try {
                for (TeacherSemester semester : semesters) {
                    model.updateSemesterMark(semester.getId(), currentStudentId, semester.getMark());
                }
                view.showAlert("Успех", "Оценки успешно обновлены", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                view.showAlert("Ошибка", "Ошибка при обновлении оценок: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}