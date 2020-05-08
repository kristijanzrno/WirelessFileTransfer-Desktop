package filetransfer.windows;


import filetransfer.*;
import filetransfer.controllers.MainController;
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
import java.util.Optional;
import java.util.Scanner;

public class FileTransfer extends Application implements MainUIHandler, DiscoveryUtils, NetworkHandlers {

    private boolean status = false;
    private Discovery discovery;
    private Device device;
    private ConnectionHandler connectionHandler;
    private MainController mainController;

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

        startServices();

    }

    @Override
    public void onQRButtonClicked() {
        BufferedImage bufferedImage = QRUtils.generateQRCode(device.getIp(), 300, 300);
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
            deviceName.setText(device.getName());
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
        mainController.setDeviceInfo(device);

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
                    } else {
                        connectionHandler.refuseConnection();
                        status = false;
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
