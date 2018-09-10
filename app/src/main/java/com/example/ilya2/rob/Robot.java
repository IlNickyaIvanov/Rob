package com.example.ilya2.rob;

import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Robot {

    boolean broken=false;//сломан ли робот

    private float x, y;
    int sqX, sqY,direction;
    private float speedX,targetX,speedY,targetY;
    private int speedRotate,targetAngle,rotation;
    boolean anim=false;
    ImageView image;
    static int size=50;
    private Queue<int[]> moveXY;
    private CommandParser comPars;
    private Activity activity;

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
        this.x=x;targetX=x;
        this.y=y;targetY=y;
        this.sqX=sqX;
        this.sqY=sqY;
        direction = turn;
        rotation=90*(turn-1);
        targetAngle=rotation;
        image.setRotation(rotation);
    }
    Robot(Activity main, int sqX, int sqY,int turn,ArrayList<ArrayList<Square>> map) {
        this.activity=main;
        size = Square.size;
        image = new ImageView(main);
        float x = map.get(sqY).get(sqX).x;
        float y = map.get(sqY).get(sqX).y;
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.robo);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x=x;
        this.y=y;
        rotation=90*(turn-1);
        image.setRotation(rotation);
    }
    //метод, отвечающй за перемещение
    void RobotMove(final int sqX, final int sqY) {
        if(sqX== GameActivity.hunter.sqX&&sqY== GameActivity.hunter.sqY) {
            moveXY.clear();
            setBroken(true);
            GameActivity.hunter.findNewActiveRobot();
        }
        float x= GameActivity.squares[sqY][sqX].x;
        float y= GameActivity.squares[sqY][sqX].y;
        if(x!=this.x || y!=this.y)anim = true;
        targetX=x;
        targetY=y;
        speedX=(x-this.x)/ 40;
        speedY=(y-this.y)/ 40;
        this.sqX = sqX;
        this.sqY = sqY;

        boolean gameOver=true;
        for (int i=0;i<GameActivity.stuff.size();++i){
            if (GameActivity.stuff.get(i).sqX == sqX && GameActivity.stuff.get(i).sqY == sqY && !GameActivity.stuff.get(i).opened) {
                if(GameActivity.stuff.get(i).type==1)GameActivity.comLim+=1;
                else {
                    setBroken(true);
                    GameActivity.hunter.findNewActiveRobot();
                }
                GameActivity.stuff.get(i).open();
            }
            gameOver=gameOver&&GameActivity.stuff.get(i).opened;
        }
        if(!broken)for(int i=0;i<GameActivity.robots.length;++i)
            if(GameActivity.robots[i].broken && sqX==GameActivity.robots[i].sqX&&sqY==GameActivity.robots[i].sqY) {
                GameActivity.robots[i].setBroken(false);
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
        if(image!=null)image.clearAnimation();
    }

    void update(){
        if(anim){
            if(targetX!=x || targetY!=y) {
                if (Math.round(x) != Math.round(targetX)) x += speedX;
                if (Math.round(y) != Math.round(targetY)) y += speedY;
                if (Math.round(x) == Math.round(targetX) && Math.round(y) == Math.round(targetY)) {
                    anim = !(rotation == targetAngle);
                    x = targetX;
                    y = targetY;
                }
                image.setX(x);
                image.setY(y);
            }
            if(targetAngle!=rotation){
                rotation+=speedRotate;
                image.setRotation(rotation);
                if((rotation>=targetAngle && speedRotate>0)||(rotation<=targetAngle && speedRotate<0)){
                    rotation = targetAngle;
                    image.setRotation(targetAngle);
                    anim=!(x == targetX && y == targetY);
                    speedRotate=0;
                }
            }
        }
    }

    void execute(ArrayList<Block> blocks){
        moveXY = comPars.parser(blocks);
    }
    //этот метод вызывается из игры
    boolean move(){
        if(!broken && moveXY.size()>0 && !anim){
            int[]xy=moveXY.poll();
            if(xy.length>1)
                RobotMove(xy[0],xy[1]);//moveXY - это очередь в которой заданы положения робота[2] или повороты[1]
            else {
                image.setRotation(targetAngle);
                direction = comPars.turn;
                targetAngle+=xy[0];
                speedRotate=xy[0]/ 40;
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
            image = null;
        }catch (Throwable t){}
    }
    void setBroken(boolean isBrok){
        if(isBrok){
            broken = true;
            setAlpha(0.5f);
        }else{
            broken = false;
            setAlpha(1.0f);
        }
    }
}
