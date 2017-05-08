package tridoo.sigma;

import android.app.Activity;
import android.provider.Settings;

import java.util.ArrayList;
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


    public static String getID(Activity activity){
        return Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
