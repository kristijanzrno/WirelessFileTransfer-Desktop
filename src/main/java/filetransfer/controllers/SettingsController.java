package filetransfer.controllers;

import filetransfer.MainUIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SettingsController {

    @FXML
    private Label imagesPath;
    @FXML
    private Label audioPath;
    @FXML
    private Label videosPath;
    @FXML
    private Label documentsPath;
    @FXML
    private Label otherPath;


    private MainUIHandler uiHandler;

    public void initUIHandler(MainUIHandler uiHandler) {
        if (this.uiHandler == null)
            this.uiHandler = uiHandler;
    }


    @FXML
    private void changeImagesPath() {

    }

    @FXML
    private void changeAudioPath() {

    }

    @FXML
    private void changeVideosPath() {

    }

    @FXML
    private void changeDocumentsPath() {

    }

    @FXML
    private void changeOtherPath() {

    }
}
