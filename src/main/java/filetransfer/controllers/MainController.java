package filetransfer.controllers;

import filetransfer.Device;
import filetransfer.MainUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.management.InstanceAlreadyExistsException;

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

    private MainUIHandler uiHandler;

    public void initUIHandler(MainUIHandler uiHandler){
        if(this.uiHandler == null)
            this.uiHandler = uiHandler;
    }


    public void setDeviceMessage(String message){
        this.mainMessage.setText(message);
    }
    public void setDeviceInfo(Device device){
        this.deviceInfo.setText(device.getName());
    }

    public void setDeviceStatus(boolean connected){
        this.deviceStatus.setText(connected ? "Connected" : "Waiting for connection...");
    }

    @FXML
    private void onQRButtonClicked(){
        uiHandler.onQRButtonClicked();
    }

    @FXML
    private void onSettingsButtonClicked(){
        uiHandler.onSettingsButtonClicked();
    }



}
