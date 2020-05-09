package filetransfer.windows;

import filetransfer.model.Device;
import filetransfer.model.Message;
import filetransfer.utils.QRUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class QRWindow {

    public QRWindow(Device currentDevice) {
        String message = new Message.Builder().add(currentDevice.getName()).add(currentDevice.getIp()).add(currentDevice.getPort() + "").add(currentDevice.getAvailable()).add(currentDevice.getInfo()).build();
        BufferedImage bufferedImage = QRUtils.generateQRCode(message, 300, 300);
        if (bufferedImage != null)
            setupUI(bufferedImage, currentDevice.getName());

    }

    private void setupUI(BufferedImage bufferedImage, String currentDeviceName) {
        Stage stage = new Stage();
        stage.setTitle("QR Code");
        stage.setResizable(false);
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 350);
        root.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        Label deviceName = new Label();
        deviceName.setAlignment(Pos.CENTER);
        deviceName.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        deviceName.setText(currentDeviceName);
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
