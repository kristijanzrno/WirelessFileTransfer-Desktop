package filetransfer.windows;


import filetransfer.Constants;
import filetransfer.controllers.MainController;
import filetransfer.customUI.Toast;
import filetransfer.model.Device;
import filetransfer.model.Message;
import filetransfer.network.ConnectionHandler;
import filetransfer.network.Discovery;
import filetransfer.network.DiscoveryUtils;
import filetransfer.network.NetworkHandlers;
import filetransfer.utils.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Stage mainStage;

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
        mainStage = stage;
        preferences = new Preferences();
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
        Platform.runLater(() -> {
            if (Alerts.yesNoAlert("Terminate Connection", "Do you really want to terminate the connection?")) {
                connectionHandler.terminateConnection();
            }
        });
    }

    @Override
    public void onFilesDraggedIn(List<File> files) {
        onFileTransferStarted(files.size());
        connectionHandler.sendMessage(new Message.Builder().add(Constants.FILE_SEND_MESSAGE).add(files.size() + "").build());
        for (File file : files) {
            connectionHandler.sendFile(new Message.Builder().add(Constants.FILE_NAME_MESSAGE).add(file.getName()).add(file.length() + "").add(file.getAbsolutePath()).build(), file.getAbsolutePath());
        }
    }


    private void startServices() {
        System.setProperty("javax.net.ssl.keyStore", "server.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "server");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        connectionHandler = new ConnectionHandler(this, preferences);
        connectionHandler.start();
        currentDevice = new Device(connectionHandler.getDeviceName(), connectionHandler.getIPAddress(), Integer.parseInt(connectionHandler.getPort()), "Available", System.getProperty("os.name"));
        discovery = new Discovery(currentDevice, this);
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
            mainController.setTransferTitle("Transferring files...");
            mainController.setTransferDescription(finished + "/" + noOfFiles + " Files Transferred...");
            if (noOfFiles == finished) {
                if (hadErrors) {
                    Toast.makeText(mainStage, "Some files could not be transferred.", 1000, 200, 200);
                } else {
                    Toast.makeText(mainStage, "Successfully transferred all files!", 1000, 200, 200);
                    mainController.setTransferDescription("Drag and drop files to transfer them to the Android device!");
                    mainController.setTransferTitle("Waiting for files...");
                }
                noOfFiles = finished = 0;
                mainController.activateProgressIndicator(true);
            }
        });
    }

    @Override
    public void onDeviceConnected() {
        mainController.setDeviceStatus(true, connectedDevice.getName());
    }

    @Override
    public void onConnectionRefused() {
        // currently impossible as the connection establishment is one-directional, future work
    }

    @Override
    public void onConnectionTerminated() {
        connectedDevice = null;
        noOfFiles = finished = 0;
        hadErrors = false;
        status = false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainController.setDeviceStatus(false, "");
                Toast.makeText(mainStage, "Lost connection...", 1000, 200, 200);
            }
        });
    }

    @Override
    public void onFileTransferStarted(int noOfFiles) {
        this.hadErrors = false;
        this.noOfFiles = noOfFiles;
        updateStatus(false);
        mainController.activateProgressIndicator(false);
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
        mainController.activateProgressIndicator(false);
    }

    @Override
    public void onFileReceived() {
        updateStatus(true);

    }

    @Override
    public void noDiscoveryPortsAvailable() {
        Platform.runLater(() -> {
            Alerts.okAlert("No ports available.", "The discovery feature can not work because all the device ports are busy. Please use the QR feature.");
        });
    }

    public static void main(String[] args) {
        launch();
    }

}
