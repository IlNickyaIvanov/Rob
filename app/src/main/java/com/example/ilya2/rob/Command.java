package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Command {
    ImageView image;
    static int size= (int) Math.round(GameActivity.screenHeight/ GameActivity.commands.length/1.5-50), alpha; // размер картинки, врещение, прозрачность
    boolean touched=false;
    float x,y;
    int type;
    String text;
    @SuppressLint("ClickableViewAccessibility")
    Command(final GameActivity main, final float x, final float y, int type) {
        this.type =type;
        image = new ImageView(main);
        this.x=x;image.setX(x);
        this.y=y;image.setY(y);
        switch (type){
            case(-1):
                image.setImageResource(R.drawable.trash);
                break;
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
        if(type!=-1)image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                touched = true;
                return false;
            }
        });
        else{
            image.setAlpha(0f);
        }
    }

    void startAnim(){
        image.setAlpha(1f);
//        AlphaAnimation animation = new AlphaAnimation(0f,1f);
//        animation.setDuration(1000);
//        image.startAnimation(animation);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) { }
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                image.setAlpha(1f);
//            }
//            @Override
//            public void onAnimationRepeat(Animation animation) { }
//        });
    }
    void stopAnim(){
        if(image!=null) {
            image.clearAnimation();
            image.setAlpha(0f);
        }
//        AlphaAnimation animation = new AlphaAnimation(1f,0f);
//        animation.setDuration(1000);
//        image.startAnimation(animation);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) { }
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                image.setAlpha(0f);
//            }
//            @Override
//            public void onAnimationRepeat(Animation animation) { }
//        });
    }

    boolean isUnderBlock(Block block){
        if(block.x>x && block.y>y)
            return block.x > x && block.y > y && block.x < (x + size) && block.y < (y + size);
        return (block.x+block.size) > x && (block.y+block.size) > y
                && (block.x+block.size) < (x + size) && (block.y+block.size) < (y + size);
    }
}
