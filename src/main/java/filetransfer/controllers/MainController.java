package filetransfer.controllers;

import filetransfer.model.Device;
import filetransfer.windows.MainUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


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
    private Pane toolbar;
    @FXML
    private VBox mainWindow;
    @FXML
    private Pane transferPane;
    @FXML
    private Label transferTitle;
    @FXML
    private Label transferDescription;
    @FXML
    private ImageView folderImage;
    @FXML
    private ProgressIndicator progressIndicator;

    private MainUIHandler uiHandler;

    public void initUIHandler(MainUIHandler uiHandler) {
        if (this.uiHandler == null) {
            this.uiHandler = uiHandler;
            disconnectButton.setVisible(false);
            setupDragAndDrop();
            setupUI();
        }
    }

    private void setupDragAndDrop() {
        mainWindow.setOnDragOver(event -> {
            if (event.getGestureSource() != mainWindow && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        mainWindow.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                uiHandler.onFilesDraggedIn(db.getFiles());
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void setupUI(){
        transferPane.setVisible(false);
        progressIndicator.setVisible(false);
        addToolbarShadow();
    }

    private void updateUI(boolean connected){
        mainMessage.setVisible(!connected);
        transferPane.setVisible(connected);
    }

    public void activateProgressIndicator(boolean finished){
        folderImage.setVisible(finished);
        progressIndicator.setVisible(!finished);
    }

    private void addToolbarShadow(){
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(1.5);
        dropShadow.setColor(Color.color(0.8, 0.8, 0.8));
        toolbar.setEffect(dropShadow);
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
        updateUI(connected);
    }

    public void setTransferDescription(String text){
        this.transferDescription.setText(text);
    }

    public void setTransferTitle(String text){
        this.transferTitle.setText(text);
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
