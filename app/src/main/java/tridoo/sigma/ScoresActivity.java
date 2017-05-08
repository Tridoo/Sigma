package tridoo.sigma;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public abstract class ScoresActivity extends Activity {
    private int size;
    boolean isTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        size =getIntent().getIntExtra("poziom",1);
        isTimer = getIntent().getBooleanExtra("isTimer", false);
        generateHeader();
        if (Config.IS_ADS) showAds();
    }

    private void showAds(){
        AdView mAdView = (AdView) findViewById(R.id.banerScores);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    protected void generateHeader(){
        ((ImageView)findViewById(R.id.ivSize)).setImageDrawable(getResources().getDrawable(Maps.sizeId.get(size)));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
