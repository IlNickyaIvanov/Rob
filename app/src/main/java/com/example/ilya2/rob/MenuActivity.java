package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class MenuActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_OFFER = "offer";
    static SharedPreferences mSettings;
    private ConstraintLayout animLayout;
    float W,H;
    static float size = 0,h=0;
    AnimSquare aSquares[][];
    static Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        W = metrics.widthPixels;
        H = metrics.heightPixels;
        animLayout = findViewById(R.id.animLayout);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!mSettings.contains(APP_PREFERENCES_OFFER)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_OFFER, true);
            editor.apply();
        }
        sw = findViewById(R.id.switch1);
        if (!mSettings.getBoolean(APP_PREFERENCES_OFFER, false)) {
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
        backAnim();

        MyTimer timer = new MyTimer();
        timer.start();
    }

    public void onClickStart(View view) {
        startActivity(new Intent(MenuActivity.this,GameActivity.class));
    }

    public void onClickSettings(View view) {
        startActivity(new Intent(MenuActivity.this,SettingsActivity.class));
    }

    void backAnim(){
        aSquares = new AnimSquare[14][3];
        h = H/(aSquares.length+1);
        size = (int)(h*Math.sqrt(2));
        for(int i=0;i<aSquares.length;i++)
            for(int j=0;j<aSquares[0].length;j++)
                aSquares[i][j]=new AnimSquare(this,(i%2==0 && j==aSquares[0].length-1)?0:(int)(Math.round(Math.random()*3)),
                        2*h*j*0.989f+((i%2==0)?h:0)+(2*h-size)/2+(W-2*h*aSquares[0].length)/2,i*h*0.989f);
    }

    void update(){
        for(int i=0;i<aSquares.length;i++)
            for(int j=0;j<aSquares[0].length;j++)
                if(i%2!=0 || j!=aSquares[0].length-1)
                if(!aSquares[i][j].anim && Math.random()>0.995) {
                    aSquares[i][j].setImage((int) Math.round(Math.random() * 2)+1);
                    aSquares[i][j].anim=true;
                }
                else if(aSquares[i][j].anim)
                    aSquares[i][j].animate();
    }

    class AnimSquare{
        ImageView image;
        double x,y;
        int type;
        float alpha=1f;
        float speedAlpha=0.027f;
        boolean anim = false;
        AnimSquare(Activity activity,int type,float x,float y){
            this.x = x-h;
            this.y = y-h;
            this.type = type;
            image = new ImageView(activity);
            image.setX(x);
            image.setY(y);
            image.setRotation(45);
            switch (type) {
                case 0:
                    image.setImageResource(R.drawable.asqd);
                    alpha = 0f;
                    break;
                case 1:
                    image.setImageResource(R.drawable.asqm);
                    break;
                case 2:
                    image.setImageResource(R.drawable.asql);
                    break;
                case 3:
                    image.setImageResource(R.drawable.asqd);
                    break;
                default:
                    image.setImageResource(R.drawable.question);
                    break;
            }
            image.setAlpha(alpha);
            animLayout.addView(image,new RelativeLayout.LayoutParams((int)size,(int)size));
        }
        void setImage(int type){
            if(this.type==-1) {
                this.type = type;
                ConstraintLayout parent = (ConstraintLayout) image.getParent();
                parent.removeView(image);
                switch (type) {
                    case 0:
                     image.setImageResource(R.drawable.asqd);
                     alpha = 0f;
                      break;
                    case 1:
                        image.setImageResource(R.drawable.asqm);
                        break;
                    case 2:
                        image.setImageResource(R.drawable.asql);
                        break;
                    case 3:
                        image.setImageResource(R.drawable.asqd);
                        break;
                    default:
                        image.setImageResource(R.drawable.question);
                        break;
                }
                speedAlpha = -speedAlpha;
                animLayout.addView(image,new RelativeLayout.LayoutParams((int)size,(int)size));
                setAlpha(0f);
            }
            else {
                this.type=-1;
                speedAlpha = -speedAlpha;
            }
        }
        void setAlpha(float n){
            image.setAlpha(n);
            alpha = n;
        }
        void animate(){
            if(speedAlpha>0 && alpha<1f)
                setAlpha(alpha+speedAlpha);
            else if(speedAlpha<0 && alpha>0f)
                setAlpha(alpha+speedAlpha);
            else
                anim = false;
        }
    }
    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 1000/60); // продолжительность работы таймера в милисекундах, интервал срабатывания
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update(); // вызываем метод, в котором происходит обновление игры
        }
        @Override
        public void onFinish() {
        }
    }

}
