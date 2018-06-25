package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Block {
    ImageView image;
    int size=Command.size*4/5, alpha; // размер картинки, врещение, прозрачность
    boolean connected=false;
    int num;
     float x,y;
    int type;
    boolean newCom=true;
    @SuppressLint("ClickableViewAccessibility")
    Block(GameActivity main, float x, float y, int type, final int number) {
        this.num = number;
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
            GameActivity.touchedBlock=num;
            return false;
        }
        });
    }
    //установка координат с проверкой на присоединение
    Block setXY(){
        setBlockXY(this.x,this.y);
        for (Block block: GameActivity.blocks){
            if(block.y ==this.y && block.x==this.x)
                continue;
            if(getRoundX()==block.getRoundX() &&  getRoundY()==block.getRoundY(size)) {
                connected = true;
                GameActivity.touchedBlock=-1;
                return block;
            }
        }
        return null;
    }

    boolean checkTopConnection(float x,float y){
        this.x=x-size/2;
        this.y=y-size/2;
        connected = GameActivity.blocks.size() == 1;
        if(getRoundY(size)== GameActivity.blocks.get(0).getRoundY() && getRoundX()== GameActivity.blocks.get(0).getRoundX()){
            this.x = GameActivity.blocks.get(0).x;
            this.y = GameActivity.blocks.get(0).y- GameActivity.blocks.get(0).size;
            setBlockXY(this.x,this.y);
            connected = true;
            GameActivity.touchedBlock=-1;
            return true;
        }
        return false;
    }

    //установка к предыдущему блоку
    void setXY(Block lastBlock){
        this.x = lastBlock.x;
        this.y = lastBlock.y+lastBlock.size;
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
    }
    void hide(){
        image.setAlpha(0f);
    }
    void show(){
        image.setAlpha(1f);
    }
    void setOld(){
        image.setAlpha(0.6f);
    }
    //чистая установка координат
    void setBlockXY(float x, float y){
        image.setX(x);
        image.setY(y);
    }
    void setNum(int number){
        num = number;
    }
}