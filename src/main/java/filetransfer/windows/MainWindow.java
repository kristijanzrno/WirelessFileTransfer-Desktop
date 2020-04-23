package filetransfer.windows;

import filetransfer.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_window.fxml"));
        Parent root = (Parent) loader.load();
        MainController mainController = loader.getController();
        mainController.initModel();

        stage.setTitle("Hello World");
        stage.setScene(new Scene(root, 300, 275));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
