package tridoo.sigma;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps {

    public static final List<Point> nextTitleCoordinates;
    static {
        nextTitleCoordinates = new ArrayList<>();
        nextTitleCoordinates.add(new Point(-1, 0));
        nextTitleCoordinates.add(new Point(1, 0));
        nextTitleCoordinates.add(new Point(0, -1));
        nextTitleCoordinates.add(new Point(0, 1));
    }


    public static final HashMap<Integer,Class> activities;
    static {
        activities=new HashMap<>();
        activities.put(R.id.btnHelp,HelpActivity.class);
        activities.put(R.id.btnArcade5,GameArcadeActivity.class);
        activities.put(R.id.btnTime5,GameTimerActivity.class);
        activities.put(R.id.btnArcadeLocal5,LocalScoresActivity.class);
        activities.put(R.id.btnArcadeGlobal5,GlobalScoresActivity.class);
        activities.put(R.id.btnTimeLocal5,LocalScoresActivity.class);
        activities.put(R.id.btnTimeGlobal5,GlobalScoresActivity.class);

        activities.put(R.id.btnArcade6,GameArcadeActivity.class);
        activities.put(R.id.btnTime6,GameTimerActivity.class);
        activities.put(R.id.btnArcadeLocal6,LocalScoresActivity.class);
        activities.put(R.id.btnArcadeGlobal6,GlobalScoresActivity.class);
        activities.put(R.id.btnTimeLocal6,LocalScoresActivity.class);
        activities.put(R.id.btnTimeGlobal6,GlobalScoresActivity.class);
    }

    public static final List<Integer> buttonsSize5;
    static {
        buttonsSize5 =new ArrayList<>();
        buttonsSize5.add(R.id.btnArcade5);
        buttonsSize5.add(R.id.btnTime5);
        buttonsSize5.add(R.id.btnArcadeLocal5);
        buttonsSize5.add(R.id.btnArcadeGlobal5);
        buttonsSize5.add(R.id.btnTimeLocal5);
        buttonsSize5.add(R.id.btnTimeGlobal5);
    }


    public static final List<Integer> buttonsSize6;
    static{
        buttonsSize6 =new ArrayList<>();
        buttonsSize6.add(R.id.btnArcade6);
        buttonsSize6.add(R.id.btnTime6);
        buttonsSize6.add(R.id.btnArcadeLocal6);
        buttonsSize6.add(R.id.btnTimeLocal6);
        buttonsSize6.add(R.id.btnArcadeGlobal6);
        buttonsSize6.add(R.id.btnTimeGlobal6);
    }

    public static final List<Integer> buttonsTimer;
    static{
        buttonsTimer=new ArrayList<>();
        buttonsTimer.add(R.id.btnTime5);
        buttonsTimer.add(R.id.btnTimeLocal5);
        buttonsTimer.add(R.id.btnTimeGlobal5);
        buttonsTimer.add(R.id.btnTime6);
        buttonsTimer.add(R.id.btnTimeLocal6);
        buttonsTimer.add(R.id.btnTimeGlobal6);
    }

    public static final HashMap<Integer, Integer> sizeId;
    static{
        sizeId=new HashMap<>();
        sizeId.put(5,R.mipmap.size5);
        sizeId.put(6,R.mipmap.size6);
    }

    public static final HashMap<Integer, Integer> clockId;
    static{
        clockId=new HashMap<>();
        clockId.put(5,R.mipmap.clock5);
        clockId.put(6,R.mipmap.clock6);
    }

    public static final HashMap<Integer, Integer> localScoresId;
    static{
        localScoresId=new HashMap<>();
        localScoresId.put(5,R.mipmap.local5);
        localScoresId.put(6,R.mipmap.local6);
    }

    public static final HashMap<Integer, Integer> globalScoresId;
    static{
        globalScoresId=new HashMap<>();
        globalScoresId.put(5,R.mipmap.global5);
        globalScoresId.put(6,R.mipmap.global6);
    }


    public static final HashMap<Bonus.Type, Integer> bonusPoints;
    static{
        bonusPoints=new HashMap<>();
        bonusPoints.put(Bonus.Type.NEW_NUMBER, Config.POINTS_BONUS_1);
        bonusPoints.put(Bonus.Type.BOMB, Config.POINTS_BONUS_2);
        bonusPoints.put(Bonus.Type.MINUS, Config.POINTS_BONUS_3);
        bonusPoints.put(Bonus.Type.PLUS, Config.POINTS_BONUS_4);
    }

    public static final HashMap<Bonus.Type, Integer> bonusImage;
    static{
        bonusImage=new HashMap<>();
        bonusImage.put(Bonus.Type.NEW_NUMBER,R.id.ivBonus1);
        bonusImage.put(Bonus.Type.BOMB,R.id.ivBonus2);
        bonusImage.put(Bonus.Type.MINUS,R.id.ivBonus3);
        bonusImage.put(Bonus.Type.PLUS,R.id.ivBonus4);
    }

    public static final HashMap<Bonus.Type, Integer> bonusImgDisable;
    static{
        bonusImgDisable=new HashMap<>();
        bonusImgDisable.put(Bonus.Type.NEW_NUMBER,R.mipmap.cube_disable);
        bonusImgDisable.put(Bonus.Type.BOMB,R.mipmap.bomb_disable);
        bonusImgDisable.put(Bonus.Type.MINUS,R.mipmap.minus_disable);
        bonusImgDisable.put(Bonus.Type.PLUS,R.mipmap.plus_disable);
    }

    public static final HashMap<Bonus.Type, Integer> bonusImgOn;
    static{
        bonusImgOn=new HashMap<>();
        bonusImgOn.put(Bonus.Type.NEW_NUMBER,R.mipmap.cube_on);
        bonusImgOn.put(Bonus.Type.BOMB,R.mipmap.bomb_on);
        bonusImgOn.put(Bonus.Type.MINUS,R.mipmap.minus_on);
        bonusImgOn.put(Bonus.Type.PLUS,R.mipmap.plus_on);
    }

    public static final HashMap<Bonus.Type, Integer> bonusImgOff;
    static{
        bonusImgOff=new HashMap<>();
        bonusImgOff.put(Bonus.Type.NEW_NUMBER,R.mipmap.cube_off);
        bonusImgOff.put(Bonus.Type.BOMB,R.mipmap.bomb_off);
        bonusImgOff.put(Bonus.Type.MINUS,R.mipmap.minus_off);
        bonusImgOff.put(Bonus.Type.PLUS,R.mipmap.plus_off);
    }

    public static final HashMap<Bonus.Type, Integer> bonusProgresBar;
    static{
        bonusProgresBar=new HashMap<>();
        bonusProgresBar.put(Bonus.Type.NEW_NUMBER,R.id.progressBarB1);
        bonusProgresBar.put(Bonus.Type.BOMB,R.id.progressBarB2);
        bonusProgresBar.put(Bonus.Type.MINUS,R.id.progressBarB3);
        bonusProgresBar.put(Bonus.Type.PLUS,R.id.progressBarB4);
    }

    public static final HashMap<Bonus.Type, Integer> bonusCounter;
    static{
        bonusCounter=new HashMap<>();
        bonusCounter.put(Bonus.Type.NEW_NUMBER,R.id.tvCounterB1);
        bonusCounter.put(Bonus.Type.BOMB,R.id.tvCounterB2);
        bonusCounter.put(Bonus.Type.MINUS,R.id.tvCounterB3);
        bonusCounter.put(Bonus.Type.PLUS,R.id.tvCounterB4);
    }
}
