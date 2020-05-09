package filetransfer.windows;

import java.io.File;
import java.util.List;

public interface MainUIHandler {
    void onQRButtonClicked();

    void onSettingsButtonClicked();

    void onDisconnectButtonClicked();

    void onFilesDraggedIn(List<File> files);
}
