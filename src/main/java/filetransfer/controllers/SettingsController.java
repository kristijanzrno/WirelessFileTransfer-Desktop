package filetransfer.controllers;

import filetransfer.Preferences;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

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

    private Stage stage;
    private Preferences preferences;

    public void initStage(Stage stage, Preferences preferences) {
        if (this.stage == null)
            this.stage = stage;
        if (this.preferences == null) {
            this.preferences = preferences;
            updateUI();
        }
    }

    private void updateUI() {
        imagesPath.setText(preferences.getImagesPath());
        audioPath.setText(preferences.getAudioPath());
        videosPath.setText(preferences.getVideosPath());
        documentsPath.setText(preferences.getDocumentsPath());
        otherPath.setText(preferences.getOtherPath());

    }


    @FXML
    private void changeImagesPath() {
        changeFolder("images");
    }

    @FXML
    private void changeAudioPath() {
        changeFolder("audio");
    }

    @FXML
    private void changeVideosPath() {
        changeFolder("videos");
    }

    @FXML
    private void changeDocumentsPath() {
        changeFolder("documents");
    }

    @FXML
    private void changeOtherPath() {
        changeFolder("other");
    }

    private void changeFolder(String mediaType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the folder");
        File directory = directoryChooser.showDialog(stage);
        if (directory != null) {
            String path = directory.getAbsolutePath();
            preferences.updateType(mediaType, path);
            updateUI();
        }
    }
}
