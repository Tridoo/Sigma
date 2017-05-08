package tridoo.sigma;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class LocalScoresActivity extends ScoresActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DAO dao = new DAO(getApplicationContext());
        showScores(dao.getScores(getSize(), isTimer));
    }

    private void showScores(List<Integer> scores) {
        Collections.reverse(scores);
        GridLayout scoreTable = (GridLayout) findViewById(R.id.gridScores);
        scoreTable.setColumnCount(2);
        float txtSize = getResources().getDimension(R.dimen.txt_1);
        int counter = 1;

        for (Integer score : scores) {
            addScore(counter, score, scoreTable, txtSize);
            counter++;
        }
    }

    private void addScore(int counter, int score, GridLayout scoreTable, float txtSize) {
        TextView tvPosition = new TextView(this);
        tvPosition.setText(String.format(Locale.US, "%d.", counter));
        tvPosition.setTextSize(txtSize);

        TextView tvScore = new TextView(this);
        tvScore.setText(String.valueOf(score));
        tvScore.setTextSize(txtSize);
        tvScore.setPadding((int) txtSize, (int) (txtSize * 0.5), 0, (int) (txtSize * 0.5));

        tvPosition.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(counter, GridLayout.CENTER), GridLayout.spec(0, GridLayout.END)));
        tvScore.setLayoutParams(new GridLayout.LayoutParams(GridLayout.spec(counter, GridLayout.CENTER), GridLayout.spec(1, GridLayout.START)));

        scoreTable.addView(tvPosition);
        scoreTable.addView(tvScore);
    }


    @Override
    protected void generateHeader() {
        super.generateHeader();
        if (isTimer) {
            ((ImageView) findViewById(R.id.ivClock)).setImageDrawable(getResources().getDrawable(Maps.clockId.get(getSize())));
        }
        ((ImageView) findViewById(R.id.ivMode)).setImageDrawable(getResources().getDrawable(Maps.localScoresId.get(getSize())));
    }
}
