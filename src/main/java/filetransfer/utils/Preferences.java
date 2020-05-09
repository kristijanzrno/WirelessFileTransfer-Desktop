package filetransfer.utils;


import filetransfer.Constants;

public class Preferences {
    private String imagesPath = "Default";
    private String audioPath = "Default";
    private String videosPath = "Default";
    private String documentsPath = "Default";
    private String otherPath = "Default";


    public Preferences() {
        loadPreferences();
    }


    public void updateType(String type, String path) {
        switch (type) {
            case "images":
                this.imagesPath = path;
                break;
            case "audio":
                this.audioPath = path;
                break;
            case "videos":
                this.videosPath = path;
                break;
            case "documents":
                this.documentsPath = path;
                break;
            default:
                this.otherPath = path;
                break;
        }
        savePreferences();
    }


    public String getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getVideosPath() {
        return videosPath;
    }

    public void setVideosPath(String videosPath) {
        this.videosPath = videosPath;
    }

    public String getDocumentsPath() {
        return documentsPath;
    }

    public void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }

    public String getOtherPath() {
        return otherPath;
    }

    public void setOtherPath(String otherPath) {
        this.otherPath = otherPath;
    }

    private void loadPreferences() {
        String data = FileHandler.readString("prefs.txt");
        if (data != null) {
            String[] paths = data.split(Constants.SETTINGS_SEPARATOR);
            imagesPath = paths[0];
            audioPath = paths[1];
            videosPath = paths[2];
            documentsPath = paths[3];
            otherPath = paths[4];
        }
    }

    private void savePreferences() {
        StringBuilder data = new StringBuilder();
        data.append(imagesPath).append(Constants.SETTINGS_SEPARATOR);
        data.append(audioPath).append(Constants.SETTINGS_SEPARATOR);
        data.append(videosPath).append(Constants.SETTINGS_SEPARATOR);
        data.append(documentsPath).append(Constants.SETTINGS_SEPARATOR);
        data.append(otherPath);
        FileHandler.writeString("prefs.txt", data.toString());
    }
}
