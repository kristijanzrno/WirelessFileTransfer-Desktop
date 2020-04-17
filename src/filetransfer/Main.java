package filetransfer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class Main extends Application implements DiscoveryUtils {

    private boolean status = false;
    private Discovery discovery;
    private Device device;
    private ConnectionHandler connectionHandler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public void create() {
        System.setProperty("javax.net.ssl.keyStore", "server.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "server");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        System.out.println("Waiting for connection...");
        connectionHandler = new ConnectionHandler(this);
        connectionHandler.start();
        device = new Device(connectionHandler.getDeviceName(), "1.2.3.4", Integer.parseInt(connectionHandler.getPort()), "Available", System.getProperty("os.name"));
        discovery = new Discovery(device);
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

    @Override
    public void changeStatus(boolean busy) {
        if (busy)
            device.setAvailable("Busy");
        else
            device.setAvailable("Available");
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.create();

    }


}
