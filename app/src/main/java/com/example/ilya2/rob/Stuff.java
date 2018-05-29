package com.example.ilya2.rob;

import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Stuff {
    private ImageView stuff;
    private float x,y;
    int sqX,sqY;
    int type,size;
    boolean opened = false;
    private Square squares[][];
    Stuff (Activity main, int type,Square squares[][]){
        this.squares = squares;
        this.size = Square.size;
        this.type = type;
        int []sqXY=randomSqXY();
        x = MainActivity.squares[sqXY[1]][sqXY[0]].x;sqX=sqXY[0];
        y = MainActivity.squares[sqXY[1]][sqXY[0]].y;sqY=sqXY[1];
        stuff = new ImageView(main);
        stuff.setX(x);
        stuff.setY(y);
        stuff.setImageResource(R.drawable.question);
        main.addContentView(stuff, new RelativeLayout.LayoutParams(size,size));
    }
    void open(){
        switch (type){
            case (0):
                stuff.setImageResource(R.drawable.lightning);
                break;
            case (1):
                stuff.setImageResource(R.drawable.sun);
                break;
        }
        AlphaAnimation anim = new AlphaAnimation(0f,1f);
        anim.setDuration(800);
        stuff.startAnimation(anim);
        opened = true;
    }
    void delete(){
        FrameLayout parent = (FrameLayout) stuff.getParent();
        parent.removeView(stuff);
    }
    int [] randomSqXY(){
        while(true){
            int []sqXY={(int)Math.round(Math.random()*(squares[0].length-1)),
                    (int)Math.round(Math.random()*(squares.length-1))};
            boolean check=true;
            for (Robot robot:MainActivity.robots) {
                if (robot.sqX==sqXY[0] && robot.sqY==sqXY[1])
                    check=false;
            }
            if(check)return sqXY;
        }
    }
}
