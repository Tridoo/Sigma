package tridoo.sigma;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DAO {
    private Context context;

    public DAO(Context aContext) {
        context = aContext;
    }

    public List<Integer> getScores(int size, boolean isTimer) {
        String jStringName = getSharedPreferencesName(size,isTimer);

        List<Integer> scores = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jString = prefs.getString(jStringName, "");
        try {
            JSONArray jScores = new JSONArray(jString);
            for (int i = 0; i < jScores.length(); i++) {
                scores.add(jScores.getInt(i));
            }

        } catch (JSONException e) {
            //brak zapisanych wynikow
        }
        return scores;
    }

    private void saveScores(List<Integer> scores, int size, boolean isTimer) {
        String jStringName = getSharedPreferencesName(size, isTimer);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jScores = new JSONArray();

        for (Integer score : scores) {
            jScores.put(score);
        }

        editor.putString(jStringName, jScores.toString());
        editor.apply();
    }

    public void addScore(int score, int size, boolean isTimer) {
        List<Integer> scores = getScores(size, isTimer);
        scores.add(score);
        Collections.sort(scores);
        saveScores(scores, size, isTimer);
    }

    public boolean isUsableSize6(){
        String jStringName = "czy6";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getBoolean(jStringName, false);
    }

    public void setUsableSize6(){
        String jStringName = "czy6";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(jStringName, true);
        editor.apply();
    }

    public String getNick(){
        String jStringName = "xywa";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getString(jStringName,"");
    }

    public void setNick(String nick){
        String jStringName = "xywa";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(jStringName, nick);
        editor.apply();
    }

    private String getSharedPreferencesName(int size, boolean isTimer){
        String name="";

        switch (size) {
            case 5:
                name= "wyniki5";
                break;
            case 6:
                name ="wyniki6";
                break;
        }

        if(isTimer) name+="_timer";
        return name;
    }
}
