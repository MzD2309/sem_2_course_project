

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;


public class AdminView {
    private final TabPane tabPane = new TabPane();
    private final TableView<User> userTable = new TableView<>();
    private final TableView<AdminElective> courseTable = new TableView<>();
    private final Button addUserButton = new Button("+ Добавить нового пользователя");
    private final Button addCourseToStudentButton = new Button("+ Добавить факультатив");
    private final Button deleteUserButton = new Button("Удалить");
    private final Button addCourseButton = new Button("+ Добавить");
    private final Button deleteCourseButton = new Button("Удалить");

    public AdminView() {
        configureUserTab();
        configureCourseTab();
    }

    private void configureUserTab() {
        Tab usersTab = new Tab("Пользователи");
        VBox userLayout = new VBox(10);
        userLayout.setPadding(new Insets(10));

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> loginCol = new TableColumn<>("Логин");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<User, String> roleCol = new TableColumn<>("Роль");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTable.getColumns().addAll(idCol, loginCol, roleCol);

        HBox userButtons = new HBox(10, addUserButton, deleteUserButton, addCourseToStudentButton);
        userButtons.setPadding(new Insets(10));
        userLayout.getChildren().addAll(userTable, userButtons);
        usersTab.setContent(userLayout);
        tabPane.getTabs().add(usersTab);
    }

    private void configureCourseTab() {
        Tab coursesTab = new Tab("Факультативы");
        VBox courseLayout = new VBox(10);
        courseLayout.setPadding(new Insets(10));

        TableColumn<AdminElective, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AdminElective, String> subjectCol = new TableColumn<>("Предмет");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));

        TableColumn<AdminElective, String> departmentCol = new TableColumn<>("Кафедра");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        courseTable.getColumns().addAll(idCol, subjectCol, departmentCol);

        HBox courseButtons = new HBox(10, addCourseButton, deleteCourseButton);
        courseButtons.setPadding(new Insets(10));
        courseLayout.getChildren().addAll(courseTable, courseButtons);
        coursesTab.setContent(courseLayout);
        tabPane.getTabs().add(coursesTab);
    }

    public TabPane getView() { return tabPane; }
    public TableView<User> getUserTable() { return userTable; }
    public TableView<AdminElective> getCourseTable() { return courseTable; }
    public Button getAddUserButton() { return addUserButton; }
    public Button getDeleteUserButton() { return deleteUserButton; }
    public Button getAddCourseButton() { return addCourseButton; }
    public Button getDeleteCourseButton() { return deleteCourseButton; }

    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Button getAddCourseToStudentButton() {
        return addCourseToStudentButton;
    }
}