package ru.breadntgames.ktulhutamagochi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    //переменные тут всякие разные
    Random random = new Random();
    boolean pause = false, isRunning = false;
    Bitmap[] monster;
    Resources res = this.getResources();
    Paint paint = new Paint();
    AnimationManager monsterAnim;

    public DrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);

        //загрузка анимаций
        monster = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.monster_0),
                BitmapFactory.decodeResource(res, R.drawable.monster_1)
        };
        monsterAnim = new AnimationManager(monster.length, 500);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!isRunning) {
            DrawThread drawThread = new DrawThread(getHolder());
            drawThread.setRunning(true);
            drawThread.start();
            isRunning = true;
        }
        else
            pause = false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause = true;
        /*boolean retry = true;
        drawThread.setRunning(false);
        monsterAnim.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
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
            //шейдеры для рисования заднего фона и пузырьков
            LinearGradient backgroundColor = new LinearGradient(0, 0, 0, getHeight(), Color.rgb(50, 120, 100), Color.rgb(10, 24, 20), Shader.TileMode.CLAMP);
            LinearGradient bubbleColor = new LinearGradient(0, (float) (getHeight() / 2 - 20), 0, getHeight(), Color.argb(0, 224, 255, 255), Color.argb(255, 0, 255, 255), Shader.TileMode.CLAMP);
            //пузырьки
            ArrayList<Bubble> bubbles = new ArrayList<>();
            int time = 100;
            while (running) {
                //пауза
                while (pause){
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    //задний фон
                    paint.setShader(backgroundColor);
                    canvas.drawPaint(paint);

                    //добавление пузырьков
                    //костыльно потом поменять
                    if (random.nextInt(25) == 5) {
                        bubbles.add(new Bubble(random.nextInt(getWidth()), getHeight() + 10, random.nextInt(10) + 10));
                    }
                    //рисование пузырьков
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

                    //отрисовка ктулху
                    //добавить остальные состояния
                    // костыльно потом поменять
                    monsterAnim.setFrames(monster.length - 1);
                    canvas.drawBitmap(monster[monsterAnim.getFrame(time)], (float) (getWidth() / 2 - monster[0].getWidth() / 2), (float) (getHeight() / 2 - monster[0].getHeight() / 2), paint);
                    sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
    //класс пузырьков
    //полкостыля если будет время поменять
    static class Bubble {
        float x, y, r;

        Bubble(float x, float y, float r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
    }
}