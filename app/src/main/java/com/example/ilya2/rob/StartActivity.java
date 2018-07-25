package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StartActivity extends AppCompatActivity {
    Image red,purple;
    Animation animAlpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);

    }

    public void onClickStart(View view) {
        view.startAnimation(animAlpha);
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }

    public void onClickSettings(View view) {
        view.startAnimation(animAlpha);
        startActivity(new Intent(StartActivity.this,SettingsActivity.class));
    }
    class Image{
        ImageView image;
        float x,y;
        int size=600;
        Activity activity;
        Image(Activity activity,float x, float y, int type){
            this.activity = activity;
            image = new ImageView(activity);
            image.setX(x);this.x = x;
            image.setY(y);this.y = y;
            switch (type) {
                case (1):
                    //image.setImageResource(R.drawable.menu_good_bot);
                    image.setRotation(150);
                    activity.addContentView(image,new RelativeLayout.LayoutParams(size,size));
                    break;
                case (2):
                    //image.setImageResource(R.drawable.menu_bad_bot);
                    image.setRotation(330);
                    activity.addContentView(image,new RelativeLayout.LayoutParams(size+60,size+60));
                    break;
            }
        }
    }
}
