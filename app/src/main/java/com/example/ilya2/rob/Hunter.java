package com.example.ilya2.rob;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Hunter {
    private float x, y;
    int sqX, sqY;
    private float speedX,targetX,speedY,targetY;
    boolean anim=false;
    private int onTickMove=600/60;
    ImageView image;
    static int size=50;
    private int map[][];//карта 0-пустая 1-цели 2-стены
    Queue<int[]>moveXY=new LinkedList<>();
    int steps=0;
    Activity activity;

    Hunter(Activity main, int sqX, int sqY,int[][]map) {
        this.map=new int[map.length][map[0].length];
        size = Square.size;
        float x = MainActivity.squares[sqX][sqY].x;
        float y = MainActivity.squares[sqX][sqY].y;
        activity=main;
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.bad_bot);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x = x;
        this.y = y;
        this.sqX=sqX;
        this.sqY=sqY;
        MyTimer timer = new MyTimer();
        timer.start();
    }
    //вызывать после окончания передвежения робота
    void hunt(){
        for(int i=0;i<MainActivity.map.length;i++)
            for (int j=0;j<MainActivity.map[i].length;j++)
                map[i][j]=MainActivity.map[i][j];
        for (Robot robot:MainActivity.robots)
            if(!robot.broken)
                map[robot.sqY][robot.sqX]=1;
        moveXY=waveAlg(sqX,sqY,map);
    }


    //метод, реализующий волновой алгоритм на Java
    //на вход даются начальные координаты и карта(где 1-цели, 2-стены)
    static Queue<int[]> waveAlg(int x, int y, int map[][]){
        Queue<String> ways = new LinkedList<>(); //пути до клеток
        ArrayList<String> checked= new ArrayList<>();//проверенные клетки
        Queue<int[]> wave = new LinkedList<>();//очередь на проверку
        int[]xy = {x,y};
        wave.add(xy);
        String shortWay="";
        ways.add(shortWay);
        do{
            int[] square=wave.poll();//проверяемая клетка
            String way=ways.poll();//путь до клетки
            if(map[square[1]][square[0]]==1){
                shortWay=way.substring(0,way.length()-1);
                break;
            }
            int[] sq=new int[]{square[0]-1,square[1]};
            if(square[0]>0 && !checked.contains(sq[0]+""+sq[1])&& map[sq[1]][sq[0]]!=2){
                wave.add(sq);//добавление клетки
                ways.add(way+sq[0]+" "+sq[1]+",");//добавление пути к клетке(в зависимости от потребностей может задаваться различными способами)
            }
            sq=new int[]{square[0],square[1]-1};
            if(square[1]>0 && !checked.contains(sq[0]+""+sq[1])&& map[sq[1]][sq[0]]!=2){
                wave.add(sq);
                ways.add(way+sq[0]+" "+sq[1]+",");
            }
            sq=new int[]{square[0]+1,square[1]};
            if(square[0]<map[0].length-1 && !checked.contains(sq[0]+""+sq[1])&& map[sq[1]][sq[0]]!=2){
                wave.add(sq);
                ways.add(way+sq[0]+" "+sq[1]+",");
            }
            sq=new int[]{square[0],square[1]+1};
            if(square[1]<map.length-1 && !checked.contains(sq[0]+""+sq[1])&& map[sq[1]][sq[0]]!=2){
                wave.add(sq);
                ways.add(way+sq[0]+" "+sq[1]+",");
            }
            checked.add(square[0]+""+square[1]);
        }while(wave.size()>0);
        //конвертирование строки в очередь(требуется для удобства последующей интеграции)
        String[] crd = shortWay.split(",");
        Queue<int[]> wayXY=new LinkedList<>();
        if(shortWay.length()>0)for (String obj: crd){
            String[] text = obj.split(" ");
            x=Integer.parseInt(text[0]);
            y=Integer.parseInt(text[1]);
            xy=new int[]{x,y};
            wayXY.add(xy);
        }
        return wayXY;
    }

    //метод, отвечающй за перемещение
    boolean botMove() {
        if(moveXY.size()==0)return true;
        anim = true;
        int xy[]=moveXY.poll();
        for (Robot robot:MainActivity.robots)
            if(xy[0]==robot.sqX&&xy[1]==robot.sqY) {
                robot.broken = true;
                robot.setAlpha(0.5f);
                findNewActiveRobot();
            }
        float x=MainActivity.squares[xy[1]][xy[0]].x;
        float y=MainActivity.squares[xy[1]][xy[0]].y;
        targetX=(x);
        targetY=(y);
        speedX=(x-this.x)/40;
        speedY=(y-this.y)/40;
        this.sqX = xy[0];
        this.sqY = xy[1];
        steps--;
        if(steps==0)moveXY.clear();
        return steps==0;
    }
    void update(){
        if(anim){
            if(Math.round(x)!=Math.round(targetX))x+=speedX;
            if(Math.round(y)!=Math.round(targetY))y+=speedY;
            if(Math.round(x)==Math.round(targetX) && Math.round(y)==Math.round(targetY)) anim=false;
            image.setX(x);
            image.setY(y);
        }
    }

    void delete(){
        FrameLayout parent = (FrameLayout) image.getParent();
        parent.removeView(image);
    }

    void findNewActiveRobot(){
        for(int i=0;i<MainActivity.robots.length;i++) {
            if (!MainActivity.robots[i].broken){
                MainActivity.activeRobot = i;
                break;
            }
            //конец игры, охотник всех поймал
            if(i==MainActivity.robots.length-1)
                Utils.AlertDialog(activity, "Конец игры", "Охотник всех поймал", "Еще раз");

        }
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, onTickMove);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }
        @Override
        public void onFinish() {
        }
    }
}
