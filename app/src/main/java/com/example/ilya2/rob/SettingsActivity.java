package com.example.ilya2.rob;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
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
    static SeekBar seekBar,seekBar2,seekBar3,seekBar4;//карта, стены, вопросы, роботы
    static TextView textSize,textWall,textStuff,textRobots,textEditor;
    static Switch switchUseMap;
    static int editType=2;//0 - ничего, 2 - стены, 3 - вопросы

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_USE_MAP = "useMap";
    static SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        switchUseMap = findViewById(R.id.switch2);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(!mSettings.contains(APP_PREFERENCES_USE_MAP)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_USE_MAP, false);
            editor.apply();
        }
        switchUseMap.setChecked(mSettings.getBoolean(APP_PREFERENCES_USE_MAP,false ));
        if (switchUseMap != null) {
            switchUseMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putBoolean(APP_PREFERENCES_USE_MAP, switchUseMap.isChecked());
                    editor.apply();
                }
            });
        }

        textSize = findViewById(R.id.textView);
        textSize.setText("Размер карты: "+mapSize+"x"+mapSize);
        textSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditType(0);
            }
        });
        textWall = findViewById(R.id.textView2);
        textWall.setText("Кол-во стен: "+wallNum);
        textWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditType(2);
            }
        });
        textStuff = findViewById(R.id.textView3);
        textStuff.setText("Кол-во вопросов: "+stuffNum);
        textStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditType(3);
            }
        });
        textRobots = findViewById(R.id.textView4);
        textRobots.setText("Кол-во роботов: "+robotsNum);
        textRobots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditType(0);
            }
        });
        textEditor = findViewById(R.id.textView5);
        textRobots.setText("Кол-во роботов: "+robotsNum);
        textEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditType((editType<3)?editType+1:0);
            }
        });

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
                setWallNum(wallNum*100/(int)Math.round(mapSize*mapSize*0.16));
                setStuffNum(stuffNum*100/(mapSize-1));
                createMap(true);
                setEditType(0);
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
                createMap(false);
                setEditType(2);
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
                createMap(false);
                setEditType(3);
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
                setEditType(0);
            }
        });
        createMap(map!=null);

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
        float num = (float)(((progress>100)?100:progress)*(mapSize*mapSize*0.16)/100f);
        wallNum = (num>0.5)?Math.round(num):1;
        textWall.setText("Кол-во стен: "+wallNum);
        seekBar2.setProgress(wallNum*100/(int)Math.round(mapSize*mapSize*0.16));
    }
    //трекер по вопросам на карте
    void setStuffNum(int progress){
        float num = ((progress>100)?100:progress)*(mapSize-1)/100f;
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
        createMap(true);
    }
    void createRandomMap(){
        setMapSize((int)(Math.random()*100));
        setWallNum((int)(Math.random()*100));
        setStuffNum((int)(Math.random()*100));
        setRobotsNum((int)(Math.random()*100));
        createMap(false);
    }
    void createMap(boolean safeMap){
        int safe[][]=null;
        if(safeMap)
            safe=safeMap();

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

        int walls = 0;
        for (int i=0;i<mapSize;i++){
            ArrayList<Square> line = new ArrayList<>();
            for(int j=0;j<mapSize;j++){
                if(safe!=null && i<safe.length && j<safe.length) {
                    line.add(new Square(this, j * (Square.size) + Square.startX,j, i * (Square.size) + Square.startY,i,
                            safe[i][j]));
                    map.add(line);
                    if(safe[i][j]==2)
                        walls++;
                    if(safe[i][j]==3)stuff.add(new Stuff(this,map,j,i));
                    map.remove(line);
                }else
                    line.add(new Square(this,j*(Square.size)+Square.startX,j,i*(Square.size)+Square.startY,i,0));
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
        for (int i=walls;i<wallNum;i++){
            int[]xy=randomSqXY();
            map.get(xy[1]).get(xy[0]).ID_NUMBER=2;
            map.get(xy[1]).get(xy[0]).image.setAlpha(0f);
        }
        //вопросы
        for (int i=stuff.size();i<stuffNum;i++){
            stuff.add(new Stuff(this,map,-1,-1));
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

    int[][] safeMap(){
        int[][]smap  = new int[(mapSize<map.size())?mapSize:map.size()][(mapSize<map.size())?mapSize:map.size()];
        int wallCun=0,stuffCun=0;
        for (int i=0;i < smap.length;i++){
            for (int j=0;j < smap.length;j++){
                if(map.get(i).get(j).ID_NUMBER==2 && wallCun<wallNum) {
                    smap[i][j] = map.get(i).get(j).ID_NUMBER;
                    wallCun++;
                }else if(map.get(i).get(j).ID_NUMBER==3 && stuffCun<stuffNum) {
                    smap[i][j] = map.get(i).get(j).ID_NUMBER;
                    stuffCun++;
                }else
                    smap[i][j] = 0;
            }
        }
        switch (robotsNum){
            case(4):
                smap[smap.length-1][0]=0;
            case(3):
                smap[0][smap.length-1]=0;
            case(2):
                smap[smap.length-1][smap.length-1]=0;
            case(1):
                smap[0][0]=0;
                break;
        }
        return smap;
    }

    public void onClickRandomMap(View view) {
        createRandomMap();
    }

    void setEditType(int n){
        editType = (n==1)?2:n;
        switch (editType){
            case(0):
                textEditor.setText("Нажми, чтобы СОЗДАТЬ клетку...");
                break;
            case(2):
                textEditor.setText("Нажми, чтобы УБРАТЬ клетку...");
                break;
            case(3):
                textEditor.setText("Нажми, чтобы создать ВОПРОС...");
                break;
        }
    }
}
