package com.example.ilya2.rob;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Robot {
    float x, y;
    int sqX, sqY;
    private int onTickMove=1000;
    private int BUSY;
    private boolean pause=true;
    ImageView image;
    static int size=50;

    Robot(Activity main, float x, float y) {
        size = Square.size;
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.robo);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x = x;
        this.y = y;
    }
    //метод, отвечающй за перемещение
    void RobotMove(final float y, final float x, final int sqY, final int sqX,boolean isBUSY) {
//        TranslateAnimation anim = new TranslateAnimation(this.x,x,this.y,y);
//        anim.setDuration(1000);
//        anim.setFillAfter(true);
//
//        image.startAnimation(anim);
//        anim.hasEnded();

        image.setX(x);
        image.setY(y);
        this.x = x;
        this.y = y;
        this.sqX = sqX;
        this.sqY = sqY;
    }
    //для обновления координат
    void MoveMySelf(boolean isBUSY){
        int n=0;
        if (isBUSY) n=BUSY;
//        TranslateAnimation moveMySelf = new TranslateAnimation(this.x, this.x, this.y, this.y);
//        moveMySelf.setDuration(onTickMove);
//        moveMySelf.setFillAfter(true);
//        image.startAnimation(moveMySelf);
//        moveMySelf.hasEnded();
    }

}
