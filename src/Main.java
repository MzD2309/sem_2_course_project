import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) {
        AuthorizationView view = new AuthorizationView();
        AuthorizationModel model = new AuthorizationModel();
        new AuthorizationController(view, model, stage);

        stage.setScene(new Scene(view.getView(), 400, 300));
        stage.setTitle("Авторизация");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}