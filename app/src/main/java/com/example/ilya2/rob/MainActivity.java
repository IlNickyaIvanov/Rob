package com.example.ilya2.rob;

import android.app.Activity;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int activeRobot=0;

    final static int map[][] = {
            {0,0,0,0,0},
            {0,2,0,0,0},
            {0,2,0,2,0},
            {0,0,0,2,0},
            {0,0,0,0,0}};
    static Square [][] squares;
    static Robot robots[];
    static Hunter hunter;
    static String AlertDialogMessage;
    boolean pause=false;
    boolean move=false;
    static Command commands[];
    static ArrayList<Block> blocks;
    static Stuff stuff[];
    static int screenWidth,screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTimer timer = new MyTimer();
        timer.start();
        createMap();
        startGame(this);
        commands = new Command[3];
        blocks = new ArrayList<>();
        int step = screenHeight/6+(screenHeight*4/5-Command.size*commands.length)/2;
        for (int i=0;i<commands.length;i++) {
            commands[i] = new Command(this,5 , step+i*(Command.size+10), i);
        }
    }
    public void createMap(){
        squares = new Square[map.length][map[0].length];
        screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels-50;
        if (map.length>map[0].length)
            Square.size = screenHeight/(map.length);
        else
            Square.size = (screenWidth/2-50)/(map[0].length);
        Square.startX = (screenWidth/2+(screenWidth/2-Square.size*map[0].length)/2);
        Square.startY = (screenHeight-Square.size*map.length)/2;
        for (int i=0;i<map.length;i++){
            for(int j=0;j<map[0].length;j++){
                squares[i][j] = new Square(this,j*(Square.size)+Square.startX ,i*(Square.size)+Square.startY,map[i][j]);
            }
        }
    }

    static void startGame(Activity activity){
        if(robots!=null)
            for (Robot robot:robots)
                robot.delete();
        robots = new Robot[2];
        robots[0] = new Robot(activity,0,0,2,squares);
        robots[1] = new Robot(activity,squares[0].length-1,squares.length-1,0,squares);
        if(hunter!=null)
            hunter.delete();
        hunter = new Hunter(activity,Math.round(squares[0].length/2),Math.round(squares.length/2),map);
        robots[activeRobot].startAnim();
        if(stuff!=null)
            for (Stuff stf:stuff)
                stf.delete();
        stuff = new Stuff[2];
        for (int i=0;i<stuff.length;i++){
            stuff[i]=new Stuff(activity,(int) Math.round(Math.random()),squares);
        }
    }

    public void onStart(View view) {
        if (!move) {
            robots[activeRobot].stopAnim();
            robots[activeRobot].execute(blocks);
            hunter.steps=blocks.size();
            move = true;
        }
    }

    public void onPrevious(View view) {
        robots[activeRobot].stopAnim();
//        for (Block block: robots[activeRobot].blocks)
//            block.hide();
        do {
            if (activeRobot == 0) activeRobot = robots.length - 1;
            else activeRobot -= 1;
        }
        while (robots[activeRobot].broken);
//        for (Block block: robots[activeRobot].blocks)
//            block.show();
        robots[activeRobot].startAnim();
    }

    public void onNext(View view) {
        robots[activeRobot].stopAnim();
//        for (Block block: robots[activeRobot].blocks)
//            block.hide();
        do {
            if (activeRobot == robots.length - 1) activeRobot = 0;
            else activeRobot += 1;
        }
        while (robots[activeRobot].broken);
//        for (Block block: robots[activeRobot].blocks)
//            block.show();
        robots[activeRobot].startAnim();
    }

    public void update() {
        if (move) {//включается при нажатии ПУСК
            boolean ended=true;
            boolean gameOver=true;
            for (Robot robot: robots){
                if(ended)ended=robot.move();
                else robot.move();
                gameOver=gameOver&&robot.broken;
            }
            if(ended && hunter.moveXY.size()==0 && !gameOver){
                hunter.hunt();//метод, генерирующий путь до ближайшего робота
            }
            if(ended)move=!hunter.botMove();//метод возвращает steps==0
//            if(ended)
////                for (Stuff stuff: stuff)
////                    if(stuff.opened)stuff.delete();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                for (Command command : commands) {
                    refreshBlocks();
                    if (command.touched) {
                        blocks.add(new Block(this, command.x, command.y,command.type));
                        command.touched=false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Block to = null,that=null;
                for (Block block: blocks){
                    if(block.touched) {
                        to=block.setXY(event.getX(), event.getY());
                        that=block;
                    }
                }                //добавление обьекта в список в нужное место
                if(to!=null){
                    blocks.remove(that);
                    blocks.add(blocks.indexOf(to)+1,that);
                    refreshBlocks();
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean ok = false;
                while(!ok) {
                    if(blocks.size()==1){//эта штука проверяет есть ли над командами блок и удаляет его в случае +
                        for (Command com: commands)
                            if(com.isUnderBlock(blocks.get(0))) {
                                blocks.get(0).delete();
                                blocks.remove(blocks.get(0));
                                break;
                            }
                    }
                   for (Block block : blocks) {
                        if (!block.connected && blocks.size() > 1) {
                            block.delete();
                            blocks.remove(block);
                            break;
                        } else block.touched = false;
                    }
                    ok = true;
                    refreshBlocks();
                }
        }
        return true;
    }

     void refreshBlocks(){
        //ориентация всего блока команд по первому
        for(int i=1;i<blocks.size();i++){
            blocks.get(i).setXY(blocks.get(i-1));
        }
    }


    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 1000);
        }
        @Override
        public void onTick(long millisIntilFinished) {
            if (!pause) update();
        }
        @Override
        public void onFinish() {
        }
    }
}
