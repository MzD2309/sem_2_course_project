

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class AuthorizationView {
    private final GridPane view = new GridPane();
    private final TextField loginField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();
    private final Button loginButton = new Button("Войти");
    private final Button registrationButton = new Button("Регистрация");

    public AuthorizationView() {
        configureView();
    }

    private void configureView() {
        view.setAlignment(Pos.CENTER);
        view.setHgap(10);
        view.setVgap(10);
        view.setPadding(new Insets(20));

        Label titleLabel = new Label("Авторизация");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        view.add(titleLabel, 0, 0, 2, 1);
        view.add(new Label("Логин:"), 0, 1);
        view.add(loginField, 1, 1);
        view.add(new Label("Пароль:"), 0, 2);
        view.add(passwordField, 1, 2);
        view.add(loginButton, 0, 3);
        view.add(registrationButton, 1, 3);
        view.add(errorLabel, 0, 4);

        errorLabel.setStyle("-fx-text-fill: red;");
        loginField.setPromptText("Введите логин");
        passwordField.setPromptText("Введите пароль");
    }

    public GridPane getView() { return view; }
    public TextField getLoginField() { return loginField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginButton() { return loginButton; }
    public Label getErrorLabel() { return errorLabel; }
    public Button getRegistrationButton(){return registrationButton;}
}