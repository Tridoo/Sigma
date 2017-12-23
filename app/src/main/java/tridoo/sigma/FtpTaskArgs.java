package tridoo.sigma;


import android.app.Activity;

import java.util.List;

public class FtpTaskArgs {
    private Activity activity;
    private OperationType operationType;
    private String nick;
    private String newNick;
    private String userId;
    private int score;
    private String fileName;
    private List<String> fileNames;

    public FtpTaskArgs(Activity activity, OperationType operationType) {
        this.activity = activity;
        this.operationType = operationType;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNewNick() {
        return newNick;
    }

    public void setNewNick(String newNick) {
        this.newNick = newNick;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
