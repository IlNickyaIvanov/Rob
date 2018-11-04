package com.example.ilya2.rob;

import android.app.Activity;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Stuff {
    ImageView stuff;
    private float x,y;
    int sqX=-1,sqY=-1;
    int type,size;
    boolean opened = false;
    Stuff (Activity main, float type){
        this.size = Square.size;
        this.type = (type>0.1)?1:0;//баланс 10%, что в стафе будет молния
        int []sqXY=randomSqXY();
        x = GameActivity.squares[sqXY[1]][sqXY[0]].x;sqX=sqXY[0];
        y = GameActivity.squares[sqXY[1]][sqXY[0]].y;sqY=sqXY[1];
        stuff = new ImageView(main);
        stuff.setX(x);
        stuff.setY(y);
        stuff.setImageResource(R.drawable.question);
        main.addContentView(stuff, new RelativeLayout.LayoutParams(size,size));
    }
    Stuff(Activity activity, ArrayList<ArrayList<Square>> map, int sqX,int sqY){
        this.size = Square.size;
        if(sqX==-1 && sqY==-1) {
            int[] sqXY = SettingsActivity.randomSqXY();
            x = map.get(sqXY[1]).get(sqXY[0]).x;this.sqX = sqXY[0];
            y = map.get(sqXY[1]).get(sqXY[0]).y;this.sqY = sqXY[1];
            map.get(sqXY[1]).get(sqXY[0]).ID_NUMBER=3;
        }else{
            x = map.get(sqY).get(sqX).x;this.sqX=sqX;
            y = map.get(sqY).get(sqX).y;this.sqY=sqY;
        }
        stuff = new ImageView(activity);
        stuff.setX(x);
        stuff.setY(y);
        stuff.setImageResource(R.drawable.question);
        activity.addContentView(stuff,new RelativeLayout.LayoutParams(size,size));
        type = (Math.random()>0.1)?1:0;
    }
    Stuff(Activity activity, int sqX, int sqY,int type){
        this.size = Square.size;
        if(GameActivity.squares!=null) {
            x = GameActivity.squares[sqY][sqX].x;
            y = GameActivity.squares[sqY][sqX].y;
        }
        else {
            x = SettingsActivity.map.get(sqY).get(sqX).x;
            y = SettingsActivity.map.get(sqY).get(sqX).y;
        }
        this.sqX=sqX;
        this.sqY=sqY;
        this.type = type;
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
        opened = true;
    }
    void close(){
        stuff.setImageResource(R.drawable.question);
        opened = false;
    }
    void delete(){
        opened = true;
        try{
            FrameLayout parent = (FrameLayout) stuff.getParent();
            parent.removeView(stuff);
        }catch (Throwable ignored){}
    }
    static int [] randomSqXY(){
        while(true){
            int []sqXY={(int)Math.round(Math.random()*(GameActivity.map[0].length-1)),
                    (int)Math.round(Math.random()*(GameActivity.map[0].length-1))};
            boolean check=true;
            for (Robot robot: GameActivity.robots)
                if (robot!=null && robot.sqX==sqXY[0] && robot.sqY==sqXY[1])
                    check=false;
            if(GameActivity.hunter.sqX==sqXY[0]&& GameActivity.hunter.sqY==sqXY[1])
                check=false;
            if(GameActivity.map[sqXY[1]][sqXY[0]]==2)
                check=false;
            if(GameActivity.map[sqXY[1]][sqXY[0]]==3)
                check=false;
            if(check){
                GameActivity.map[sqXY[1]][sqXY[0]]=3;
                return sqXY;
            }
        }
    }
    void setXY(){
        sqX = 0;
        sqY = 1;
        this.x = GameActivity.squares[sqY][sqX].x;
        this.y = GameActivity.squares[sqY][sqX].y;
        stuff.setX(this.x);
        stuff.setY(this.y);
    }
}
