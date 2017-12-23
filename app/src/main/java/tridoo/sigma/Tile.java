package tridoo.sigma;

import android.content.Context;

public class Tile extends android.support.v7.widget.AppCompatTextView {
    public Tile(Context context) {
        super(context);
    }

    int xPos, yPos;

    public void setPosInGrid(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public int[] getPosInGrid() {
        int[] pos = new int[2];
        pos[0] = xPos;
        pos[1] = yPos;
        return pos;
    }
}
