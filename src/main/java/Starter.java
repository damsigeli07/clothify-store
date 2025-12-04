import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_form.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700); // Set exact size

        primaryStage.setTitle("Satine - Store Management");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Prevent resizing
        primaryStage.centerOnScreen(); // Center the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}