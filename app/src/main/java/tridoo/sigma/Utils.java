package tridoo.sigma;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Utils {
    static List<Integer> getBackgruonds(int size) {
        boolean isSize6 = size == 6;

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.bg_round99);
        list.add(R.drawable.bg_round);
        list.add(R.drawable.bg_round1);
        if (isSize6) list.add(R.drawable.bg_round2);
        list.add(R.drawable.bg_round3);
        if (isSize6) list.add(R.drawable.bg_round4);
        list.add(R.drawable.bg_round5);
        if (isSize6) list.add(R.drawable.bg_round6);
        list.add(R.drawable.bg_round7);
        if (isSize6) list.add(R.drawable.bg_round8);
        list.add(R.drawable.bg_round9);
        if (isSize6) list.add(R.drawable.bg_round10);
        list.add(R.drawable.bg_round11);
        if (isSize6) list.add(R.drawable.bg_round12);
        list.add(R.drawable.bg_round13);
        if (isSize6) list.add(R.drawable.bg_round14);
        list.add(R.drawable.bg_round15);
        if (isSize6) list.add(R.drawable.bg_round16);
        list.add(R.drawable.bg_round17);
        list.add(R.drawable.bg_round18);
        if (isSize6) list.add(R.drawable.bg_round19);
        list.add(R.drawable.bg_round20);
        if (isSize6) list.add(R.drawable.bg_round21);
        list.add(R.drawable.bg_round22);

        return list;
    }

    static int getRandomNumber(int max) {
        int wielkoscTablicy = 0;

        for (int i = 1; i <= max; i++) {
            wielkoscTablicy += i;
        }
        Random rnd = new Random();
        int x = rnd.nextInt(wielkoscTablicy) + 1;

        int tmp = 0;
        for (int i = 0; i < max; i++) {
            tmp += (max - i);
            if (x <= tmp) return i + 1;
        }
        return 1;
    }

    static int getExtraPoints(int value, int tilesNumber) {
        return (int) (value * tilesNumber * Math.pow(1.15, value - 1) * (1.1 * (tilesNumber - 2)));
    }


    public static String getUserEmail(Activity activity, boolean showoRequest) {
        String email;
        Context context = activity.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                email = getEmailFromAccount(context);
            } else {
                email = getId(activity);
            }
        } else {
            email = getEmailFromAccount(context);
        }
        return email;
    }


    public static String getId(Activity activity) {
        return Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static String getEmailFromAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");

        if (accounts.length > 0) {
            return accounts[0].name;
        } else return null;
    }

    public static String getFtpFileName(int size, boolean isTimer) {
        StringBuilder builder = new StringBuilder("AA_scores_");
        builder.append(size == 5 ? "5_" : "6_");
        builder.append(isTimer ? "t" : "a");
        builder.append(".txt");
        return builder.toString();
    }

    public static List<String> getAllFtpFileNames() {
        List<String> result = new ArrayList<>(4);
        result.add(getFtpFileName(5, false));
        result.add(getFtpFileName(5, true));
        result.add(getFtpFileName(6, false));
        result.add(getFtpFileName(6, true));
        return result;
    }

    public static String generateNick(){
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2017, 11, 22);

        long diffInMilis = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        long diff = diffInMilis / (60 * 10000); //co 10 min

        return "Player" + diff;
    }

}
