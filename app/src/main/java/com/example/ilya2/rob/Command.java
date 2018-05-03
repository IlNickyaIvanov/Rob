package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Command {
    ImageView image;
    static int size=MainActivity.screenWidth/2/MainActivity.commands.length-10, rotation, alpha; // размер картинки, врещение, прозрачность
    boolean touched=false;
    float x,y;
    int type;
    TextView textView;
    String text;
    @SuppressLint("ClickableViewAccessibility")
    Command(final MainActivity main, final float x, final float y,int type) {
        this.type =type;
        image = new ImageView(main);
        textView = new TextView(main);
        this.x=x;image.setX(x);textView.setX(x+size/4);
        this.y=y;image.setY(y);textView.setY(y+size/2/4);
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
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                touched = true;
                return false;
            }
        });
    }
}
