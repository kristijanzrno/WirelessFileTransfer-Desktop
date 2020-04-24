package filetransfer.windows;

import filetransfer.*;
import filetransfer.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.Scanner;

public class FileTransfer extends Application implements MainUIHandler, DiscoveryUtils, NetworkHandlers {

    private boolean status = false;
    private Discovery discovery;
    private Device device;
    private ConnectionHandler connectionHandler;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_window.fxml"));
        Parent root = loader.load();
        MainController mainController = loader.getController();
        mainController.initUIHandler(this);
        stage.setTitle("Wireless File Transfer");
        stage.setScene(new Scene(root, 810, 550));
        stage.setResizable(false);
        stage.show();

        startServices();

    }

    @Override
    public void onQRButtonClicked() {
        System.out.println("qr clicked");
    }

    @Override
    public void onSettingsButtonClicked() {
        System.out.println("settings clicked");
    }

    private void startServices() {
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
    }

    @Override
    public void changeStatus(boolean busy) {
        this.status = busy;
    }


    @Override
    public void onConnectionAttempted(Device request) {
        if (!status) {
            status = true;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Connection Request");
                    alert.setContentText(request.getName() + " wants to connect...");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        connectionHandler.acceptConnection();
                        // ... user chose OK
                    } else {
                        connectionHandler.refuseConnection();
                        status = false;
                        // ... user chose CANCEL or closed the dialog
                    }
                }
            });
        }

    }

    @Override
    public void onDeviceConnected() {

    }

    @Override
    public void onConnectionRefused() {

    }

    @Override
    public void onConnectionTerminated() {

    }

    @Override
    public void onFileTransferStarted(int noOfFiles) {

    }

    @Override
    public void onFileTransferred() {

    }

    @Override
    public void onFileTransferFailed(String filename) {

    }

    @Override
    public void onReceivingFiles(int noOfFiles) {

    }

    @Override
    public void onFileReceived() {

    }

    public static void main(String[] args) {
        launch();
    }

}
