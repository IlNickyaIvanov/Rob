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
    int size=Command.size, alpha; // размер картинки, врещение, прозрачность
    boolean touched=true;
    boolean connected=false;
    float x,y;
    int type;
    TextView textView;
    @SuppressLint("ClickableViewAccessibility")
    Block(MainActivity main, float x, float y,int type) {
        this.type = type;
        this.x=x;
        this.y=y;
        image = new ImageView(main);
        textView = new TextView(main);
        this.x=x;
        this.y=y;
        setBlockXY(x,y);
        image.setImageResource(R.drawable.cmd);
        switch (type){
            case (0):
                textView.setText("вперед");
                break;
            case (1):
                textView.setText("направо");
                break;
            case (2):
                textView.setText("налево");
                break;
        }
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size/2));
        main.addContentView(textView,new RelativeLayout.LayoutParams(size*3/4,size*3/4));
        image.setOnTouchListener(new View.OnTouchListener()
        {@Override
        public boolean onTouch(View v, MotionEvent event)
        {
            touched=true;
            return false;
        }
        });
    }
    Block setXY(float x,float y){
        this.x=x-size/2;
        this.y=y-size/2;
        setBlockXY(this.x,this.y);
        connected = MainActivity.robots[MainActivity.activeRobot].blocks.size() == 1;
        for (Block block:MainActivity.robots[MainActivity.activeRobot].blocks){
            if(block.y ==this.y && block.x==this.x)
                continue;
            if(getRoundX()==block.getRoundX() &&  getRoundY()==block.getRoundY()) {
                connected = true;
                touched=false;
                return block;
            }
        }
        return null;
    }
    void setXY(Block lastBlock){
        this.x = lastBlock.x;
        this.y = lastBlock.y+lastBlock.size/2-lastBlock.size/6;
        setBlockXY(this.x,this.y);
    }
    int getRoundX(){
        return Math.round(x-x%100);
    }
    int getRoundY(){
        return Math.round(y-y%100);
    }
    int getRoundY(int size){
        return Math.round((y+size)-(y+size)%100);
    }
    void delete(){
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
        parent = (FrameLayout) textView.getParent();
        parent.removeView(textView);
    }
    void hide(){
        image.setAlpha(0f);
        textView.setAlpha(0f);
    }
    void show(){
        image.setAlpha(1f);
        textView.setAlpha(1f);
    }
    void setBlockXY(float x, float y){
        image.setX(x);textView.setX(x+size/4);
        image.setY(y);textView.setY(y+size/2/4);
    }
}