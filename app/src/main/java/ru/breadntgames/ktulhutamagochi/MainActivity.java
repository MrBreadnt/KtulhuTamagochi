package ru.breadntgames.ktulhutamagochi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
        progressBarThread = new Thread(){
            @Override
            public void run() {
                super.run();
                int time = 1000;
                while (true) {
                    if (!draw.isPause()) {
                        healthProgress.setProgress(healthProgress.getProgress() - 1);
                        eatProgress.setProgress(eatProgress.getProgress() - 3);
                        happyProgress.setProgress(happyProgress.getProgress() - 2);
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

    public void setEatProgress(View v){
        eatProgress.setProgress(eatProgress.getProgress() + 10);
    }
    public void setHealthProgress(View v){
        healthProgress.setProgress(healthProgress.getProgress() + 10);
    }
    public void setHappyProgress(View v){
        happyProgress.setProgress(happyProgress.getProgress() + 10);
    }
}