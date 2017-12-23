package tridoo.sigma;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

abstract class GameActivity extends Activity {
    private int maxGlobalScore;
    private String fileName;
    private String userId;
    private String nick;

    protected int maxNumber;
    private int size;
    private int arraryValues[][];
    private boolean checkedPositions[][];
    protected boolean isTimer;
    private boolean isFirstRun;
    private int points;
    private List<Integer> backgrounds;
    private TextView pointsCounter;

    protected Context context;
    protected GameScreenController screenController;
    private FtpTask ftpTask;
    private boolean isBackPressed;
    private DAO dao;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        screenController = new GameScreenController(this);

        context = getApplicationContext();
        generateHeader();
        pointsCounter = (TextView) findViewById(R.id.tvPoints);

        size = getIntent().getIntExtra("level", 1);
        isFirstRun = getIntent().getBooleanExtra("isFirstRun", false);
        userId = getIntent().getStringExtra("userId");
        nick = getIntent().getStringExtra("nick");
        isTimer = getIntent().getBooleanExtra("isTimer", false);
        fileName = Utils.getFtpFileName(size, isTimer);

        backgrounds = Utils.getBackgruonds(size);

        screenController.generateEmptyTiles(size);
        screenController.setEndButtons();
        if(!isFirstRun) getFtpData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.IS_ADS) showAds();
    }

    @Override
    public void onBackPressed() {
        if (ftpTask != null && ftpTask.getStatus() == AsyncTask.Status.RUNNING) {
            showSavingGlobalScore();
            isBackPressed = true;
            return;
        }
        if (points == 0 || isGameOver()) {
            super.onBackPressed();
            return;
        }
        stoperPause();

        Dialog dialog;
        final String[] items = {"Save Score"};
        final boolean[] selectedItems={true};

        final ArrayList<Integer> itemsSelected = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End the game?");
        builder.setMultiChoiceItems(items, selectedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {
                        if (isSelected) {
                            itemsSelected.add(selectedItemId);
                        } else if (itemsSelected.contains(selectedItemId)) {
                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                        }
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0)) {
                            saveScore(points, isTimer);
                        }
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stoperUnpause();
                            }
                        }
                );
        dialog = builder.create();
        dialog.show();
    }

    private void showAds()    {
        AdView mAdView = (AdView) findViewById(R.id.banerGame);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    protected void newGame() {
        points = 0;
        maxNumber = 1;
        arraryValues = new int[size + 2][size + 2];
        checkedPositions = new boolean[size + 2][size + 2];
        screenController.setSource(1);
        screenController.addInitialTiles(size);
        screenController.setEndVisibility(false);
        pointsCounter.setText(String.valueOf(0));
    }

    public int getBackground(int points) {
        int size = backgrounds.size();
        if (points >= size) return backgrounds.get(size - 1);
        else return backgrounds.get(points);
    }

    protected void checkTile(Tile tile, String sValue) {
        boolean isChanges = false;
        int value = Integer.valueOf(sValue);
        int x=tile.getPosInGrid()[0];
        int y=tile.getPosInGrid()[1];
        arraryValues[x][y] = value;
        screenController.setTile(tile,value);
        Set<Tile> neighborsList = getNeighbors(tile);

        if (neighborsList.size() > 2) {
            if (value == maxNumber) maxNumber++;
            screenController.cleanTiles(neighborsList);
            arraryValues[x][y] = value + 1;
            screenController.setTile(tile,value+1);
            int points = Utils.getExtraPoints(value, neighborsList.size());
            addPoints(points);
            isChanges = true;
            extraPointsActions(points);
            addTime(value);
        }
        if (isGameOver()) {
            gameOver();
        } else {
            clearCheckedPositions();
            if (isChanges) checkTile(tile, String.valueOf(value + 1));
            extraBonusesActions();
        }
    }

    public Set<Tile> getNeighbors(Tile tile) {
        int pX = tile.getPosInGrid()[0];
        int pY = tile.getPosInGrid()[1];
        int value = arraryValues[pX][pY];

        checkedPositions[pX][pY] = true;
        Set<Tile> setToChange = new HashSet<>();
        setToChange.add(tile);
        List<Tile> setToCheck = new ArrayList<>();
        List<Tile> tmp = getNeighborsFiltrated(tile, value);
        setToChange.addAll(tmp);
        setToCheck.addAll(tmp);

        ListIterator<Tile> iterator = setToCheck.listIterator();
        while (iterator.hasNext()) {
            Tile pTile = iterator.next();
            tmp = getNeighborsFiltrated(pTile, value);
            if (!tmp.isEmpty()) {
                setToChange.addAll(tmp);
                for (Tile neighbour : tmp) {
                    iterator.add(neighbour);
                    iterator.previous();
                }
            }
        }
        return setToChange;
    }

    private List<Tile> getNeighborsFiltrated(Tile tile, int value) {
        List<Tile> neighbours = new ArrayList<>();
        int pX = tile.getPosInGrid()[0];
        int pY = tile.getPosInGrid()[1];

        for (Point point : Maps.nextTitleCoordinates) {
            if (isAddNeighbor(pX - point.x, pY - point.y, value))
                neighbours.add(getTile(pX - point.x, pY - point.y));
        }
        checkedPositions[pX][pY] = true;
        return neighbours;
    }

    private boolean isAddNeighbor(int x, int y, int value) {
        return (!checkedPositions[x][y] && arraryValues[x][y] == value);
    }

    @Nullable
    public Tile getTile(int x, int y){
        GridLayout gridLayout = (GridLayout) findViewById(R.id.layTiles);
        int tileNumber = gridLayout.getChildCount();
        for (int i = 0; i < tileNumber; i++) {
            Tile tile = (Tile) gridLayout.getChildAt(i);
            if ((tile.getPosInGrid()[0]==x) && (tile.getPosInGrid()[1]==y)) return tile;
        }
        return null;
    }

    public void clearCheckedPositions() {
        checkedPositions = new boolean[size + 2][size + 2];
    }

    private void addPoints(int points){
        this.points +=points;
        pointsCounter.setText(String.valueOf(this.points));
    }

    protected boolean isGameOver() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.layTiles);
        int tileNumber = gridLayout.getChildCount();
        for (int i = 0; i < tileNumber; i++) {
            Tile tile = (Tile) gridLayout.getChildAt(i);
            if (tile.getText().length() == 3) return false;
        }
        return true;
    }

    protected void gameOver() {
        screenController.setEndVisibility(true);
        if (points > 0) {
            saveScore(points, isTimer);
        }
        setShareButton(points);
    }

    private void saveScore(int points, boolean isTimer) {
        saveScoreLocal(points, isTimer);
        if (points > maxGlobalScore) {
            Toast.makeText(getApplicationContext(), "New record !!", Toast.LENGTH_LONG).show();
            maxGlobalScore = points;
            sendScoreToFTP(points);
        }
    }

    private void saveScoreLocal(int points, boolean isTimer) {
        DAO dao = getDao();
        dao.addScore(points, size, isTimer);
        if (points > Config.EDGE_6) dao.setUsableSize6();
    }

    private void sendScoreToFTP(int points) {
        try {
            FtpTaskArgs args = new FtpTaskArgs(this, OperationType.SAVE_SCORE);
            args.setScore(points);
            args.setFileName(fileName);
            args.setUserId(userId);
            args.setNick(nick);
            new FtpTask(args).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setShareButton(final int points) {
        ImageView shareButton=(ImageView)findViewById(R.id.btnShare);
        if (points >= getShareMinimum()) {
            shareButton.setImageResource(R.mipmap.share_on);
            shareButton.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendScore(points);
                    //udostepnijFB(points);
                }
            }));
        } else {
            shareButton.setImageResource(R.mipmap.share_off);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Need " + getShareMinimum() + " points", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendScore(int points){
        if (ContextCompat.checkSelfPermission(context,  Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            share(points);
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "NEED PERMISSION", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Config.REQUEST_SHARE);
        }
    }

    private void share(int points){ // udostepnianie ogolne
        int bmpId;
        bmpId = size == 5 ? R.mipmap.score5 : R.mipmap.score6;

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), bmpId);
        Bitmap mutableBitmap = Bitmap.createBitmap(icon).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(canvas.getHeight()/10);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(points), (canvas.getWidth() / 2f) , (canvas.getHeight() / 1.53f), paint);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, getContentValues());

        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        startActivity(Intent.createChooser(getShareIntent(points,uri), "Share Image"));
    }

    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Game score");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return  values;
    }

    private Intent getShareIntent(int pkt, Uri uri){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, "New score: "+ pkt+" points");
        return  share;
    }

    public void exit(){
        if(ftpTask!=null && ftpTask.getStatus()== AsyncTask.Status.RUNNING) {
            showSavingGlobalScore();
            isBackPressed=true;
            return;
        }
        super.onBackPressed();
    }

    private void udostepnijFB(int punkty) {
/*        //FacebookSdk.sdkInitialize(this);
        if (!ShareDialog.canShow(ShareLinkContent.class)) return;
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        //CallbackManager callbackManager = CallbackManager.Factory.create();
        //ShareDialog shareDialog = new ShareDialog(this);
        //ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        //.setContentTitle("Sigma new score: " + points)
                        //.setContentDescription("desc")
                        //.setImageUrl(Uri.parse("android.resource:tridoo.sigma" + idBmp))
//                        .setContentUrl(Uri.parse(getString(R.string.link))).build();
        //shareDialog.show(linkContent);

        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "books.book")
                .putString("og:title", "A Game of Thrones")
                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                .putString("books:isbn", "0-553-57340-3")
                .build();

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.wynik_5))
                .setUserGenerated(true)
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("books.reads")
                .putObject("book", object)
                .putPhoto("image", photo)
                .build();


        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness:course")
                .setAction(action)
                .build();

        ShareDialog.show(this, content);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Config.REQUEST_SHARE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    share(points);
                } else {
                    // brak uprawnien
                    //todo toast ?
                }
                break;
            }
        }
    }

    public void setMaxGlobalScore(int maxGlobalScore) {
        this.maxGlobalScore = maxGlobalScore;
    }

    public int[][] getArraryValues() {
        return arraryValues;
    }

    public void getFtpData() {
        FtpTaskArgs args = new FtpTaskArgs(this, OperationType.READ_MAX);
        args.setUserId(userId);
        args.setFileName(fileName);
        new FtpTask(args).execute();
    }

    public void showSavingGlobalScore(){
        Toast.makeText(getApplicationContext(), R.string.new_global_score, Toast.LENGTH_LONG).show();
    }

    public void showNetworError(){
        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_LONG).show();
    }

    public void afterFtpTask(){
        if(isBackPressed) super.onBackPressed();
    }

    private DAO getDao() {
        if (dao == null) dao = new DAO(context);
        return dao;
    }

    abstract void generateHeader();

    abstract void stoperPause();

    abstract void stoperUnpause();

    abstract void extraPointsActions(int points);

    abstract void extraBonusesActions();

    abstract void addTime(int value);

    abstract protected void setTileLiseners(Tile tile);

    abstract int getShareMinimum();

}