package tridoo.sigma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MenuActivity extends Activity {
    private DAO dao;
    private String nick, userId;
    private Context context;
    private EditText eNick;
    private ImageView btnSave;
    private boolean isFirstRun = false;
    private FtpTask ftpTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = getApplicationContext();
        eNick = (EditText) findViewById(R.id.eName);
        btnSave = (ImageView) findViewById(R.id.btnSave);
        userId = Utils.getId(this);
        setLogoSize6();
        setButtons();
        if (Config.SHOW_ADS) showAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNick(); //rozpoznanie pierwszego uruchomienia w tym przy powrocie z innaj aktywnosci
        setSaveButtonEnable(false);
    }

    private void setButtons() {
        setHelpButton();
        setSaveButton();
        setSize5Buttons();
        setSize6Buttons();
    }

    private void setSize6Buttons() {
        for (int id : Maps.buttonsSize6) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isUsableSize6()) {
                        startActivity(view);
                    } else {
                        showLocked6();
                    }
                }
            });
        }
    }

    private void setSize5Buttons() {
        for(int id: Maps.buttonsSize5){
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(view);
                }
            });
        }
    }

    private void setLogoSize6(){
        int idLogo6 = !isUsableSize6() ? R.mipmap.logo6_off : R.mipmap.logo6;
        ((ImageView) findViewById(R.id.ivLogo6)).setImageDrawable(getResources().getDrawable(idLogo6));
    }

    private void setSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNick();
            }
        });

        NickTextWatcher nickTextWatcher = new NickTextWatcher();
        eNick.addTextChangedListener(nickTextWatcher);
    }

    private void setHelpButton() {
        findViewById(R.id.btnHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(view);
            }
        });
    }

    private void startActivity(View button) {
        if (ftpTask != null && ftpTask.getStatus() == AsyncTask.Status.RUNNING) {
            Toast.makeText(context, R.string.ftp_running, Toast.LENGTH_LONG).show();
            return;
        }
        int id = button.getId();
        Intent intent = new Intent(button.getContext(), Maps.activities.get(id));
        if (id != R.id.btnHelp) {
            Bundle bundle = new Bundle();
            bundle.putInt("level", Maps.buttonsSize5.contains(id) ? 5 : 6);
            bundle.putBoolean("isTimer", Maps.buttonsTimer.contains(id));
            bundle.putBoolean("isFirstRun", isFirstRun);
            bundle.putString("nick", nick);
            bundle.putString("userId", userId);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private void showLocked6() {
        Toast.makeText(context, "Need " + Config.EDGE_6 + " points at size 5", Toast.LENGTH_LONG).show();
    }

    private boolean isUsableSize6() {
        return getDAO().isUsableSize6();
    }

    private DAO getDAO() {
        if (dao == null) dao = new DAO(context);
        return dao;
    }

    private void showAds() {
        AdView mAdView = (AdView) findViewById(R.id.banerMenu);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupNick() {
        nick = getDAO().getNick();
        if (nick.isEmpty()) {
            isFirstRun = true;
            String email = Utils.getUserEmail(this, false);
            if (email != null && !email.isEmpty()){
                nick = convertEmailToNick(email);
            } else{
                nick = Utils.generateNick();
            }
            getDAO().setNick(nick);
        } else {
            isFirstRun = false;
        }

        eNick.setText(nick);
    }

    private String convertEmailToNick(String email) {
        return email.contains("@") ? email.split("@")[0] : Utils.generateNick();
    }

    private void updateNick() {
        String newNick = eNick.getText().toString();
        if (!isNickCorrect(newNick)) {
            showNickError();
            return;
        }

        if (newNick.equals(nick)) {
            setSaveButtonEnable(false);
            return;
        }

        if (!isFirstRun) {
            Toast.makeText(context, "Updating", Toast.LENGTH_LONG).show();
            updateNickInGlobalScores(nick, newNick, userId);
        } else {
            afterNickUpdated(newNick, false);
        }
    }

    public void afterNickUpdated(String newNick, boolean isNetworkError) {
        if (isNetworkError){
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            eNick.setText(nick);
         }else {
            nick = newNick;
            getDAO().setNick(nick);
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT).show();
        }
        setSaveButtonEnable(false);
        findViewById(R.id.layMenuMain).requestFocus();
    }

    public void setSaveButtonEnable(boolean isEnable) {
        int mipmapId = isEnable ? R.mipmap.save : R.mipmap.save_off;
        btnSave.setImageDrawable(getResources().getDrawable(mipmapId));
    }

    private void showNickError() {
        Toast.makeText(context, R.string.illegal_name, Toast.LENGTH_LONG).show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private boolean isNickCorrect(String nick){
        String regex ="[a-zA-Z\\d\\s_]{4,}";
        return nick.matches(regex);
    }

    private void updateNickInGlobalScores(String oldNick, String newNick, String userId){
        FtpTaskArgs ftpArgs = new FtpTaskArgs(this, OperationType.UPDATE_NICK);
        ftpArgs.setNick(oldNick);
        ftpArgs.setNewNick(newNick);
        ftpArgs.setUserId(userId);
        ftpArgs.setFileNames(Utils.getAllFtpFileNames());

        ftpTask = new FtpTask(ftpArgs);
        ftpTask.execute();
    }

    private class NickTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {        }

        @Override
        public void afterTextChanged(Editable s) {
            setSaveButtonEnable(!s.toString().equals(nick));
        }
    }

}
