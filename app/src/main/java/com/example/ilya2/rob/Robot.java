package com.example.ilya2.rob;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Robot {
    private float x, y;
    int sqX, sqY;
    private float speedX,targetX,speedY,targetY;
    boolean anim=false;
    private int onTickMove=1000/60;
    ImageView image;
    static int size=50;
    private Animation alpha;

    Robot(Activity main, int sqX, int sqY) {
        size = Square.size;
        image = new ImageView(main);
        float x = MainActivity.squares[sqY][sqX].x;
        float y = MainActivity.squares[sqY][sqX].y;
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.robo);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x=x;
        this.y=y;
        this.sqX=sqX;
        this.sqY=sqY;
        MyTimer timer = new MyTimer();
        timer.start();
        alpha = AnimationUtils.loadAnimation(main, R.anim.alpha);
    }
    //метод, отвечающй за перемещение
    void RobotMove(final int sqY, final int sqX) {
        anim = true;
        float x=MainActivity.squares[sqY][sqX].x;
        float y=MainActivity.squares[sqY][sqX].y;
        targetX=x;
        targetY=y;
        speedX=(x-this.x)/40;
        speedY=(y-this.y)/40;
        this.sqX = sqX;
        this.sqY = sqY;
    }

    void startAnim(){
        image.startAnimation(alpha);
    }
    void stopAnim(){
        image.clearAnimation();
    }

    void update(){
        if(anim){
            if(Math.round(x)!=Math.round(targetX))x+=speedX;
            if(Math.round(y)!=Math.round(targetY))y+=speedY;
            if(Math.round(x)==Math.round(targetX) && Math.round(y)==Math.round(targetY)) anim=false;
            image.setX(x);
            image.setY(y);
        }
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, onTickMove);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }
        @Override
        public void onFinish() {
        }
    }

}