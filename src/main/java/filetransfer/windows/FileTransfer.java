package filetransfer.windows;


import filetransfer.*;
import filetransfer.controllers.MainController;
import filetransfer.controllers.SettingsController;
import filetransfer.model.Device;
import filetransfer.model.Message;
import filetransfer.network.ConnectionHandler;
import filetransfer.network.Discovery;
import filetransfer.network.DiscoveryUtils;
import filetransfer.utils.Preferences;
import filetransfer.utils.QRUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.net.Inet4Address;
import java.util.Optional;

public class FileTransfer extends Application implements MainUIHandler, DiscoveryUtils, NetworkHandlers {

    private boolean status = false;
    private Preferences preferences;
    private Discovery discovery;
    private Device currentDevice, connectedDevice;
    private ConnectionHandler connectionHandler;
    private MainController mainController;
    private SettingsController settingsController;

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
        stage.show();
        String address = Inet4Address.getLocalHost().getHostAddress();
        System.out.println(address);
        System.out.println(Inet4Address.getLocalHost());
        startServices(address);
    }

    @Override
    public void onQRButtonClicked() {
        String message = new Message.Builder().add(currentDevice.getName()).add(currentDevice.getIp()).add(currentDevice.getPort() + "").add(currentDevice.getAvailable()).add(currentDevice.getInfo()).build();
        BufferedImage bufferedImage = QRUtils.generateQRCode(message, 300, 300);
        if (bufferedImage != null) {
            Stage stage = new Stage();
            stage.setTitle("QR Code");
            stage.setResizable(false);
            Pane root = new Pane();
            Scene scene = new Scene(root, 350, 350);
            root.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

            Label deviceName = new Label();
            deviceName.setAlignment(Pos.CENTER);
            deviceName.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
            deviceName.setText(currentDevice.getName());
            deviceName.setMinWidth(350);
            deviceName.setLayoutY(10);
            root.getChildren().add(deviceName);

            Label guideMessage = new Label();
            guideMessage.setAlignment(Pos.CENTER);
            guideMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
            guideMessage.setText("Scan the QR Code using the Android application");
            guideMessage.setMinWidth(350);
            guideMessage.setLayoutY(330);
            root.getChildren().add(guideMessage);

            ImageView imageView = new ImageView();
            imageView.setLayoutX(25);
            imageView.setLayoutY(25);
            imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            root.getChildren().add(imageView);

            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void onSettingsButtonClicked() {
        try {
            System.out.println("Settings");
            Stage stage = new Stage();
            stage.setTitle("Settings");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
            Parent root = loader.load();
            settingsController = loader.getController();
            settingsController.initStage(stage, preferences);
            stage.setScene(new Scene(root, 590, 600));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectButtonClicked() {
        //todo ask first
        connectionHandler.terminateConnection();
    }


    private void startServices(String hostAddress) {
        System.setProperty("javax.net.ssl.keyStore", "server.ks");
        System.setProperty("javax.net.ssl.keyStorePassword", "server");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        System.out.println("Waiting for connection...");
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Connection Request");
                    alert.setContentText(request.getName() + " wants to connect...");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        connectionHandler.acceptConnection();
                        connectedDevice = request;
                        onDeviceConnected();
                    } else {
                        connectionHandler.refuseConnection();
                        status = false;
                    }
                }
            });
        }

    }

    private void updateStatus(boolean increase) {
        if (increase)
            finished++;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    @Override
    public void onDeviceConnected() {
        if (connectedDevice != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mainController.setDeviceStatus(true, connectedDevice.getName());
                    mainController.setMainMessage("Drag and drop files to transfer them to the Android device!");
                }
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
