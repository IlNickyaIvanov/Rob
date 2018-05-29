package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Command {
    ImageView image;
    static int size= (int) Math.round(MainActivity.screenHeight/MainActivity.commands.length/1.5-50), alpha; // размер картинки, врещение, прозрачность
    boolean touched=false;
    float x,y;
    int type;
    String text;
    @SuppressLint("ClickableViewAccessibility")
    Command(final MainActivity main, final float x, final float y,int type) {
        this.type =type;
        image = new ImageView(main);
        this.x=x;image.setX(x);
        this.y=y;image.setY(y);
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
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                touched = true;
                return false;
            }
        });
    }
    boolean isUnderBlock(Block block){
        if(block.x>x && block.y>y)
            return block.x > x && block.y > y && block.x < (x + size) && block.y < (y + size);
        return (block.x+block.size) > x && (block.y+block.size) > y
                && (block.x+block.size) < (x + size) && (block.y+block.size) < (y + size);
    }
}
