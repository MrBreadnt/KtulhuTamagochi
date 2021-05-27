package ru.breadntgames.ktulhutamagochi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {
    DrawView draw;
    public int progress = 0, score = 0, level = 1;
    static SharedPreferences preferences;
    ProgressBar eatProgress;
    ProgressBar healthProgress;
    ProgressBar happyProgress;
    ProgressBar levelProgress;
    ProgressBar eatProgressMinigame;
    ProgressBar healthProgressMinigame;
    ProgressBar happyProgressMinigame;
    CustomProgressBar eatProgressBar, healthProgressBar, happyProgressBar;
    Thread progressBarThread;
    MediaPlayer mediaPlayer;
    FrameLayout gameLayout;
    LinearLayout uiLayout, minigameUiLayout;
    MinigameView minigame;
    ArrayList<MainActivity.Player> bestPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        //убирает вот эту штуку с кнопками раздражающую
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        preferences = getSharedPreferences("DagonsSonPrefs", MODE_PRIVATE);

        for (int i = 0; i < 5; i++) {
            MainActivity.Player p = new MainActivity.Player(preferences.getInt("score" + i, 0), preferences.getString("name" + i, "---"));
            bestPlayers.add(p);
        }
        Collections.sort(bestPlayers, MainActivity.Player.COMPARE_BY_SCORE);

        minigame = new MinigameView(getApplicationContext(), false);
        draw = findViewById(R.id.draw);
        gameLayout = findViewById(R.id.game_layout);
        uiLayout = findViewById(R.id.ui_layout);
        minigameUiLayout = findViewById(R.id.minigame_ui_layout);
        minigameUiLayout.setVisibility(View.INVISIBLE);

        levelProgress = findViewById(R.id.level_progress);
        eatProgress = findViewById(R.id.eat_progress);
        healthProgress = findViewById(R.id.health_progress);
        happyProgress = findViewById(R.id.happy_progress);

        eatProgressMinigame = findViewById(R.id.eat_progress_minigame);
        healthProgressMinigame = findViewById(R.id.health_progress_minigame);
        happyProgressMinigame = findViewById(R.id.happy_progress_minigame);

        eatProgressBar = new CustomProgressBar(1000, 50, -1);
        healthProgressBar = new CustomProgressBar(5000, 50, 1);
        happyProgressBar = new CustomProgressBar(2000, 50, -1);
        //пока нормаьную мелодию не напишу в комменте будет
        /*mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);*/
        levelProgress.setProgress(progress);

        draw.setLevel(level);

        progressBarThread = new Thread() {
            @Override
            public void run() {
                super.run();
                int time = 500;
                while (true) {
                    /*if (minigame.isFinished) {
                        closeMinigame();
                    }*/
                    if (!draw.isPause()) {
                        if (eatProgress.getProgress() <= 0 || healthProgress.getProgress() <= 0 || happyProgress.getProgress() <= 0) {
                            gameOver();
                            break;
                        }
                        levelProgress.setProgress(progress);

                        if (levelProgress.getProgress() == levelProgress.getMax()) {
                            level++;
                            progress = 0;
                            switch (level) {
                                case 1:
                                    levelProgress.setMax(1000);
                                    break;
                                case 2:
                                default:
                                    levelProgress.setMax(2000);
                            }
                            TextView t = findViewById(R.id.level_text);
                            t.setText("Level " + level);
                            draw.setLevel(level);
                        }

                        eatProgressBar.addProgress(time);
                        healthProgressBar.addProgress(time);
                        happyProgressBar.addProgress(time);

                        healthProgressBar.time = 50 * (100 - happyProgressBar.progress);

                        eatProgress.setProgress(eatProgressBar.getProgress());
                        healthProgress.setProgress(healthProgressBar.getProgress());
                        happyProgress.setProgress(happyProgressBar.getProgress());

                        eatProgressMinigame.setProgress(eatProgressBar.getProgress());
                        healthProgressMinigame.setProgress(healthProgressBar.getProgress());
                        happyProgressMinigame.setProgress(happyProgressBar.getProgress());
                        if (eatProgressBar.getProgress() <= 25 || healthProgressBar.getProgress() <= 25 || happyProgressBar.getProgress() <= 25)
                            draw.setState(0);
                        else if (eatProgressBar.getProgress() <= 60 || healthProgressBar.getProgress() <= 60 || happyProgressBar.getProgress() <= 60)
                            draw.setState(1);
                        else
                            draw.setState(2);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.gameover_activity);
                TextView t = findViewById(R.id.score_view);
                t.setText("Счет: " + score);
            }
        });
    }

    public void saveScore(View v) {
        for (int i = 0; i < bestPlayers.size(); i++) {
            if (score > bestPlayers.get(i).score) {
                EditText name = findViewById(R.id.name);
                bestPlayers.add(i, new MainActivity.Player(score, name.getText().toString()));
                bestPlayers.remove(bestPlayers.size() - 1);
                SharedPreferences.Editor ed = preferences.edit();
                for (int j = 0; j < bestPlayers.size(); j++) {
                    ed.putInt("score" + j, bestPlayers.get(j).score);
                    ed.putString("name" + j, bestPlayers.get(j).name);
                }
                ed.apply();
                break;
            }
        }
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
        //draw.setPause(true);
        draw.setVisibility(View.INVISIBLE);
        uiLayout.setVisibility(View.INVISIBLE);
        minigameUiLayout.setVisibility(View.VISIBLE);
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
                        eatProgressBar.addIntProgress(minigame.foodInt);
                        healthProgressBar.addIntProgress(minigame.healthInt);
                        happyProgressBar.addIntProgress(minigame.happyInt);
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
        minigameUiLayout.setVisibility(View.INVISIBLE);
        gameLayout.removeAllViews();
        gameLayout.addView(draw);
        progress += happyProgress.getProgress() * 0.01 * 100;
        score += happyProgress.getProgress() * 0.01 * 100;
    }

    static class CustomProgressBar {
        int time, progress, currentTime = 0, d;

        CustomProgressBar(int time, int progress, int d) {
            this.time = time;
            this.progress = progress;
            this.d = d;
        }

        public void addProgress(int time) {
            currentTime += time;
            if (currentTime >= this.time) {
                currentTime = 0;
                progress += d;
            }
            if (this.progress > 100)
                this.progress = 100;
        }

        public void addIntProgress(int progress) {
            this.progress += progress;
            if (this.progress > 100)
                this.progress = 100;
        }

        public int getProgress() {
            return this.progress;
        }
    }
}
