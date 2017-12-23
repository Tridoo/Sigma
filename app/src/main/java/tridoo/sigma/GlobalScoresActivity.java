package tridoo.sigma;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class GlobalScoresActivity extends ScoresActivity {
    private FtpTask ftpTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressVisibility(true);
        String file = Utils.getFtpFileName(size, isTimer);

        FtpTaskArgs args = new FtpTaskArgs(this, OperationType.READ_SCORE);
        args.setFileName(file);

        ftpTask = new FtpTask(args);
        ftpTask.execute();
    }


    protected void showScores(HashMap<String, Integer> scores) {
        GridLayout scoreTable = (GridLayout) findViewById(R.id.gridScores);
        scoreTable.setColumnCount(3);
        float txtSize = getResources().getDimension(R.dimen.txt_1);
        int counter = 1;

        for (Map.Entry<String, Integer> score : scores.entrySet()) {
            addScore(scoreTable, txtSize, counter, score);
            counter++;
            if (counter>300) break; //max 350
        }
        setProgressVisibility(false);
    }

    private void addScore(GridLayout scoreTable, float txtSize, int counter, Map.Entry<String, Integer> scores) {


        TextView tvPosition = new TextView(this);
        tvPosition.setText(String.format(Locale.US,"%d.  ", counter));
        tvPosition.setTextSize(txtSize);

        TextView tvNick = new TextView(this);
        tvNick.setText(scores.getKey());
        tvNick.setTextSize(txtSize);

        TextView tvScore = new TextView(this);
        tvScore.setText(String.valueOf(scores.getValue()));
        tvScore.setTextSize(txtSize);
        tvScore.setPadding((int)txtSize,(int)(txtSize*0.5),0,(int)(txtSize*0.5));

        tvPosition.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(counter, GridLayout.CENTER), GridLayout.spec(0, GridLayout.END)));
        tvNick.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(counter, GridLayout.CENTER), GridLayout.spec(1, GridLayout.CENTER)));
        tvScore.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(counter, GridLayout.CENTER), GridLayout.spec(2, GridLayout.START)));

        scoreTable.addView(tvPosition);
        scoreTable.addView(tvNick);
        scoreTable.addView(tvScore);

        if (scores.getKey().equals(nick)) {
            tvPosition.setTextColor(Color.GREEN);
            tvNick.setTextColor(Color.GREEN);
            tvScore.setTextColor(Color.GREEN);
        }
    }

    private void setProgressVisibility(boolean visible){
        findViewById(R.id.progresScore).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void showNetworkError(){
        setProgressVisibility(false);
        TextView txt=new TextView(this);
        txt.setText(R.string.error);
        txt.setTextSize(getResources().getDimension(R.dimen.txt_3));
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        ((LinearLayout)findViewById(R.id.layScores)).addView(txt,2);
    }

    @Override
    protected void generateHeader() {
        super.generateHeader();
        if (isTimer) ((ImageView)findViewById(R.id.ivClock)).setImageDrawable(getResources().getDrawable(Maps.clockId.get(getSize())));
        ((ImageView)findViewById(R.id.ivMode)).setImageDrawable(getResources().getDrawable(Maps.globalScoresId.get(getSize())));
    }

    @Override
    public void onBackPressed() {
        if(!ftpTask.getStatus().equals(AsyncTask.Status.FINISHED)) ftpTask.cancel(true);
        super.onBackPressed();
    }
}