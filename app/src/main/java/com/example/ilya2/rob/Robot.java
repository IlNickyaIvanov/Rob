package com.example.ilya2.rob;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Robot {

    boolean broken=false;//сломан ли робот

    private float x, y;
    int sqX, sqY;
    private float speedX,targetX,speedY,targetY;
    private int speedRotate,targetAngle,rotation;
    boolean anim=false;
    private int onTickMove=1000/60;
    ImageView image;
    static int size=50;
    private Queue<int[]> moveXY;
    private CommandParser comPars;
    private Activity activity;
    private final int speed=40;

    Robot(Activity main, int sqX, int sqY,int turn,Square squares[][]) {
        this.activity=main;
        comPars = new CommandParser(squares,sqX,sqY,turn);
        moveXY = new ArrayDeque<>();
        size = Square.size;
        image = new ImageView(main);
        float x = GameActivity.squares[sqY][sqX].x;
        float y = GameActivity.squares[sqY][sqX].y;
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
        targetAngle=rotation;
        image.setRotation(rotation);
    }
    //метод, отвечающй за перемещение
    void RobotMove(final int sqX, final int sqY) {
        if(sqX== GameActivity.hunter.sqX&&sqY== GameActivity.hunter.sqY) {
            moveXY.clear();
            broken = true;
            setAlpha(0.5f);
            GameActivity.hunter.findNewActiveRobot();
        }
        anim = true;
        float x= GameActivity.squares[sqY][sqX].x;
        float y= GameActivity.squares[sqY][sqX].y;
        targetX=x;
        targetY=y;
        speedX=(x-this.x)/speed;
        speedY=(y-this.y)/speed;
        this.sqX = sqX;
        this.sqY = sqY;

        boolean gameOver=true;
        for (int i=0;i<GameActivity.stuff.length;++i){
            if (GameActivity.stuff[i].sqX == sqX && GameActivity.stuff[i].sqY == sqY && !GameActivity.stuff[i].opened) {
                GameActivity.comLim+=1;
                GameActivity.stuff[i].open();
            }
            gameOver=gameOver&&GameActivity.stuff[i].opened;
        }
        for(int i=0;i<GameActivity.robots.length;++i)
            if(GameActivity.robots[i].broken && sqX==GameActivity.robots[i].sqX&&sqY==GameActivity.robots[i].sqY) {
                GameActivity.robots[i].broken = false;
                GameActivity.robots[i].setAlpha(1f);
            }
        if(gameOver && !GameActivity.gameOver && !Tutorial.isTutorial){
            Utils.AlertDialog(activity,"Конец игры","Вы победили","Еще раз");
            GameActivity.gameOver=true;
        }
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
        if(anim && !GameActivity.gameOver){
            if(targetX!=0 && targetY!=0) {
                if (Math.round(x) != Math.round(targetX)) x += speedX;
                if (Math.round(y) != Math.round(targetY)) y += speedY;
                if (Math.round(x) == Math.round(targetX) && Math.round(y) == Math.round(targetY)) {
                    anim = false;
                    x = targetX;
                    y = targetY;
                    targetX = 0;
                    targetY = 0;
                }
                image.setX(x);
                image.setY(y);
            }
            if(targetAngle!=rotation){
                rotation+=speedRotate;
                image.setRotation(rotation);
                if(rotation==targetAngle){
                    rotation = 90*(targetAngle/90);
                    image.setRotation(targetAngle);
                    anim=false;
                    speedRotate=0;
                }
            }
        }
    }

    void execute(ArrayList<Block> blocks){
        moveXY = comPars.parser(blocks);
    }
    boolean move(){
        if(!broken && moveXY.size()>0){
            int[]xy=moveXY.poll();
            if(xy.length>1)
                RobotMove(xy[0],xy[1]);//moveXY - это очередь в которой заданы положения робота[2] или повороты[1]
            else {
                image.setRotation(90*(targetAngle/90));
                targetAngle=rotation+xy[0];
                speedRotate=xy[0]/speed;
                anim=true;
            }
        }
        return broken || moveXY.size()==0;
    }
    void setAlpha(float alpha){
        image.setAlpha(alpha);
    }
    void delete(){
        stopAnim();
        broken = true;
        try{
            FrameLayout parent = (FrameLayout) image.getParent();
            parent.removeView(image);
        }catch (Throwable t){}
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
