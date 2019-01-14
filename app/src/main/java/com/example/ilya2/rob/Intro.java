package com.example.ilya2.rob;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Intro extends AppCompatActivity {

    int screenWidth,screenHeight;
    ImageView intro;
    Intent intent;
    boolean click = false;
    Animation introAnim;
    Button btn;
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
        intent = new Intent(Intro.this, MenuActivity.class);
        //само интро
        intro = new ImageView(this);
        this.addContentView(intro, new RelativeLayout.LayoutParams(screenWidth*5/6, screenWidth*5/6));
        intro.setX(screenWidth/12);
        intro.setY((screenHeight-screenWidth*5/6)/2);
        intro.setImageResource(R.drawable.clogo);
        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(click){
                    setAnim();
                    click = false;
                }
            }
        });
        btn = findViewById(R.id.buttonBrowser);
        setAnim();
    }

    void setAnim(){
        introAnim = AnimationUtils.loadAnimation(this, R.anim.intro);
        intro.startAnimation(introAnim);
        btn.startAnimation(introAnim);
        introAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                if(!mSettings.contains("first_lunch")) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean("first_lunch", true);
                    editor.apply();
                    intent = new Intent(Intro.this, GameActivity.class);
                }
                if(!click){
                    intro.setVisibility(View.INVISIBLE);
                    startActivity(intent);
                    Intro.this.finish();
                }
            }@Override
            public void onAnimationRepeat(Animation animation) { }});
    }

    public void onClickBrowser(View view) {
        click = true;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://algoritmika.org"));
        startActivity(browserIntent);
    }
}