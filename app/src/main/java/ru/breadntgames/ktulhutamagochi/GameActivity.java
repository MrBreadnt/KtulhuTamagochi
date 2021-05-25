package ru.breadntgames.ktulhutamagochi;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    DrawView draw;
    public static int progress = 0;
    static SharedPreferences preferences;
    ProgressBar eatProgress;
    ProgressBar healthProgress;
    ProgressBar happyProgress;
    ProgressBar levelProgress;
    Thread progressBarThread;
    MediaPlayer mediaPlayer;
    FrameLayout gameLayout;
    LinearLayout uiLayout;
    MinigameView minigame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        //убирает вот эту штуку с кнопками раздражающую
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        minigame = new MinigameView(getApplicationContext(), false);
        draw = findViewById(R.id.draw);
        gameLayout = findViewById(R.id.game_layout);
        uiLayout = findViewById(R.id.ui_layout);

        levelProgress = findViewById(R.id.level_progress);
        eatProgress = findViewById(R.id.eat_progress);
        healthProgress = findViewById(R.id.health_progress);
        happyProgress = findViewById(R.id.happy_progress);
        //пока нормаьную мелодию не напишу в комменте будет
        /*mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);*/
        levelProgress.setProgress(progress);
        progressBarThread = new Thread() {
            @Override
            public void run() {
                super.run();
                int time = 1000;
                while (true) {
                    /*if (minigame.isFinished) {
                        closeMinigame();
                    }*/
                    if (!draw.isPause()) {
                        levelProgress.setProgress(progress);
                        eatProgress.setProgress(eatProgress.getProgress() - 3);
                        happyProgress.setProgress(happyProgress.getProgress() - 2);
                        healthProgress.setProgress(healthProgress.getProgress() + 1);
                        if (eatProgress.getProgress() <= 25 || healthProgress.getProgress() <= 25 || happyProgress.getProgress() <= 25)
                            draw.setState(0);
                        else if (eatProgress.getProgress() <= 60 || healthProgress.getProgress() <= 60 || happyProgress.getProgress() <= 60)
                            draw.setState(1);
                        else
                            draw.setState(2);
                        if (eatProgress.getProgress() <= 0 || healthProgress.getProgress() <= 0 || happyProgress.getProgress() <= 0) {
                            gameOver();
                            break;
                        }
                    }
                    try {
                        sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        progressBarThread.start();
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

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        draw.setPause(true);
    }

    void gameOver() {
    }

    @Override
    public void onBackPressed() {
        minigame.boundsPaint = !minigame.boundsPaint;
    }

    public void setEatProgress(View v) {
        if (!draw.isPause())
            startMinigame();
        //eatProgress.setProgress(eatProgress.getProgress() + 10);
    }

    public void setHealthProgress(View v) {
        if (!draw.isPause())
            startMinigame();
        //healthProgress.setProgress(healthProgress.getProgress() + 10);
    }

    public void setHappyProgress(View v) {
        if (!draw.isPause())
            startMinigame();
        //happyProgress.setProgress(happyProgress.getProgress() + 10);
    }

    public void setPaused(View v) {
        draw.setPause(!draw.isPause());
    }

    public void startMinigame() {
        draw.setPause(true);
        draw.setVisibility(View.INVISIBLE);
        uiLayout.setVisibility(View.INVISIBLE);
        minigame = new MinigameView(getApplicationContext(), new Runnable() {
            @Override
            public void run() {

            }
        }, draw.state);
        gameLayout.addView(minigame);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!minigame.isFinished) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eatProgress.setProgress(eatProgress.getProgress() + minigame.foodInt);
                        healthProgress.setProgress(healthProgress.getProgress() + minigame.healthInt);
                        happyProgress.setProgress(happyProgress.getProgress() + minigame.happyInt);
                        closeMinigame();
                    }
                });
            }
        }.start();
    }

    public void closeMinigame() {
        draw.setPause(false);
        draw.setVisibility(View.VISIBLE);
        uiLayout.setVisibility(View.VISIBLE);
        gameLayout.removeAllViews();
        gameLayout.addView(draw);
        progress += happyProgress.getProgress() * 0.01 * 100;
    }
}
