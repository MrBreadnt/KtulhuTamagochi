package ru.breadntgames.ktulhutamagochi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Random;


public class MinigameView extends SurfaceView implements SurfaceHolder.Callback {
    //переменные тут всякие разные
    static Random random = new Random();
    boolean pause = false, isRunning = false, isFinished = false, ifFinished = true, boundsPaint = false;
    ArrayList<Fish> fishArray = new ArrayList<>();
    Bitmap[] shark;
    Bitmap[] food;
    Bitmap[] monster;

    ProgressBar eatProgressMinigame;
    ProgressBar healthProgressMinigame;
    ProgressBar happyProgressMinigame;

    public int foodInt = 0, healthInt = 0, happyInt = 0;
    Player player;
    Resources res = this.getResources();
    Paint paint = new Paint();
    AnimationManager monsterAnim;
    Runnable closeOperation;

    public MinigameView(Context context, Runnable closeOperation, int state) {
        super(context);
        getHolder().addCallback(this);
        this.closeOperation = closeOperation;
        //загрузка анимаций
        shark = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.shark_0),
                BitmapFactory.decodeResource(res, R.drawable.shark_1)
        };
        for (int i = 0; i < shark.length; i++) {
            shark[i] = Bitmap.createScaledBitmap(shark[i], (int) (shark[i].getWidth() * 0.75), (int) (shark[i].getHeight() * 0.75), false);
        }
        food = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.shark_0),
                BitmapFactory.decodeResource(res, R.drawable.shark_1)
        };
        for (int i = 0; i < food.length; i++) {
            food[i] = Bitmap.createScaledBitmap(food[i], (int) (food[i].getWidth() * 0.5), (int) (food[i].getHeight() * 0.5), false);

            int[] srcPixels = new int[food[i].getWidth() * food[i].getHeight()];
            food[i].getPixels(srcPixels, 0, food[i].getWidth(), 0, 0, food[i].getWidth(), food[i].getHeight());
            int[] destPixels = new int[food[i].getWidth() * food[i].getHeight()];
            swapRB(srcPixels, destPixels);
            food[i].setPixels(destPixels, 0, food[i].getWidth(), 0, 0, food[i].getWidth(), food[i].getHeight());
        }
        switch (state) {
            case 2:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.normal_0),
                        BitmapFactory.decodeResource(res, R.drawable.normal_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
                break;
            case 1:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.alert_0),
                        BitmapFactory.decodeResource(res, R.drawable.alert_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
                break;
            case 0:
            default:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.monster_0),
                        BitmapFactory.decodeResource(res, R.drawable.monster_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
        }
        eatProgressMinigame = findViewById(R.id.eat_progress_minigame);
        healthProgressMinigame = findViewById(R.id.health_progress_minigame);
        happyProgressMinigame = findViewById(R.id.happy_progress_minigame);
        paint.setStyle(Paint.Style.STROKE);
    }

    public MinigameView(Context context, boolean isFinished) {
        super(context);
        this.ifFinished = isFinished;
        getHolder().addCallback(this);
        int state = 0;
        //загрузка анимаций
        shark = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.shark_0),
                BitmapFactory.decodeResource(res, R.drawable.shark_1)
        };
        for (int i = 0; i < shark.length; i++) {
            shark[i] = Bitmap.createScaledBitmap(shark[i], (int) (shark[i].getWidth() * 0.75), (int) (shark[i].getHeight() * 0.75), false);
        }
        food = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.shark_0),
                BitmapFactory.decodeResource(res, R.drawable.shark_1)
        };
        for (int i = 0; i < food.length; i++) {
            food[i] = Bitmap.createScaledBitmap(food[i], (int) (food[i].getWidth() * 0.5), (int) (food[i].getHeight() * 0.5), false);

            int[] srcPixels = new int[food[i].getWidth() * food[i].getHeight()];
            food[i].getPixels(srcPixels, 0, food[i].getWidth(), 0, 0, food[i].getWidth(), food[i].getHeight());
            int[] destPixels = new int[food[i].getWidth() * food[i].getHeight()];
            swapRB(srcPixels, destPixels);
            food[i].setPixels(destPixels, 0, food[i].getWidth(), 0, 0, food[i].getWidth(), food[i].getHeight());
        }
        switch (state) {
            case 2:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.normal_0),
                        BitmapFactory.decodeResource(res, R.drawable.normal_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
                break;
            case 1:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.alert_0),
                        BitmapFactory.decodeResource(res, R.drawable.alert_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
                break;
            case 0:
            default:
                monster = new Bitmap[]{
                        BitmapFactory.decodeResource(res, R.drawable.monster_0),
                        BitmapFactory.decodeResource(res, R.drawable.monster_1)
                };
                for (int i = 0; i < monster.length; i++) {
                    monster[i] = Bitmap.createScaledBitmap(monster[i], (int) (monster[i].getWidth() * 0.75), (int) (monster[i].getHeight() * 0.75), false);
                }
        }
        paint.setStyle(Paint.Style.STROKE);
    }

    void swapRB(int[] src, int[] dest) {
        for (int i = 0; i < src.length; i++) {
            dest[i] = (src[i] & 0xff00ff00) | ((src[i] & 0x000000ff) << 16)
                    | ((src[i] & 0x00ff0000) >> 16);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isRunning) {
            DrawThread drawThread = new DrawThread(getHolder());
            for (int i = 150; i < getHeight() - 150; i += 150) {
                for (int j = 0; j < random.nextInt(2) + 1; j++) {
                    boolean isAggressive = random.nextBoolean();
                    boolean right = random.nextBoolean();
                    Bitmap[] texture = isAggressive ? shark : food;
                    fishArray.add(new Fish(random.nextInt(getWidth() - texture[0].getWidth()), i, (float) (random.nextInt(5) + 7.5), right ? 1 : -1, isAggressive, texture));
                }
            }
            player = new Player((float) (getWidth() * 0.5 - monster[0].getWidth() * 0.5), 0, 10, monster);
            drawThread.setRunning(true);
            drawThread.start();
            isRunning = true;
        } else
            setPause(false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setPause(true);
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
                if (!isPause()) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        //задний фон
                        paint.setColor(Color.WHITE);
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
                        for (Fish f : fishArray) {
                            f.x += f.speed * f.dir;
                            if (f.dir == 1) {
                                if (f.x >= getWidth() - f.texture[0].getWidth())
                                    f.changeDir();
                            } else {
                                if (f.x <= 0)
                                    f.changeDir();
                            }
                            paint.setColor(Color.RED);
                            paint.setShader(null);
                            f.manager.setFrames(f.texture.length - 1);
                            canvas.drawBitmap(f.texture[f.manager.getFrame(time)], f.x, f.y, paint);
                            if (boundsPaint)
                                canvas.drawRect(f.getBounds().leftUp.x, f.getBounds().leftUp.y, f.getBounds().rightDown.x, f.getBounds().rightDown.y, paint);
                        }
                        i = 0;
                        while (i < fishArray.size()) {
                            if (Rectangle.isIntersection(fishArray.get(i).getBounds(), player.getBounds())) {
                                if (fishArray.get(i).isAggressive) {
                                    healthInt -= 3;
                                } else {
                                    foodInt += 2;
                                }
                                happyInt += 1;
                                fishArray.remove(i);
                                continue;
                            }
                            i++;
                        }

                        //отрисовка ктулху
                        player.manager.setFrames(player.texture.length - 1);
                        canvas.drawBitmap(player.texture[player.manager.getFrame(time)], player.x, player.y, paint);
                        player.y += player.speed;
                        if (player.y > getHeight() - player.texture[0].getHeight() * 0.5)
                            end();
                        if (boundsPaint)
                            canvas.drawRect(player.getBounds().leftUp.x, player.getBounds().leftUp.y, player.getBounds().rightDown.x, player.getBounds().rightDown.y, paint);
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

    void end() {
        isFinished = ifFinished;
        isRunning = false;
    }

    //класс пузырьков
    static class Bubble {
        float x, y, r;

        Bubble(float x, float y, float r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }
    }

    class Fish {
        public float x, y, speed;
        boolean isAggressive;
        public int dir;
        public Bitmap[] texture;
        public AnimationManager manager;

        Fish(float x, float y, float speed, int dir, boolean isAggressive, Bitmap[] texture) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.dir = dir;
            this.isAggressive = isAggressive;
            this.texture = texture.clone();
            manager = new AnimationManager(texture.length, random.nextInt(50) + 275);
            if (dir == -1) {
                for (int i = 0; i < this.texture.length; i++) {
                    this.texture[i] = Bitmap.createScaledBitmap(this.texture[i], this.texture[i].getWidth() * -1, this.texture[i].getHeight(), false);
                }
            }
        }

        public Rectangle getBounds() {
            float smooth = (float) (texture[0].getWidth() * 0.1);
            return new Rectangle(new Point((int) (x + smooth), (int) (y + smooth)), new Point((int) (x + texture[0].getWidth() - smooth), (int) (y + texture[0].getHeight() - smooth)));
        }

        public void changeDir() {
            dir = -dir;
            for (int i = 0; i < texture.length; i++) {
                texture[i] = Bitmap.createScaledBitmap(texture[i], texture[i].getWidth() * -1, texture[i].getHeight(), false);
            }
        }
    }

    static class Player {
        public float x, y, speed;
        public Bitmap[] texture;
        public AnimationManager manager;

        Player(float x, float y, float speed, Bitmap[] texture) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.texture = texture.clone();
            manager = new AnimationManager(texture.length, 500);
        }

        public Rectangle getBounds() {
            float smooth = (float) (texture[0].getWidth() * 0.1);
            return new Rectangle(new Point((int) (x + smooth), (int) (y + smooth)), new Point((int) (x + texture[0].getWidth() - smooth), (int) (y + texture[0].getHeight() - smooth)));
        }
    }

    static class Rectangle {
        public Point leftUp;
        public Point rightDown;

        Rectangle(Point leftUp, Point rightDown) {
            this.leftUp = leftUp;
            this.rightDown = rightDown;
        }

        static boolean isIntersection(Rectangle a, Rectangle b) {
            return ((a.leftUp.x < b.leftUp.x && (a.rightDown.x) > b.leftUp.x) || (a.leftUp.x > b.leftUp.x && a.leftUp.x < (b.rightDown.x))) &&
                    ((a.leftUp.y < b.leftUp.y && (a.rightDown.y) > b.leftUp.y) || (a.leftUp.y > b.leftUp.y && a.leftUp.y < (b.rightDown.y)));
        }

        int getWidth() {
            return rightDown.x - leftUp.x;
        }

        int getHeight() {
            return leftUp.y - rightDown.y;
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (player.x < getWidth() && player.x + monster[0].getWidth() > 0) {
            if (event.getX() > getWidth() * 0.5) player.x += player.speed * 0.5;
            else player.x -= player.speed * 0.5;
        }
        return true;
    }
}