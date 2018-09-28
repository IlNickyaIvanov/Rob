package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class StartActivity extends AppCompatActivity {
    Image red,purple;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_OFFER = "offer";
    static SharedPreferences mSettings;

    static Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(!mSettings.contains(APP_PREFERENCES_OFFER)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_OFFER, true);
            editor.apply();
        }
        sw = findViewById(R.id.switch1);
        if(!mSettings.getBoolean(APP_PREFERENCES_OFFER,false )){
          sw.setChecked(false);
        }
        if (sw != null) {
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(APP_PREFERENCES_OFFER, sw.isChecked());
                    editor.apply();
                }
            });
        }
    }

    public void onClickStart(View view) {
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }

    public void onClickSettings(View view) {
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
