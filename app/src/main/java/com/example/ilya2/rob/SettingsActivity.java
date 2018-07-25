package com.example.ilya2.rob;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    static ArrayList<Robot> robots;
    SeekBar seekBar,seekBar2,seekBar3,seekBar4;
    TextView textSize,textWall,textStuff,textRobots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textSize = findViewById(R.id.textView);
        textSize.setText("Размер карты: "+mapSize+"x"+mapSize);
        textWall = findViewById(R.id.textView2);
        textWall.setText("Кол-во стен: "+wallNum);
        textStuff = findViewById(R.id.textView3);
        textStuff.setText("Кол-во вопросов: "+stuffNum);
        textRobots = findViewById(R.id.textView4);
        textRobots.setText("Кол-во роботов: "+robotsNum);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(mapSize*100/10);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar2.setProgress(wallNum*100/(int)(mapSize*mapSize*0.16));
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar3.setProgress(stuffNum*100/(mapSize-1));
        seekBar4 = findViewById(R.id.seekBar4);
        seekBar4.setProgress(robotsNum*100/4);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMapSize(seekBar.getProgress());
                setWallNum(seekBar2.getProgress());
                setStuffNum(seekBar3.getProgress());
                createMap();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setWallNum(seekBar.getProgress());
                createMap();
            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setStuffNum(seekBar.getProgress());
                createMap();
            }
        });

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setRobotsNum(seekBar.getProgress());
            }
        });
        createMap();

    }
    //трекер по размеру карты
    void setMapSize(int progress){
        float num = progress*10/100;
        mapSize = (num>3)?Math.round(num):3;
        textSize.setText("Размер карты: "+mapSize+"x"+mapSize);
        seekBar.setProgress(mapSize*100/10);
    }
    //трекер по стенам на карте
    void setWallNum(int progress){
        float num = progress*Math.round(mapSize*mapSize*0.16)/100;
        wallNum = (num>0.5)?Math.round(num):1;
        textWall.setText("Кол-во стен: "+wallNum);
        seekBar2.setProgress(wallNum*100/(int)(mapSize*mapSize*0.16));
    }
    //трекер по вопросам на карте
    void setStuffNum(int progress){
        float num = progress*(mapSize-1)/100;
        stuffNum = (num>0.5)?Math.round(num):1;
        textStuff.setText("Кол-во вопросов: "+stuffNum);
        seekBar3.setProgress(stuffNum*100/(mapSize-1));

    }
    //трекер по роботам
    void setRobotsNum(int progress){
        float num = progress*(4)/100;
        robotsNum = (num>0.5)?Math.round(num):1;
        textRobots.setText("Кол-во роботов: "+robotsNum);
        seekBar4.setProgress(robotsNum*100/4);
        createMap();
    }
    void createRandomMap(){
        setMapSize((int)(Math.random()*100));
        setWallNum((int)(Math.random()*100));
        setStuffNum((int)(Math.random()*100));
        setRobotsNum((int)(Math.random()*100));
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
        if(robots!=null)
            for(Robot robot:robots)
                robot.delete();
        map = new ArrayList<>();
        stuff = new ArrayList<>();
        robots = new ArrayList<>();
        int screenWidth = this.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = this.getApplicationContext().getResources().getDisplayMetrics().heightPixels-50;
        Square.size = screenHeight/2/mapSize;
        Square.startX = (screenWidth-(mapSize*Square.size))/2;
        Square.startY = screenHeight*3/7+(screenHeight/2-(mapSize*Square.size))/2;
        for (int i=0;i<mapSize;i++){
            ArrayList<Square> line = new ArrayList<>();
            for(int j=0;j<mapSize;j++){
                line.add(new Square(this,j*(Square.size)+Square.startX,i*(Square.size)+Square.startY,0));
            }
            map.add(line);
        }
        //роботы
        robots.add(new Robot(this,0,0,2,map));
        if(robotsNum>1)
            robots.add(new Robot(this,mapSize-1,mapSize-1,0,map));
        if(robotsNum>2)
            robots.add(new Robot(this,mapSize-1,0,3,map));
        if(robotsNum>3)
            robots.add(new Robot(this,0,mapSize-1,1,map));
        //стены
        for (int i=0;i<wallNum;i++){
            int[]xy=randomSqXY();
            map.get(xy[1]).get(xy[0]).ID_NUMBER=2;
            map.get(xy[1]).get(xy[0]).image.setAlpha(0f);
        }
        //вопросы
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
            if(sqXY[0]==0 && sqXY[1]==0)
                check=false;
            if(robotsNum>1 && sqXY[0]==mapSize-1 && sqXY[1]==mapSize-1)
                check=false;
            if(robotsNum>2 && sqXY[0]==mapSize-1 && sqXY[1]==0)
                check=false;
            if(robotsNum>3 && sqXY[0]==0 && sqXY[1]==mapSize-1)
                check=false;
            if(check)return sqXY;
        }
    }

    public void onClickRandomMap(View view) {
        createRandomMap();
    }
}
