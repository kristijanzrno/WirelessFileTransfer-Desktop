package filetransfer.controllers;

import filetransfer.model.Device;
import filetransfer.windows.MainUIHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;


public class MainController {


    @FXML
    private Label mainMessage;
    @FXML
    private Label deviceInfo;
    @FXML
    private Label deviceStatus;
    @FXML
    private Button qrButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private VBox mainWindow;

    private MainUIHandler uiHandler;

    public void initUIHandler(MainUIHandler uiHandler) {
        if (this.uiHandler == null) {
            this.uiHandler = uiHandler;
            disconnectButton.setVisible(false);
            setupDragAndDrop();
        }
    }

    public void setupDragAndDrop() {
        mainWindow.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != mainWindow && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        mainWindow.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    uiHandler.onFilesDraggedIn(db.getFiles());
                }
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }


    public void setMainMessage(String message) {
        this.mainMessage.setText(message);
    }

    public void setDeviceInfo(Device device) {
        this.deviceInfo.setText(device.getName());
    }

    public void setDeviceStatus(boolean connected, String deviceName) {
        this.deviceStatus.setText(connected ? "Connected to " + deviceName : "Waiting for connection...");
        this.disconnectButton.setVisible(connected);
    }

    @FXML
    private void onQRButtonClicked() {
        uiHandler.onQRButtonClicked();
    }

    @FXML
    private void onSettingsButtonClicked() {
        uiHandler.onSettingsButtonClicked();
    }

    @FXML
    private void onDisconnectButtonClicked() {
        uiHandler.onDisconnectButtonClicked();
    }


}
