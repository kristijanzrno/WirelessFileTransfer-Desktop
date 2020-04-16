package filetransfer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

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
        Device device = new Device(connectionHandler.getDeviceName(), "1.2.3.4", connectionHandler.getPriority(), "available", "extrainfo");
        Discovery discovery = new Discovery(device);
        Thread discoveryThread = new Thread(discovery);
        discoveryThread.start();
        Scanner scanner = new Scanner(System.in);
        String action = "";
        while (!action.equals("q")) {
            action = scanner.nextLine().split(":")[0];
            File file = new File(scanner.nextLine().split(":")[1]);
            System.out.println(action);
            switch (action) {
                case "sendFile":
                    connectionHandler.sendMessage(Constants.FILE_SEND_MESSAGE + Constants.DATA_SEPARATOR + 1);
                    connectionHandler.sendFile(Constants.FILE_NAME_MESSAGE + Constants.DATA_SEPARATOR + file.getName() + Constants.DATA_SEPARATOR + file.length(), file.getAbsolutePath());
                    break;

            }
        }
    }

}
