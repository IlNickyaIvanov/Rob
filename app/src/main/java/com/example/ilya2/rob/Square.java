package com.example.ilya2.rob;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

 class Square {
    ImageView image;
    float x,y;
    static int size=50;
    static int startX,startY;
    int ID_NUMBER=0;
    boolean isVISIBLE=true;
        Square(Activity main, float x, float y) {
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.sqdark);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x = x;
        this.y = y;
    }
}
