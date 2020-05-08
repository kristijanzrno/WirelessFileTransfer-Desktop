package filetransfer.controllers;

import filetransfer.Device;
import filetransfer.MainUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


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

    private MainUIHandler uiHandler;

    public void initUIHandler(MainUIHandler uiHandler) {
        if (this.uiHandler == null)
            this.uiHandler = uiHandler;
        disconnectButton.setVisible(false);
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
