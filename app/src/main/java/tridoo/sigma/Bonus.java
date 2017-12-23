package tridoo.sigma;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Bonus {
    public enum Type {NEW_NUMBER, BOMB, PLUS, MINUS}    ;

    private int points;
    private int idImgOff, idImgOn, idImgDisable;
    private ImageView img;
    private TextView counter;
    private ProgressBar progress;
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getIdImgOff() {
        return idImgOff;
    }

    public void setIdImgOff(int idImgOff) {
        this.idImgOff = idImgOff;
    }

    public int getIdImgOn() {
        return idImgOn;
    }

    public void setIdImgOn(int idImgOn) {
        this.idImgOn = idImgOn;
    }

    public int getIdImgDisable() {
        return idImgDisable;
    }

    public void setIdImgDisable(int idImgDisable) {
        this.idImgDisable = idImgDisable;
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public TextView getCounter() {
        return counter;
    }

    public void setCounter(TextView counter) {
        this.counter = counter;
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public void setProgress(ProgressBar progress) {
        this.progress = progress;
    }
}
