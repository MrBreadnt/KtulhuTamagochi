package ru.breadntgames.ktulhutamagochi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    DrawView draw;
    ProgressBar eatProgress;
    ProgressBar healthProgress;
    ProgressBar happyProgress;
    Thread progressBarThread;
    MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //убирает вот эту штуку с кнопками раздражающую
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        draw = findViewById(R.id.draw);
        eatProgress = findViewById(R.id.eat_progress);
        healthProgress = findViewById(R.id.health_progress);
        happyProgress = findViewById(R.id.happy_progress);
        //пока нормаьную мелодию не напишу в комменте будет
        /*mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);*/
        progressBarThread = new Thread() {
            @Override
            public void run() {
                super.run();
                int time = 1000;
                while (true) {
                    if (!draw.isPause()) {
                        healthProgress.setProgress(healthProgress.getProgress() - 1);
                        eatProgress.setProgress(eatProgress.getProgress() - 3);
                        happyProgress.setProgress(happyProgress.getProgress() - 2);
                        if (eatProgress.getProgress() <= 25 || healthProgress.getProgress() <= 25 || happyProgress.getProgress() <= 25)
                            draw.setState(0);
                        else if (eatProgress.getProgress() <= 60 || healthProgress.getProgress() <= 60 || happyProgress.getProgress() <= 60)
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

    public void setEatProgress(View v) {
        if (!draw.isPause())
            eatProgress.setProgress(eatProgress.getProgress() + 10);
    }

    public void setHealthProgress(View v) {
        if (!draw.isPause())
            healthProgress.setProgress(healthProgress.getProgress() + 10);
    }

    public void setHappyProgress(View v) {
        if (!draw.isPause())
            happyProgress.setProgress(happyProgress.getProgress() + 10);
    }

    public void setPaused(View v) {
        draw.setPause(!draw.isPause());
    }

}