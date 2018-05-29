package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Block {
    ImageView image;
    int size=Command.size*4/5, alpha; // размер картинки, врещение, прозрачность
    boolean touched=true;
    boolean connected=false;
     float x,y;
    int type;
    @SuppressLint("ClickableViewAccessibility")
    Block(MainActivity main, float x, float y,int type) {
        this.type = type;
        this.x=x;
        this.y=y;
        image = new ImageView(main);
        this.x=x;
        this.y=y;
        setBlockXY(x,y);
        switch (type){
            case (0):
                image.setImageResource(R.drawable.forward);
                break;
            case (1):
                image.setImageResource(R.drawable.right);
                break;
            case (2):
                image.setImageResource(R.drawable.left);
                break;
        }
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        image.setOnTouchListener(new View.OnTouchListener()
        {@Override
        public boolean onTouch(View v, MotionEvent event)
        {
            touched=true;
            return false;
        }
        });
    }
    //установка координат с проверкой на присоединение
    Block setXY(float x,float y){
        this.x=x-size/2;
        this.y=y-size/2;
        setBlockXY(this.x,this.y);
        connected = MainActivity.robots[MainActivity.activeRobot].blocks.size() == 1;
        for (Block block:MainActivity.robots[MainActivity.activeRobot].blocks){
            if(block.y ==this.y && block.x==this.x)
                continue;
            if(getRoundX()==block.getRoundX() &&  getRoundY()==block.getRoundY(size)) {
                connected = true;
                touched=false;
                return block;
            }
        }
        return null;
    }
    //установка к предыдущему блоку
    void setXY(Block lastBlock){
        this.x = lastBlock.x;
        this.y = lastBlock.y+lastBlock.size;
        setBlockXY(this.x,this.y);
    }
    private int getRoundX(){
        return Math.round(x-x%100);
    }
    private int getRoundY(){
        return Math.round(y-y%100);
    }
    int getRoundY(int size){
        return Math.round((y+size)-(y+size)%100);
    }
    void delete(){
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
    }
    void hide(){
        image.setAlpha(0f);
    }
    void show(){
        image.setAlpha(1f);
    }
    //чистая установка координат
    void setBlockXY(float x, float y){
        image.setX(x);
        image.setY(y);
    }
}