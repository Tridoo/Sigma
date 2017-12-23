package tridoo.sigma;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class FtpTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private String nick,userId, newNick;
    private boolean isWrite, isReadMax, isNickUpdate;
    private int points;
    private List<Score> scores;
    private boolean isNetworkError;
    private FtpTaskArgs ftpArgs;


    public FtpTask(FtpTaskArgs ftpArgs) {
        this.ftpArgs = ftpArgs;
        activity = ftpArgs.getActivity();
    }

    @Deprecated
    public FtpTask(Activity aActivity, boolean isWrite) {
        activity = aActivity;
        if(isWrite) ((GameActivity)activity).showSavingGlobalScore();
    }

    protected String doInBackground(String... args) {
        FTPClient ftp = new FTPClient();
        try {
            ftpConnect(ftp);
            List<Score> scores;

            switch (ftpArgs.getOperationType()) {
                case UPDATE_NICK:
                    updateNick(ftpArgs.getFileNames(), ftpArgs.getUserId(), ftpArgs.getNewNick(), ftp);
                    break;
                case READ_MAX:
                    scores = getScores(ftpArgs.getFileName(), ftp);
                    points = getPlayerScore(ftpArgs.getUserId(), scores);
                    break;
                case READ_SCORE:
                    this.scores = getScores(ftpArgs.getFileName(), ftp);
                    break;
                case SAVE_SCORE:
                    if (backupFile(ftpArgs.getFileName(), ftp)) {
                        scores = getScores(ftpArgs.getFileName(), ftp);
                        addScore(scores, ftpArgs.getUserId(), ftpArgs.getNick(), ftpArgs.getScore());
                        saveFile(scores, ftpArgs.getFileName(), ftp);
                    }
                    break;
            }
            ftp.logout();
            ftp.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            isNetworkError = true;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }

    private void updateNick(List<String> fileNames, String userId, String newNick, FTPClient ftp) {
        List<Score> scores;
        for (String file : fileNames) {
            try {
                backupFile(file, ftp);
                scores = getScores(file, ftp);
                updateNickInList(scores, userId, newNick);
                saveFile(scores, file, ftp);
            } catch (IOException e) {
            }
        }
    }

    private void updateNickInList(List<Score> scores, String userId, String newNick) {
        for (Score score : scores) {
            if (score.userId.equals(userId)) {
                score.nick = newNick;
                return;
            }
        }
    }

    private void ftpConnect(FTPClient ftp) throws IOException{
        String url = activity.getString(R.string.ftp_host);
        String dir = activity.getString(R.string.ftp_dir);
        String login = activity.getString(R.string.ftp_login);
        String pass = activity.getString(R.string.ftp_pass);

        ftp.connect(url);
        int reply = ftp.getReplyCode();
        if(!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        } else {
            ftp.login(login, pass);
            ftp.enterLocalPassiveMode(); // important!
            ftp.changeWorkingDirectory(dir);
        }
        //ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
    }

    private boolean backupFile(String fileName, FTPClient ftp) throws IOException{
        String backupName = getnerateBackupFileName(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ftp.retrieveFile(fileName, outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return ftp.storeFile(backupName, inputStream);
    }

    private boolean saveFile(List<Score> scores, String fileName, FTPClient ftp) throws IOException{
        String scoresStream = convertScores(scores);
        InputStream is = new ByteArrayInputStream(scoresStream.getBytes());
        boolean result = ftp.storeFile(fileName, is);
        is.close();
        return result;
    }

    private void addScore(List<Score> scores, String userId, String nick, int points) {
        for (Score score : scores) {
            if (userId.equals(score.userId)) {
                score.points = points;
                return;
            }
        }
        scores.add(new Score(nick, userId, points));
    }

    private List<Score> getScores(String fileName, FTPClient ftp) throws IOException {
        List<Score> result = new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ftp.retrieveFile(fileName, outputStream);
        List<String> lines = Arrays.asList(outputStream.toString().split("\\r?\\n"));

        for(String line : lines){
            String[] field = line.split(";");
            try {
                result.add(new Score(field[0], field[1], Integer.valueOf(field[2])));
            }
            catch (ArrayIndexOutOfBoundsException e){
                //todo odczytac backup jesli odczyt zawiedzie
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        switch(activity.getClass().getName()){
            case "tridoo.sigma.GlobalScoresActivity":
                postExecuteGlobalScores((GlobalScoresActivity) activity);
                break;
            case "tridoo.sigma.GameTimerActivity":
            case "tridoo.sigma.GameArcadeActivity":
                postExecuteGame((GameActivity) activity);
                break;
            case "tridoo.sigma.MenuActivity":
                postExecuteMenu((MenuActivity) activity, isNetworkError);
                break;

        }
    }

    private void postExecuteGlobalScores(GlobalScoresActivity activity){
        if (isNetworkError) {
            activity.showNetworkError();
            return;
        }

        HashMap<String, Integer> scoresToShow = new HashMap<>();
        for (Score score : this.scores) {
            scoresToShow.put(score.nick, score.points);
        }
        activity.showScores(sortByValues(scoresToShow));
    }

    private void postExecuteGame(GameActivity activity){
        if (isNetworkError) {
            activity.showNetworError();
            return;
        }

        if (ftpArgs.getOperationType() == OperationType.READ_MAX) {
            activity.setMaxGlobalScore(points);
        } else if (ftpArgs.getOperationType() == OperationType.SAVE_SCORE){
            activity.afterFtpTask();
        }
    }

    private void postExecuteMenu(MenuActivity activity, boolean isNetworkError) {
        activity.afterNickUpdated(ftpArgs.getNewNick(), isNetworkError);
    }


    private String convertScores(List<Score> scores) {
        StringBuilder result = new StringBuilder();
        for (Score score : scores) {
            result.append(score.nick);
            result.append(";");
            result.append(score.userId);
            result.append(";");
            result.append(score.points);
            result.append("\r\n");
        }
        return result.toString();
    }

    private int getPlayerScore(String userId, List<Score> scores){
        for (Score score: scores){
            if(userId.equals(score.userId)) return score.points;
        }
        return 0;
    }

    private static HashMap<String,Integer> sortByValues(HashMap<String,Integer> map) {
        List list = new LinkedList<>(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap<String,Integer> sortedHashMap = new LinkedHashMap<>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put((String)entry.getKey(), (Integer) entry.getValue());
        }
        return sortedHashMap;
    }

    private void addScore(String nick, String userId, int points) {
        for (Score score : scores) {
            if (userId.equals(score.userId)) {
                score.points = points;
                return;
            }
        }
        scores.add(new Score(nick, userId, points));
    }

    private void updateNick(String userId, String newNick) {
        for (Score score : scores) {
            if (score.userId.equals(userId)) {
                score.nick = newNick;
                return;
            }
        }
    }

    private String getnerateBackupFileName(String name){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yy_MM_dd_HH_mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        return name.replace(".txt","_")+ date.format(currentLocalTime)+".txt";
    }

    private class Score {
        String nick;
        String userId;
        int points;

        public Score(String nick, String userId, int points) {
            this.nick = nick;
            this.userId = userId;
            this.points = points;
        }
    }

}