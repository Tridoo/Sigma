package tridoo.sigma;


import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameArcadeActivity extends GameActivity {
    private int bonusPoints;
    private Bonus.Type activeBonus;
    private List<Bonus> bonuses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bonuses = getBonuses();
        setBonusTouchListeners();

        newGame();
    }

    @Override
    protected void onResume() {
        setProgresBars();
        super.onResume();
    }

    @Override
    protected void newGame() {
        bonusPoints = 0;
        activeBonus = null;
        setProgresBars();
        super.newGame();
    }

    private List<Bonus> getBonuses() {
        List<Bonus> bonuses = new ArrayList<>();
        for (Bonus.Type type : Bonus.Type.values()) {
            Bonus bonus = new Bonus();
            bonus.setType(type);
            bonus.setPoints(Maps.bonusPoints.get(type));
            bonus.setImg((ImageView) findViewById(Maps.bonusImage.get(type)));
            bonus.setIdImgDisable(Maps.bonusImgDisable.get(type));
            bonus.setIdImgOn(Maps.bonusImgOn.get(type));
            bonus.setIdImgOff(Maps.bonusImgOff.get(type));
            bonus.setProgress((ProgressBar) findViewById(Maps.bonusProgresBar.get(type)));
            bonus.setCounter((TextView) findViewById(Maps.bonusCounter.get(type)));
            bonuses.add(bonus);
        }
        return bonuses;
    }

    private void setProgresBars() {
        for (Bonus bonus : bonuses) {
            int progres = (bonusPoints * 100) / bonus.getPoints();
            bonus.getProgress().setProgress(progres);
            bonus.getCounter().setText(progres > 99 ? String.valueOf((int) (progres / 100)) : null);
        }
    }

    private void setBonusTouchListeners() {
        screenController.getSource().setOnTouchListener(new BonusTouchListener());
        for (Bonus bonus : bonuses) {
            bonus.getImg().setOnTouchListener(new BonusTouchListener());
        }
    }

    protected void setTileLiseners(Tile tile) {
        tile.setOnTouchListener(new TileTouchListener());
        tile.setOnDragListener(new TileDragListener());
    }

    @Override
    protected void extraPointsActions(int points) {
        bonusPoints += points;
        animateBonusIcons(points);
    }

    @Override
    protected void extraBonusesActions() {
        checkUsableBonuses();
        setProgresBars();
    }

    private void animateBonusIcons(int points) {
        int pointsBefore = bonusPoints - points;
        for (Bonus bonus : bonuses) {
            if (bonus.getPoints() <= bonusPoints && bonus.getPoints() > pointsBefore) {
                Animation animZoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                bonus.getImg().startAnimation(animZoom);
            }
        }
    }

    private void setBonusIcon(Bonus.Type type) {
        checkUsableBonuses();
        if (type != null) {
            for (Bonus bonus : bonuses) {
                if (bonus.getType() == type)
                    bonus.getImg().setImageDrawable(getResources().getDrawable(bonus.getIdImgOn()));
            }
        }
    }

    private boolean isBonusActive(ImageView view) {
        for (Bonus bonus : bonuses) {
            if (bonus.getImg().getId() == view.getId()) {
                return bonus.getPoints() < bonusPoints;
            }
        }
        return false;
    }

    private Bonus.Type getBonusDraged(ImageView iv) {
        for (Bonus bonus : bonuses) {
            if (bonus.getImg().getId() == iv.getId()) return bonus.getType();
        }
        return null;
    }

    private void executeBonus(Tile tile) {
        if (activeBonus == null) return;

        int value = 0;
        String txt = tile.getText().toString();
        if (!txt.equals("   ")) value = Integer.valueOf(txt);
        int newValue;

        switch (activeBonus) {
            case NEW_NUMBER:
                do {
                    newValue = Utils.getRandomNumber(maxNumber);
                } while (value == newValue);
                screenController.setTile(tile, newValue);
                checkTile(tile, String.valueOf(newValue));
                break;

            case BOMB:
                Set<Tile> setTiles = new HashSet<>();
                setTiles.add(tile);
                screenController.cleanTiles(setTiles);
                break;

            case MINUS:
                if (value == 0) break;
                newValue = value - 1;
                screenController.setTile(tile, newValue);
                if (newValue != 0) checkTile(tile, String.valueOf(newValue));
                break;

            case PLUS:
                newValue = value + 1;
                if (newValue > maxNumber) maxNumber = newValue;
                screenController.setTile(tile, newValue);
                checkTile(tile, String.valueOf(newValue));
                break;
        }

        for (Bonus bonus : bonuses) {
            if (bonus.getType() == activeBonus) {
                bonusPoints -= bonus.getPoints();
                break;
            }
        }
        if (isGameOver()) gameOver();
        activeBonus = null;
        checkUsableBonuses();
        setProgresBars();
    }

    private void checkUsableBonuses() {
        for (Bonus bonus : bonuses) {
            if (bonusPoints >= bonus.getPoints())
                bonus.getImg().setImageDrawable(getResources().getDrawable(bonus.getIdImgOff()));
            else
                bonus.getImg().setImageDrawable(getResources().getDrawable(bonus.getIdImgDisable()));
        }
    }

    @Override
    protected boolean isGameOver() {
        if (bonusPoints > Config.POINTS_BONUS_1) return false;
        return super.isGameOver();
    }

    @Override
    int getShareMinimum() {
        return Config.EDGE_SHARE_ARCADE;
    }

    private class BonusTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);

                for (int i = 0; i < bonuses.size(); i++) {
                    if (view.getId() == bonuses.get(i).getImg().getId()) {
                        if (bonuses.get(i).getPoints() > bonusPoints) activeBonus = null;
                        else {
                            if (activeBonus == null) activeBonus = bonuses.get(i).getType();
                            else activeBonus = null;
                        }
                    }
                }
                setBonusIcon(activeBonus);
                return true;
            } else {
                return false;
            }
        }
    }

    private class TileTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Tile tile = (Tile) v;
                if (activeBonus != null) {
                    executeBonus(tile);
                    setBonusIcon(activeBonus);
                    return true;
                }
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

    private class TileDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (((TextView) v).getText().length() == 3)
                        screenController.setBackground((TextView) v, -1);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (((TextView) v).getText().length() == 3)
                        screenController.setBackground((TextView) v, 0);
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    Tile tile = (Tile) v;
                    if (view instanceof TextView) {
                        TextView tv = (TextView) view;
                        if (tile.getText().length() == 3) {
                            checkTile(tile, tv.getText().toString());
                            screenController.setSource(Utils.getRandomNumber(maxNumber));
                        }
                    } else if (view instanceof ImageView) {
                        if (isBonusActive((ImageView) view)) {
                            if (activeBonus == null) activeBonus = getBonusDraged((ImageView) view);
                            executeBonus(tile);
                        } else {
                            if (tile.getText().toString().equals("   "))
                                screenController.setBackground(tile, 0);
                            else
                                screenController.setTile(tile, Integer.valueOf(tile.getText().toString()));
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

    @Override
    void generateHeader() {    }

    @Override
    void stoperPause() {    }

    @Override
    void stoperUnpause() {    }

    @Override
    void addTime(int value) {    }
}
