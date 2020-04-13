package filetransfer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("Waiting for connection...");
        ConnectionHandler connectionHandler = new ConnectionHandler();
        connectionHandler.start();
        Discovery discovery = new Discovery("1.2.3.4", connectionHandler.getPort());
        Thread discoveryThread = new Thread(discovery);
        discoveryThread.start();
    }
}
