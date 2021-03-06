package ru.breadntgames.ktulhutamagochi;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    ArrayList<Player> bestPlayers = new ArrayList<>();
    LinearLayout menuLayout;
    FrameLayout frame, recordsLayout;
    ListView l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuLayout = findViewById(R.id.btns_layout);
        recordsLayout = findViewById(R.id.records_layout);
        l = findViewById(R.id.score_list);
        frame = (FrameLayout) recordsLayout.getParent();
        frame.removeAllViews();
        frame.addView(menuLayout);
        preferences = getSharedPreferences("DagonsSonPrefs", MODE_PRIVATE);
        if (!preferences.contains("newPlayer")) {
            SharedPreferences.Editor ed = preferences.edit();
            ed.putBoolean("newPlayer", false);
            for (int i = 0; i < 5; i++) {
                ed.putInt("score" + i, 0);
                ed.putString("name" + i, "---");
            }
            ed.apply();
            Intent intent = new Intent(MainActivity.this, LearnActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            for (int i = 0; i < 5; i++) {
                Player p = new Player(preferences.getInt("score" + i, 0), preferences.getString("name" + i, "---"));
                bestPlayers.add(p);
            }
            Collections.sort(bestPlayers, Player.COMPARE_BY_SCORE);
            ArrayAdapter<Player> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.score_list_view, bestPlayers);
            l.setAdapter(adapter);
        }
        //?????????????? ?????? ?????? ?????????? ?? ???????????????? ????????????????????????
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void viewRecords(View view) {
        frame.removeAllViews();
        frame.addView(recordsLayout);
    }

    public void backToMenu(View v) {
        frame.removeAllViews();
        frame.addView(menuLayout);
    }

    public static class MenuView extends SurfaceView implements SurfaceHolder.Callback {
        //???????????????????? ?????? ???????????? ????????????
        Random random = new Random();
        boolean pause = false, isRunning = false;
        Paint paint = new Paint();

        public MenuView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            getHolder().addCallback(this);

            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isRunning) {
                DrawThread drawThread = new DrawThread(getHolder());
                drawThread.setRunning(true);
                drawThread.start();
                isRunning = true;
            } else
                setPause(false);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            setPause(true);
        }

        class DrawThread extends Thread {

            private boolean running = false;
            private SurfaceHolder surfaceHolder;

            public DrawThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            @Override
            public void run() {
                Canvas canvas;
                //?????????????? ?????? ?????????????????? ?????????????? ???????? ?? ??????????????????
                LinearGradient backgroundColor = new LinearGradient(0, 0, 0, getHeight(), Color.rgb(50, 120, 100), Color.rgb(10, 24, 20), Shader.TileMode.CLAMP);
                LinearGradient bubbleColor = new LinearGradient(0, (float) (getHeight() / 2 - 20), 0, getHeight(), Color.argb(0, 224, 255, 255), Color.argb(255, 0, 255, 255), Shader.TileMode.CLAMP);
                //????????????????
                ArrayList<ru.breadntgames.ktulhutamagochi.DrawView.Bubble> bubbles = new ArrayList<>();
                int time = 100;
                while (running) {
                    //??????????
                    if (!isPause()) {
                        canvas = null;
                        try {
                            canvas = surfaceHolder.lockCanvas(null);
                            if (canvas == null)
                                continue;
                            //???????????? ??????
                            paint.setShader(backgroundColor);
                            canvas.drawPaint(paint);

                            //???????????????????? ??????????????????
                            //?????????????????? ?????????? ????????????????
                            if (random.nextInt(25) == 5) {
                                bubbles.add(new ru.breadntgames.ktulhutamagochi.DrawView.Bubble(random.nextInt(getWidth()), getHeight() + 10, random.nextInt(10) + 10));
                            }
                            //?????????????????? ??????????????????
                            paint.setShader(bubbleColor);
                            int i = 0;
                            while (i < bubbles.size()) {
                                if (bubbles.get(i).y < (float) (getHeight() / 2 - 20)) {
                                    bubbles.remove(i);
                                    continue;
                                }
                                bubbles.get(i).y -= 10;
                                canvas.drawCircle(bubbles.get(i).x, bubbles.get(i).y, bubbles.get(i).r, paint);
                                i++;
                            }
                        } finally {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    try {
                        sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //?????????? ??????????????????
        //???????????????????? ???????? ?????????? ?????????? ????????????????
        class Bubble {
            float x, y, r;

            Bubble(float x, float y, float r) {
                this.x = x;
                this.y = y;
                this.r = r;
            }
        }

        public boolean isPause() {
            return pause;
        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }
    }

    static class Player {
        public int score;
        public String name;

        Player(int score, String name) {
            this.score = score;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + "\n" + score;
        }

        public static final Comparator<Player> COMPARE_BY_SCORE = new Comparator<Player>() {
            @Override
            public int compare(Player pOne, Player pTwo) {
                return pTwo.score - pOne.score;
            }
        };
    }
}