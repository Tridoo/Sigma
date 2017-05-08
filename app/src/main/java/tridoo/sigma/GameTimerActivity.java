package tridoo.sigma;

import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Locale;


public class GameTimerActivity extends GameActivity {
    private boolean isTimeActiv, isGameOver;
    private TextView remainingTime;
    private ProgressBar progressBar;
    private TextSwitcher extraTime;
    private Stopwatch stopwatch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTimer =true;
        screenController.getSource().setOnTouchListener(new SourceTouchListener());
        newGame();
    }

    @Override
    protected void generateHeader() {
        ((LinearLayout)findViewById(R.id.layPoints)).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,3f));

        LinearLayout header=(LinearLayout)findViewById(R.id.layHeader);
        header.removeViews(1,4);//bonusy

        LinearLayout layProgres=new LinearLayout(context);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f);
        int margin=(int)getResources().getDimension(R.dimen.margin_3);
        layParams.setMargins(margin/2,0,margin/2,0);
        layProgres.setLayoutParams(layParams);
        layProgres.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layTime=new LinearLayout(context);
        layTime.setLayoutParams(layParams);
        layTime.setOrientation(LinearLayout.HORIZONTAL);

        generateProgressBar();
        generateRemainingTime(layParams);
        generateExtraTime();
        addExtraTimeAnimation();

        layTime.addView(extraTime);
        layTime.addView(remainingTime);
        layProgres.addView(layTime);
        layProgres.addView(progressBar);
        header.addView(layProgres);
    }

    private void generateExtraTime() {
        extraTime =new TextSwitcher(context);
        extraTime.setFactory(mFactory);
        extraTime.setCurrentText(String.valueOf(""));
        extraTime.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2f));
    }

    private void generateRemainingTime(LinearLayout.LayoutParams layParams) {
        remainingTime =new TextView(context);
        remainingTime.setTextSize(getResources().getDimension(R.dimen.txt_2));
        remainingTime.setTextColor(Color.BLACK);
        remainingTime.setGravity(Gravity.START);
        remainingTime.setLayoutParams(layParams);
    }

    private void generateProgressBar() {
        progressBar=new ProgressBar(context,    null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(Config.TIMER_SECONDS);
    }


    protected void newGame() {
        super.newGame();
        progressBar.setProgress(0);
        remainingTime.setText(String.format(Locale.US, "%.2f", (float) Config.TIMER_SECONDS));
        isTimeActiv = false;
        isGameOver = false;
        stopwatch = new Stopwatch(Config.TIMER_SECONDS * 1000, 50, remainingTime, progressBar, this);
    }

    @Override
    protected void gameOver(){
        super.gameOver();
        stopwatch.cancel();
        isGameOver =true;
    }

    @Override
    protected boolean isGameOver() {
        return (isGameOver || super.isGameOver());
    }

    protected void setTileLiseners(Tile tile){
        tile.setOnTouchListener(new TileTouchListener());
        tile.setOnDragListener(new TileDragListener());
    }

    @Override
    int getShareMinimum() {
        return Config.EDGE_SHARE_TIMER;
    }

    @Override
    protected void addTime(int value) {
        extraTime.setText("+" + value);
        stopwatch.cancel();
        long time = (long) (Float.valueOf(remainingTime.getText().toString()) * 1000);
        stopwatch = new Stopwatch(time + value * 1000, 50, remainingTime, progressBar, this);
        stopwatch.start();
    }

    @Override
    protected void stoperPause() {
        stopwatch.cancel();
        isTimeActiv =false;
    }

    @Override
    protected void stoperUnpause() {
        stopwatch =new Stopwatch((long)(Float.valueOf(remainingTime.getText().toString())*1000), 50, remainingTime,progressBar,this);
        stopwatch.start();
        isTimeActiv =true;
    }

    private final class TileDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (isGameOver) return true;
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (((TextView) v).getText().length()==3) screenController.setBackground((TextView) v,-1);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (((TextView) v).getText().length()==3) screenController.setBackground((TextView) v,0);
                    break;
                case DragEvent.ACTION_DROP:
                    if (!isTimeActiv) {
                        isTimeActiv =true;
                        stopwatch.start();
                    }
                    View view = (View) event.getLocalState();
                    Tile tile = (Tile) v;
                    if (view instanceof TextView) {
                        TextView tv = (TextView) view;
                        if (tile.getText().length() == 3) {
                            checkTile(tile, tv.getText().toString());
                            screenController.setSource(Utils.getRandomNumber(maxNumber));
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    }

    private final class TileTouchListener implements  View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isGameOver) return true;
            if (!isTimeActiv) {
                isTimeActiv =true;
                stopwatch.start();
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN){
                Tile tile=(Tile)v;
                if (tile.getText().length() == 3) {
                    String value = screenController.getSource().getText().toString();
                    tile.setText(value);
                    checkTile(tile, value);
                    screenController.setSource(Utils.getRandomNumber(maxNumber));
                    return true;
                }
            }
            return false;
        }
    }

    private final class SourceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER |Gravity.BOTTOM);
            tv.setTextColor(Color.GREEN);
            tv.setTextSize(getResources().getDimension(R.dimen.txt_2));
            return tv;
        }
    };

    private void addExtraTimeAnimation(){
        Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        blink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {                            }
            @Override
            public void onAnimationEnd(Animation animation) {  extraTime.setCurrentText("");          }
            @Override
            public void onAnimationRepeat(Animation animation) {            }
        });

        extraTime.setInAnimation(blink);
    }

    @Override
    void extraPointsActions(int points) {    }

    @Override
    void extraBonusesActions() {}
}
