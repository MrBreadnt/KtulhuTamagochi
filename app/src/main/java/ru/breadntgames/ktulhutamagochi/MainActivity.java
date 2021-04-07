package ru.breadntgames.ktulhutamagochi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    DrawView draw;
    ProgressBar eatProgress;
    ProgressBar healthProgress;
    ProgressBar happyProgress;
    //это осталось с тестов анимации это не надо но я оставил вдруг пригодится
    /*View.OnTouchListener cl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
            } else if (action == MotionEvent.ACTION_UP) {
            }
            return true;
        }
    };*/

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
}