package filetransfer.windows;

import filetransfer.MainUIHandler;
import filetransfer.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application implements MainUIHandler {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_window.fxml"));
        Parent root = (Parent) loader.load();
        MainController mainController = loader.getController();
        mainController.initUIHandler(this);
        stage.setTitle("Wireless File Transfer");
        stage.setScene(new Scene(root, 814, 450));
        stage.setResizable(false);
        stage.show();

    }

    @Override
    public void onQRButtonClicked() {
        System.out.println("qr clicked");
    }

    @Override
    public void onSettingsButtonClicked() {
        System.out.println("settings clicked");
    }





    public static void main(String[] args) {
        launch(args);
    }


}
