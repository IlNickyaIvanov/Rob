package com.example.ilya2.rob;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Table {
    TextView text;
    Activity activity;
    final int TEXT_SIZE=16;
    int width,height;
    static boolean isTableVisible;
    ImageView image;
    Table(Activity activity, String _text, View view,String pos){
        this.activity = activity;
        text = new TextView(activity);
        text.setText(_text);
        int line=3,max_len=0,c=0;
        for(int i=1;i<_text.length();++i){
            c++;
            if(_text.substring(i - 1, i).equals("\n")) {
                line++;
                if(c>max_len)max_len=c;
                c=0;
            }
        }
        if(c>max_len)max_len=c;
        width = (max_len*pxFromDp(TEXT_SIZE)/2);
        height = line*(pxFromDp(TEXT_SIZE)+10);

        int w = view.getWidth();w=(w==0)?Square.size:w;
        int h = view.getHeight();h=(h==0)?Square.size:h;
        switch (pos){
            case ("down"):
                float x = view.getX() + w / 2 - width / 2;
                text.setX((x+width<GameActivity.screenWidth)?x:x-(x+width-GameActivity.screenWidth));
                text.setY(view.getY() + h + 10);
                break;
            case ("right"):
                text.setX(view.getX() + w + 10);
                text.setY(view.getY());
                break;
            case ("up"):
                text.setX(view.getX() + w / 2 - width / 2);
                text.setY(view.getY()- h);
                break;
            case ("left"):
                text.setX(view.getX() - width - 10);
                text.setY(view.getY());
                break;
        }
        text.setBackgroundResource(R.drawable.orange);
        text.setTextColor(Color.parseColor("#FFDBB3"));

        text.setTextSize(TEXT_SIZE);
        text.setGravity(Gravity.CENTER);
        activity.addContentView(text,new RelativeLayout.LayoutParams(width,height));

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                isTableVisible = false;
            }
        });

        isTableVisible = true;

        AlphaAnimation anim = new AlphaAnimation(0f,1f);
        anim.setDuration(500);
        text.startAnimation(anim);

    }

    Table(Activity activity,String _text,int imgNum){
        this.activity = activity;
        image = new ImageView(activity);
        switch (imgNum){
            case 1:
                image.setImageResource(R.drawable.tutor1);
                break;
            case 2:
                image.setImageResource(R.drawable.tutor2);
                break;
        }
        int size = GameActivity.screenHeight/3;
        image.setX(GameActivity.screenWidth/2-size);
        image.setY(GameActivity.screenHeight*2/3-pxFromDp(TEXT_SIZE)-5);

        text = new TextView(activity);
        text.setText(_text);
        width = (_text.length()*pxFromDp(TEXT_SIZE)/2);
        text.setX(image.getX()+size-width/2);
        text.setY(image.getY()+size);

        isTableVisible = true;

        activity.addContentView(image, new RelativeLayout.LayoutParams(2*size,size));
        activity.addContentView(text,new RelativeLayout.LayoutParams(width,pxFromDp(TEXT_SIZE)+10));
    }

    private int pxFromDp(float dp) {
        return (int)(dp *activity.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    void delete(){
        try{
            FrameLayout parent = (FrameLayout)text.getParent();
            parent.removeView(text);
            parent = (FrameLayout)image.getParent();
            parent.removeView(image);
        }catch (Throwable ignored){}
    }
}
