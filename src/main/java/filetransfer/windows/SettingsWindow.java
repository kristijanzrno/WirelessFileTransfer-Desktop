package filetransfer.windows;

import filetransfer.controllers.SettingsController;
import filetransfer.utils.Preferences;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsWindow {
    private Preferences preferences;
    private SettingsController settingsController;

    public SettingsWindow(Preferences preferences) {
        this.preferences = preferences;
        this.setupUI();
    }

    private void setupUI() {
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
}
