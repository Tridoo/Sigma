package tridoo.sigma;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static tridoo.sigma.Config.REQUEST_2;


public class MenuActivity extends Activity {
    private DAO dao;
    private String nick,email;
    private boolean isUpdateNick;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context=getApplicationContext();
        setNick();
        setLogoSize6();
        setButtons();
        if (Config.IS_ADS) showAds();
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
        final ImageView btnSave=(ImageView)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNick();
            }
        });

        TextWatcher watcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                btnSave.setImageDrawable(getResources().getDrawable(R.mipmap.save));
            }
        };
        ((EditText)findViewById(R.id.eName)).addTextChangedListener(watcher);
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
        int id = button.getId();
        Intent intent = new Intent(button.getContext(), Maps.activities.get(id));
        if (id != R.id.btnHelp) {
            Bundle bundle = new Bundle();
            bundle.putInt("poziom", Maps.buttonsSize5.contains(id) ? 5 : 6);
            bundle.putBoolean("isTimer", Maps.buttonsTimer.contains(id));
            if (isUpdateNick) {
                bundle.putString("newNick", nick);
                isUpdateNick=false;
            } else bundle.putString("nick", nick);
                bundle.putString("email",email);
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

    private void setNick() {
        nick = getDAO().getNick();
        email = getEmail();
        if (nick.isEmpty()) {
            if (email.contains("@")) {
                nick = email.split("@")[0];
            } else {
                Date dateStart = new Date();
                dateStart.setYear(117);
                dateStart.setMonth(1);
                dateStart.setDate(1);
                nick = "Player" + ((new Date().getTime() - dateStart.getTime()) / 10000);
            }
            isUpdateNick =true;
        }
        ((EditText) findViewById(R.id.eName)).setText(nick);
    }

    private String getEmail(){
        String email = getUserEmail();
        if (email==null){
            email=Utils.getID(this);
        }
        return email;
    }

    private void updateNick() {
        String newNick = ((EditText) findViewById(R.id.eName)).getText().toString();
        if (!isNickCorrect(newNick)) {
            Toast.makeText(context, "Illegal name \nonly letters and numbers \nmin 4 charters long", Toast.LENGTH_LONG).show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            return;
        }
        nick = newNick;
        ((ImageView) findViewById(R.id.btnSave)).setImageDrawable(getResources().getDrawable(R.mipmap.save_off));
        getDAO().setNick(nick);
        isUpdateNick = true;
        Toast.makeText(context, "Save successful", Toast.LENGTH_SHORT).show();
        findViewById(R.id.layMenuMain).requestFocus();
    }

    private boolean isNickCorrect(String nick){
        String regex ="[a-zA-Z\\d\\s_]{4,}";
        return nick.matches(regex);
    }

    public String getUserEmail() {
        AccountManager manager = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, REQUEST_2);
        }
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            return possibleEmails.get(0);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_2: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    setNick();
                } else {
                    // permission denied,
                }
            }
        }
    }

}
