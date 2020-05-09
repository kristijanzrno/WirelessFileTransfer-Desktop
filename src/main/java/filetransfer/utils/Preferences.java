package filetransfer.utils;


import filetransfer.Constants;

public class Preferences {
    private static final String default_dir = System.getProperty("user.home") + "/Desktop/Transferred";
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
        if (this.imagesPath.equals("Default"))
            return default_dir + "/Images/";
        else return this.imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String getAudioPath() {
        if (this.audioPath.equals("Default"))
            return default_dir + "/Audio/";
        else return this.audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getVideosPath() {
        if (this.videosPath.equals("Default"))
            return default_dir + "/Videos/";
        else return this.videosPath;
    }

    public void setVideosPath(String videosPath) {
        this.videosPath = videosPath;
    }

    public String getDocumentsPath() {
        if (this.documentsPath.equals("Default"))
            return default_dir + "/Documents/";
        else return this.documentsPath;
    }

    public void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }

    public String getOtherPath() {
        if (this.otherPath.equals("Default"))
            return default_dir + "/Other/";
        else return this.otherPath;
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
