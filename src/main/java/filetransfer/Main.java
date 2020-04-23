package filetransfer;

import filetransfer.controllers.MainController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main extends Application implements DiscoveryUtils {

    private boolean status = false;
    private Discovery discovery;
    private Device device;
    private ConnectionHandler connectionHandler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main_window.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        /*Main main = new Main();
        main.create();

        Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label, dropped);
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        dragTarget.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    dropped.setText(db.getFiles().toString());
                    main.transferFiles(db.getFiles());
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used
                event.setDropCompleted(success);

                event.consume();
            }
        });


        StackPane root = new StackPane();
        root.getChildren().add(dragTarget);

        Scene scene = new Scene(root, 500, 250);

        primaryStage.setTitle("Drag Test");
        primaryStage.setScene(scene);
        primaryStage.show();*/
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
       /* while (!action.equals("q")) {
            action = scanner.nextLine().split(":")[0];
            File file = new File(scanner.nextLine().split(":")[1]);
            System.out.println(action);
            switch (action) {
                case "sendFile":
                    connectionHandler.sendMessage(Constants.FILE_SEND_MESSAGE + Constants.DATA_SEPARATOR + 1);
                    connectionHandler.sendFile(Constants.FILE_NAME_MESSAGE + Constants.DATA_SEPARATOR + file.getName() + Constants.DATA_SEPARATOR + file.length(), file.getAbsolutePath());
                    break;
            }
        }*/
    }

    @Override
    public void changeStatus(boolean busy) {
        if (busy)
            device.setAvailable("Busy");
        else
            device.setAvailable("Available");
    }

    public void transferFiles(List<File> files) {
        connectionHandler.sendMessage(new Message.Builder().add(Constants.FILE_SEND_MESSAGE).add(files.size() + "").build());
        for (File file : files) {
            System.out.println("Sending file " + file.getName());
            connectionHandler.sendFile(new Message.Builder().add(Constants.FILE_NAME_MESSAGE).add(file.getName()).add(file.length() + "").add(file.getAbsolutePath()).build(), file.getAbsolutePath());
        }

    }



}
