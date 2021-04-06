package ru.breadntgames.ktulhutamagochi;

//самый костыльный костыль что я делал могу гордиться
public class AnimationManager {
    private int currentFrame = 0;
    private int frames;
    private int time;
    private int currentTime;

    public AnimationManager(int frames, int time) {
        this.frames = frames;
        this.time = time;
    }

    public void setFrames(int frames) {
        if (frames != this.frames)
            currentFrame = 0;
        this.frames = frames;
    }

    public int getFrame(int time) {
        //получение текущего кадра
        currentTime += time;
        if (currentTime >= this.time) {
            currentTime = 0;
            if (currentFrame == frames)
                currentFrame = 0;
            else
                currentFrame++;
        }
        return this.currentFrame;
    }
}
