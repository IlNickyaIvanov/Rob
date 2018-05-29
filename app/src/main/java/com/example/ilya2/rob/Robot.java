package com.example.ilya2.rob;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Robot {
    private float x, y;
    int sqX, sqY,rotation=0,turn=1;
    private float speedX,targetX,speedY,targetY;
    private int speedRotate,targetAngle;
    boolean anim=false;
    private int onTickMove=1000/60;
    ImageView image;
    static int size=50;
    ArrayList<Block> blocks;
    private Queue<int[]> moveXY;
    private CommandParser comPars;
    private Activity activity;

    Robot(Activity main, int sqX, int sqY,int turn,Square squares[][]) {
        this.activity=main;
        this.turn = turn;
        comPars = new CommandParser(squares,sqX,sqY,turn);
        blocks = new ArrayList<>();
        moveXY = new LinkedList<>();
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
        rotation=90*(turn-1);
        image.setRotation(rotation);
    }
    //метод, отвечающй за перемещение
    void RobotMove(final int sqX, final int sqY) {
        anim = true;
        float x=MainActivity.squares[sqY][sqX].x;
        float y=MainActivity.squares[sqY][sqX].y;
        targetX=x;
        targetY=y;
        speedX=(x-this.x)/40;
        speedY=(y-this.y)/40;
        this.sqX = sqX;
        this.sqY = sqY;
        for (Stuff stuff: MainActivity.stuff)
            if(stuff.sqX == sqX && stuff.sqY==sqY)
                stuff.open();

    }

    void startAnim(){
        AlphaAnimation animation = new AlphaAnimation(0f,1f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        image.startAnimation(animation);
    }
    void stopAnim(){
        image.clearAnimation();
    }

    void update(){
        if(anim){
            if(targetX!=0 && targetY!=0) {
                if (Math.round(x) != Math.round(targetX)) x += speedX;
                if (Math.round(y) != Math.round(targetY)) y += speedY;
                if (Math.round(x) == Math.round(targetX) && Math.round(y) == Math.round(targetY)) {
                    anim = false;
                    targetX = 0;
                    targetY = 0;
                }
                image.setX(x);
                image.setY(y);
            }else {
                rotation+=speedRotate;
                image.setRotation(rotation);
                if(Math.round(rotation)==Math.round(targetAngle)){
                    image.setRotation(Math.round(targetAngle));
                    anim=false;
                    speedRotate=0;
                }
            }
        }
    }

    void execute(){
        moveXY = comPars.parser(blocks);
    }
    boolean move(){
        if(moveXY.size()>0){
            int[]xy=moveXY.poll();
            if(xy.length>1)
                RobotMove(xy[0],xy[1]);//moveXY - это очередь в которой заданы положения робота[2] или повороты[1]
            else {
                targetAngle=rotation+xy[0];
                speedRotate=xy[0]/40;
                anim=true;
            }
        }
        if(moveXY.size()==0 && comPars.error!=null){
            Utils.AlertDialog(activity,"Ошибка",comPars.error,"ок");
            comPars.error=null;
        }
        return moveXY.size()==0;
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
