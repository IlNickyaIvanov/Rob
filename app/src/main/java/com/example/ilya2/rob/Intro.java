package com.example.ilya2.rob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Intro extends AppCompatActivity {

    int screenWidth,screenHeight;
    ImageView intro;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        intent = new Intent(Intro.this, StartActivity.class);
        //само интро
        intro = new ImageView(this);
        this.addContentView(intro, new RelativeLayout.LayoutParams(screenWidth, screenWidth));
        intro.setX(0);
        intro.setY((screenHeight-screenWidth)/2);
        intro.setImageResource(R.drawable.fulllogo);
        Animation introAnim = AnimationUtils.loadAnimation(this, R.anim.intro);
        intro.startAnimation(introAnim);
        introAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                intro.setVisibility(View.INVISIBLE);
                startActivity(intent);
                Intro.this.finish();
                }@Override
            public void onAnimationRepeat(Animation animation) { }});
    }
}
