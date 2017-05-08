package tridoo.sigma;


import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Stopwatch extends CountDownTimer {
    private TextView remainingTime;
    private ProgressBar progressBar;
    private float sec;
    private GameTimerActivity activity;

    public Stopwatch(long millisInFuture, long countDownInterval, TextView remainingTime, ProgressBar progressBar, GameTimerActivity activity) {
        super(millisInFuture, countDownInterval);
        this.remainingTime =remainingTime;
        this.progressBar =progressBar;
        this.activity =activity;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        sec=(float)TimeUnit.MILLISECONDS.toMillis(millisUntilFinished)/1000;
        remainingTime.setText(String.format(Locale.US,"%.2f",sec));
        progressBar.setProgress(Config.TIMER_SECONDS -(int)sec);

    }

    @Override
    public void onFinish() {
        remainingTime.setText("0.00");
        activity.gameOver();
    }
}
