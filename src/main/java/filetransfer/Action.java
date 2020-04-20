package filetransfer;


public class Action {
    private String action;
    private String message;
    private String filePath;

    public Action(String action, String message, String filePath) {
        this.action = action;
        this.message = message;
        this.filePath = filePath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
