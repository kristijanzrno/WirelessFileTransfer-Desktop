package filetransfer.windows;


import filetransfer.Constants;
import filetransfer.controllers.MainController;
import filetransfer.model.Device;
import filetransfer.model.Message;
import filetransfer.network.ConnectionHandler;
import filetransfer.network.Discovery;
import filetransfer.network.DiscoveryUtils;
import filetransfer.network.NetworkHandlers;
import filetransfer.utils.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class FileTransfer extends Application implements MainUIHandler, DiscoveryUtils, NetworkHandlers {

    private boolean status = false;
    private Preferences preferences;
    private Discovery discovery;
    private Device currentDevice, connectedDevice;
    private ConnectionHandler connectionHandler;
    private MainController mainController;

    private int noOfFiles = 0;
    private int finished = 0;
    private boolean hadErrors = false;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_window.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        mainController.initUIHandler(this);
        stage.setTitle("Wireless File Transfer");
        stage.setScene(new Scene(root, 810, 550));
        stage.setResizable(false);
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
        startServices();
    }

    @Override
    public void onQRButtonClicked() {
        if (currentDevice != null)
            new QRWindow(currentDevice);
    }

    @Override
    public void onSettingsButtonClicked() {
        new SettingsWindow(preferences);
    }

    @Override
    public void onDisconnectButtonClicked() {
        //todo ask first
        connectionHandler.terminateConnection();
    }

    @Override
    public void onFilesDraggedIn(List<File> files) {
        onFileTransferStarted(files.size());
        connectionHandler.sendMessage(new Message.Builder().add(Constants.FILE_SEND_MESSAGE).add(files.size() + "").build());
        for (File file : files) {
            System.out.println("Sending file " + file.getName());
            connectionHandler.sendFile(new Message.Builder().add(Constants.FILE_NAME_MESSAGE).add(file.getName()).add(file.length() + "").add(file.getAbsolutePath()).build(), file.getAbsolutePath());
        }
    }


    private void startServices() {
        System.setProperty("javax.net.ssl.keyStore", "server.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "server");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        connectionHandler = new ConnectionHandler(this);
        connectionHandler.start();
        currentDevice = new Device(connectionHandler.getDeviceName(), connectionHandler.getIPAddress(), Integer.parseInt(connectionHandler.getPort()), "Available", System.getProperty("os.name"));
        discovery = new Discovery(currentDevice);
        preferences = new Preferences();
        Thread discoveryThread = new Thread(discovery);
        discoveryThread.start();
        mainController.setDeviceInfo(currentDevice);

    }

    @Override
    public void changeStatus(boolean busy) {
        this.status = busy;
    }


    @Override
    public void onConnectionAttempted(Device request) {
        if (!status) {
            status = true;
            Platform.runLater(() -> {
                if (Alerts.yesNoAlert("Connection Request", request.getName() + " wants to connect...")) {
                    connectionHandler.acceptConnection();
                    connectedDevice = request;
                    onDeviceConnected();
                } else {
                    connectionHandler.refuseConnection();
                    status = false;
                }
            });
        }

    }

    private void updateStatus(boolean increase) {
        if (increase)
            finished++;
        Platform.runLater(() -> {
            mainController.setMainMessage(finished + "/" + noOfFiles + "Files Transferred...");
            if (noOfFiles == finished) {
                if (hadErrors) {
                    //todo had some errors
                } else {
                    // todo message = success
                    mainController.setMainMessage("Drag and drop files to transfer them to the Android device!");
                }
                noOfFiles = finished = 0;
                //todo spinner dismiss
            }
        });
    }

    @Override
    public void onDeviceConnected() {
        if (connectedDevice != null) {
            Platform.runLater(() -> {
                mainController.setDeviceStatus(true, connectedDevice.getName());
                mainController.setMainMessage("Drag and drop files to transfer them to the Android device!");
            });

        }
    }

    @Override
    public void onConnectionRefused() {
        // currently impossible as the connection establishment is one-directional, future work
    }

    @Override
    public void onConnectionTerminated() {
        mainController.setDeviceStatus(false, "");
        mainController.setMainMessage("Connect with a device to start transferring files!");
        connectedDevice = null;
        noOfFiles = finished = 0;
        hadErrors = false;
    }

    @Override
    public void onFileTransferStarted(int noOfFiles) {
        this.hadErrors = false;
        this.noOfFiles = noOfFiles;
        updateStatus(false);
    }

    @Override
    public void onFileTransferred() {
        updateStatus(true);
    }

    @Override
    public void onFileTransferFailed(String filename) {
        this.hadErrors = true;
        updateStatus(true);
    }

    @Override
    public void onReceivingFiles(int noOfFiles) {
        this.noOfFiles = noOfFiles;
        updateStatus(false);
    }

    @Override
    public void onFileReceived() {
        updateStatus(true);

    }

    public static void main(String[] args) {
        launch();
    }

}
