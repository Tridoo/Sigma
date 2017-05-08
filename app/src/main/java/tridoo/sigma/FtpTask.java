package tridoo.sigma;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String nick,email, newNick;
    private boolean isWrite, isReadMax, isNickUpdate;
    private int points;
    private List<Score> scores;
    private boolean isNetworkError;



    public FtpTask(Activity aActivity, boolean isWrite) {
        activity = aActivity;
        if(isWrite) ((GameActivity)activity).showSavingGlobalScore();
    }

    protected String doInBackground(String... args) {
        String url = activity.getString(R.string.ftp_host);
        String dir = activity.getString(R.string.ftp_dir);
        String login = activity.getString(R.string.ftp_login);
        String pass = activity.getString(R.string.ftp_pass);
        String file = args[0];

        processArgs(args);

        FTPClient ftp = new FTPClient();
        try {
            scores = getScores(url, dir, login, pass, file, ftp);

            if(isWrite) { //odczyt i zapis w jednej sesji nie dziala
                saveScores(url, dir, login, pass, file, ftp);
            }

        } catch (IOException e) {
            isNetworkError =true;
            //e.printStackTrace();
        }
        return null;
    }

    private void saveScores(String url, String dir, String login, String pass, String file, FTPClient ftp) throws IOException {
        if(isNickUpdate) {
            updateNick(email, newNick);
        }
        else {
            addScore(nick, email, points);
        }
        String scores = convertScores();

        ftp.connect(url);
        ftp.login(login, pass);
        ftp.enterLocalPassiveMode(); // important!
        ftp.changeWorkingDirectory(dir);
        ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

        String copy= getCopyFileName(file);

        boolean rename = ftp.rename(file, copy);
        InputStream is = new ByteArrayInputStream(scores.getBytes());
        boolean result = ftp.storeFile(file, is);
        is.close();
        ftp.logout();
        ftp.disconnect();
    }

    private List<Score> getScores(String url, String dir, String login, String pass, String file, FTPClient ftp) throws IOException {
        List<Score> scores=new ArrayList<>();
        ftp.connect(url);
        ftp.login(login, pass);
        ftp.enterLocalPassiveMode(); // important!
        ftp.changeWorkingDirectory(dir);
        ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

        InputStream input = ftp.retrieveFileStream(file);
        if (input != null) {
            BufferedReader buffreader = new BufferedReader(new InputStreamReader(input));
            String line;

            while ((line = buffreader.readLine()) != null) {
                String[] field = line.split(";");
                try {
                    scores.add(new Score(field[0], field[1], Integer.valueOf(field[2])));
                }
                catch (ArrayIndexOutOfBoundsException e){
                    //todo odczytac backup jesli odczyt zawiedzie
                }
            }
            buffreader.close();
            input.close();
            ftp.logout();
            ftp.disconnect();
        }
        return scores;
    }

    private void processArgs(String[] args) {
        switch (args.length) {
            case 2:
                email = args[1];
                isReadMax = true;
                break;
            case 3:
                email = args[1];
                newNick = args[2];
                isNickUpdate = true;
                isWrite = true;
                if (activity instanceof GameActivity) isReadMax = true;
                break;
            case 4:
                email = args[1];
                nick = args[2];
                points = Integer.valueOf(args[3]);
                isWrite = true;
                break;
        }
    }


    @Override
    protected void onPostExecute(String s) {
        if (activity instanceof GlobalScoresActivity) {
            if (isNetworkError) {
                ((GlobalScoresActivity) activity).showNetworkError();
                return;
            }

            HashMap<String, Integer> scores = new HashMap<>();
            for (Score score : this.scores) {
                scores.put(score.nick, score.points);
            }
            ((GlobalScoresActivity) activity).showScores(sortByValues(scores));
        }

        if (activity instanceof GameActivity) {
            if (isNetworkError) {
                ((GameActivity) activity).showNetworError();
                return;
            }

            if (isReadMax)
                ((GameActivity) activity).setMaxGlobalScore(getPlayerScore(email));

            if(isWrite) ((GameActivity) activity).afterFtpTask();
        }
    }

    private String convertScores(){
        String result="";
        for (Score score: scores){
            result+=score.nick +";"+score.email+";"+score.points +"\r\n";
        }
        return result;
    }

    private int getPlayerScore(String email){
        for (Score score: scores){
            if(email.equals(score.email)) return score.points;
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

    private void addScore(String nick, String email, int points){
        for (Score score: scores){
            if (email.equals(score.email)){
                score.points =points;
                return;
            }
        }
        scores.add(new Score(nick,email,points));
    }

    private void updateNick(String email, String newNick){
        for (Score score: scores){
            if(score.email.equals(email)) {
                score.nick =newNick;
                return;
            }
        }
    }

    private String getCopyFileName(String name){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("yy_MM_dd_HH_mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        return name.replace(".txt","_")+ date.format(currentLocalTime)+".txt";
    }

    private class Score {
        String nick;
        String email;
        int points;

        public Score(String nick, String email, int points) {
            this.nick = nick;
            this.email = email;
            this.points = points;
        }
    }

}