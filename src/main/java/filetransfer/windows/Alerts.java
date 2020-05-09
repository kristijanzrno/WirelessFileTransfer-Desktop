package filetransfer.windows;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {

    public static boolean yesNoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
        /*if (result.get() == ButtonType.OK) {
            connectionHandler.acceptConnection();
            connectedDevice = request;
            onDeviceConnected();
        } else {
            connectionHandler.refuseConnection();
            status = false;
        }*/

    }
}
