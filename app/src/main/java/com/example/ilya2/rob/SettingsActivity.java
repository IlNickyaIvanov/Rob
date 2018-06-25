package com.example.ilya2.rob;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    static int mapSize=5;//значения
    static int wallNum=3;//по
    static int stuffNum=3;//умолчанию
    static int robotsNum=2;//
    static ArrayList<ArrayList<Square>> map;
    static ArrayList<Stuff> stuff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final TextView textSize = findViewById(R.id.textView);
        final TextView textWall = findViewById(R.id.textView2);
        final TextView textStuff = findViewById(R.id.textView3);
        final TextView textRobots = findViewById(R.id.textView4);
        SeekBar seekBar = findViewById(R.id.seekBar);//трекер по размеру карты
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float num = progress/10;
                mapSize = (num>0.5)?Math.round(num):1;
                float num2 = seekBar.getProgress()*Math.round(mapSize*mapSize*0.16)/100;
                wallNum = (num2>0.5)?Math.round(num2):0;
                float num3 = seekBar.getProgress()*(mapSize-1)/100;
                stuffNum = (num3>0.5)?Math.round(num3):0;
                createMap();
                textSize.setText("Размер карты: "+mapSize+"x"+mapSize);
                textWall.setText("Кол-во стен: "+wallNum);
                textStuff.setText("Кол-во вопросов: "+stuffNum);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        SeekBar seekBar2 = findViewById(R.id.seekBar2);//трекер по стенам на карте
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float num = seekBar.getProgress()*Math.round(mapSize*mapSize*0.16)/100;
                wallNum = (num>0.5)?Math.round(num):0;
                createMap();
                textWall.setText("Кол-во стен: "+wallNum);
            }
        });
        SeekBar seekBar3 = findViewById(R.id.seekBar3);//трекер по вопросам на карте
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float num = seekBar.getProgress()*(mapSize-1)/100;
                stuffNum = (num>0.5)?Math.round(num):0;
                createMap();
                textStuff.setText("Кол-во вопросов: "+stuffNum);
            }
        });
        SeekBar seekBar4 = findViewById(R.id.seekBar4);//трекер по роботам на карте
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float num = seekBar.getProgress()*4/100;
                robotsNum = (num>0.5)?Math.round(num):1;
                textRobots.setText("Кол-во роботов: "+robotsNum);
            }
        });
        createMap();

    }
    void createMap(){
        if(map!=null)
            for (ArrayList<Square> squares:map)
                for (Square sq:squares)
                    sq.delete();
        if(stuff!=null)
            for (Stuff stf:stuff)
                stf.delete();
        map = new ArrayList<>();
        stuff = new ArrayList<>();
        int screenWidth = this.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = this.getApplicationContext().getResources().getDisplayMetrics().heightPixels-50;
        Square.size = screenHeight/2/mapSize;
        Square.startX = (screenWidth-(mapSize*Square.size))/2;
        for (int i=0;i<mapSize;i++){
            ArrayList<Square> line = new ArrayList<>();
            for(int j=0;j<mapSize;j++){
                line.add(new Square(this,j*(Square.size)+Square.startX,i*(Square.size)+screenHeight/2,0));
            }
            map.add(line);
        }

        for (int i=0;i<wallNum;i++){
            int[]xy=randomSqXY();
            map.get(xy[1]).get(xy[0]).ID_NUMBER=2;
            map.get(xy[1]).get(xy[0]).image.setAlpha(0f);
        }
        for (int i=0;i<stuffNum;i++){
            stuff.add(new Stuff(this,map));
        }

    }
    static int [] randomSqXY(){
        while(true){
            int []sqXY={(int)Math.round(Math.random()*(map.get(0).size()-1)),
                    (int)Math.round(Math.random()*(map.get(0).size()-1))};
            boolean check=true;
                if( map.get(sqXY[1]).get(sqXY[0]).ID_NUMBER==2)
                    check=false;
            if(check)return sqXY;
        }
    }
}
