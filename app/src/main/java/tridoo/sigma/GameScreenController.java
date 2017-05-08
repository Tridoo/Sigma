package tridoo.sigma;


import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Set;

public class GameScreenController {
    private GameActivity activity;
    private TextView source;

    public GameScreenController(GameActivity activity) {
        this.activity = activity;
        source = (TextView)activity.findViewById(R.id.tvSource);
    }

    public void generateEmptyTiles(final int size) {
        final GridLayout gridLayout = (GridLayout) activity.findViewById(R.id.layTiles);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(size);
        gridLayout.setRowCount(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                addTile(gridLayout, i, j);
            }
        }
        equaliseTiles(size, gridLayout);
    }

    private void equaliseTiles(final int size, final GridLayout gridLayout) {
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int margin = activity.getResources().getDimensionPixelSize(R.dimen.margin_tile);
                        int width = (int) ((gridLayout.getWidth() - 2 * size * margin));
                        int height = (int) ((gridLayout.getHeight() - 2 * size * margin));
                        for (int i = 0; i < size * size; i++) {
                            ((Tile) gridLayout.getChildAt(i)).setWidth(width / size);
                            ((Tile) gridLayout.getChildAt(i)).setHeight(height / size);
                        }
                        gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    private void addTile(GridLayout gridLayout, int i, int j) {
        Tile textView = new Tile(activity);
        textView.setBackground(activity.getResources().getDrawable(R.drawable.bg_round));
        textView.setText("   ");
        textView.setTextSize(activity.getResources().getDimension(R.dimen.txt_2));
        textView.setGravity(Gravity.CENTER);
        textView.setPosInGrid(j + 1, i + 1);
        activity.setTileLiseners(textView);

        int margin = activity.getResources().getDimensionPixelSize(R.dimen.margin_tile);
        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams();
        gridParam.setMargins(margin, margin, margin, margin);
        gridParam.setGravity(Gravity.FILL);
        gridLayout.addView(textView, gridParam);
    }

    public void addInitialTiles(int size){
        Random rnd=new Random();
        int x, y, ile=0;
        Tile tile;

        do{
            x=rnd.nextInt(size)+1;
            y=rnd.nextInt(size)+1;
            if(activity.getArraryValues()[x][y]==1) continue;
            tile =activity.getTile(x,y);
            activity.getArraryValues()[x][y]=1;
            if(activity.getNeighbors(tile).size()>1){
                activity.getArraryValues()[x][y]=0;
                activity.clearCheckedPositions();
                continue;
            }
            setTile(tile,1);
            ile++;
            activity.clearCheckedPositions();
        }while(ile< Config.START_QUANTITY);
    }

    public void setTile(Tile aTile, int value){
        int x= aTile.getPosInGrid()[0];
        int y= aTile.getPosInGrid()[1];
        activity.getArraryValues()[x][y]=value;
        if (value==0) aTile.setText("   ");
        else aTile.setText(String.valueOf(value));
        setBackground(aTile,value);
    }

    private void cleanAllTiles(){
        GridLayout gridLayout = (GridLayout) activity.findViewById(R.id.layTiles);
        int number = gridLayout.getChildCount();
        for (int i=0; i<number;i++){
            setTile((Tile)gridLayout.getChildAt(i),0);
        }
    }

    public void setBackground(TextView tile, int points){
        tile.setBackground(activity.getResources().getDrawable(activity.getBackground(points+1)));
    }

    public void cleanTiles(Set<Tile> tiles){
        for(Tile pTile :tiles){
            setTile(pTile,0);
        }
    }

    public void setSource(int liczba){
        source.setText(String.valueOf(liczba));
        setBackground(source,liczba);
    }

    public void setEndButtons(){
        //przycisk udostepniania ustawiany na koncu gry
        ((ImageView)activity.findViewById(R.id.btnExit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.exit();
            }
        });

        ((ImageView)activity.findViewById(R.id.btnRestart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndVisibility(false);
                cleanAllTiles();
                activity.newGame();
            }
        });
    }

    public void setEndVisibility(boolean isVisible){
        RelativeLayout lay=(RelativeLayout) activity.findViewById(R.id.layEndButtons);
        lay.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public TextView getSource() {
        return source;
    }
}
