package com.example.ilya2.rob;

import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Stuff {
    private ImageView stuff;
    private float x,y;
    int sqX,sqY;
    int type,size;
    boolean opened = false;
    static private Square squares[][];
    Stuff (Activity main, int type,Square squares[][]){
        this.squares = squares;
        this.size = Square.size;
        this.type = type;
        int []sqXY=randomSqXY();
        x = GameActivity.squares[sqXY[1]][sqXY[0]].x;sqX=sqXY[0];
        y = GameActivity.squares[sqXY[1]][sqXY[0]].y;sqY=sqXY[1];
        stuff = new ImageView(main);
        stuff.setX(x);
        stuff.setY(y);
        stuff.setImageResource(R.drawable.question);
        main.addContentView(stuff, new RelativeLayout.LayoutParams(size,size));
    }
    Stuff(Activity activity, ArrayList<ArrayList<Square>> map){
        this.size = Square.size;
        int []sqXY=SettingsActivity.randomSqXY();
        x = map.get(sqXY[1]).get(sqXY[0]).x;
        y = map.get(sqXY[1]).get(sqXY[0]).y;
        stuff = new ImageView(activity);
        stuff.setX(x);
        stuff.setY(y);
        stuff.setImageResource(R.drawable.question);
        activity.addContentView(stuff,new RelativeLayout.LayoutParams(size,size));

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
        opened = true;
        try{
            FrameLayout parent = (FrameLayout) stuff.getParent();
            parent.removeView(stuff);
        }catch (Throwable t){}
    }
    static int [] randomSqXY(){
        while(true){
            int []sqXY={(int)Math.round(Math.random()*(GameActivity.map[0].length-1)),
                    (int)Math.round(Math.random()*(GameActivity.map[0].length-1))};
            boolean check=true;
            for (Robot robot: GameActivity.robots)
                if (robot.sqX==sqXY[0] && robot.sqY==sqXY[1])
                    check=false;
            if(GameActivity.hunter.sqX==sqXY[0]&& GameActivity.hunter.sqY==sqXY[1])
                check=false;
            if(GameActivity.map[sqXY[1]][sqXY[0]]==2)
                check=false;
            if(check)return sqXY;
        }
    }
    void setXY(int x, int y){
        sqX = x;
        sqY = y;
        this.x = GameActivity.squares[sqY][sqX].x;
        this.y = GameActivity.squares[sqY][sqX].y;
        stuff.setX(this.x);
        stuff.setY(this.y);
    }
}
