package com.example.ilya2.rob;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Bot {
    private float x, y;
    int sqX, sqY;
    private float speedX,targetX,speedY,targetY;
    boolean anim=false;
    private int onTickMove=600/60;
    ImageView image;
    static int size=50;

    Bot(Activity main, int sqX, int sqY) {
        size = Square.size;
        float x = MainActivity.squares[sqX][sqY].x;
        float y = MainActivity.squares[sqX][sqY].y;
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.bad_bot);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x = x;
        this.y = y;
        this.sqX=sqX;
        this.sqY=sqY;
        MyTimer timer = new MyTimer();
        timer.start();
    }

    void hunt(int sqX,int sqY){
        if(Math.random()>=0.5) {
            if (sqX > this.sqX) botMove(this.sqX + 1, this.sqY);
            else if (sqX < this.sqX) botMove(this.sqX - 1, this.sqY);
            else if (sqY > this.sqY) botMove(this.sqX, this.sqY + 1);
            else if (sqY < this.sqY) botMove(this.sqX, this.sqY - 1);
        }
        else {
            if (sqY > this.sqY) botMove(this.sqX, this.sqY + 1);
            else if (sqY < this.sqY) botMove(this.sqX, this.sqY - 1);
            else if (sqX > this.sqX) botMove(this.sqX + 1, this.sqY);
            else if (sqX < this.sqX) botMove(this.sqX - 1, this.sqY);
        }

    }

    //метод, отвечающй за перемещение
    void botMove(final int sqX, final int sqY) {
        anim = true;
        float x=MainActivity.squares[sqY][sqX].x;
        float y=MainActivity.squares[sqY][sqX].y;
        targetX=(x);
        targetY=(y);
        speedX=(x-this.x)/40;
        speedY=(y-this.y)/40;
        this.sqX = sqX;
        this.sqY = sqY;
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
