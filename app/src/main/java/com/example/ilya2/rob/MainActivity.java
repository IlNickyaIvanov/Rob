package com.example.ilya2.rob;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    static int activeRobot=0;

    int map[][] = {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0}};
    static Square [][] squares;
    static Robot robots[];
    Bot bot;
    static String AlertDialogMessage;
    boolean pause=false;
    boolean move=false;
    static Command commands[];
    static Stuff stuff[];
    static int screenWidth,screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTimer timer = new MyTimer();
        timer.start();
        startGame();
        robots = new Robot[2];
        robots[0] = new Robot(this,0,0,2,squares);
        robots[1] = new Robot(this,squares[0].length-1,squares.length-1,0,squares);
        bot = new Bot(this,Math.round(squares[0].length/2),Math.round(squares.length/2));
        robots[activeRobot].startAnim();

        stuff = new Stuff[2];
        for (int i=0;i<stuff.length;i++){
            stuff[i]=new Stuff(this,(int) Math.round(Math.random()),squares);
        }
        commands = new Command[3];
        int step = screenHeight/6+(screenHeight*4/5-Command.size*commands.length)/2;
        for (int i=0;i<commands.length;i++) {
            commands[i] = new Command(this,5 , step+i*(Command.size+10), i);
        }
    }



    public void startGame(){
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
                squares[i][j] = new Square(this,j*(Square.size)+Square.startX ,i*(Square.size)+Square.startY);
            }
        }
    }

    public void onStart(View view) {
        if (!move) {
            robots[activeRobot].stopAnim();
            for (Robot robot:robots)
                robot.execute();
            move = true;
        }
    }

    public void onPrevious(View view) {
        robots[activeRobot].stopAnim();
        for (Block block: robots[activeRobot].blocks)
            block.hide();
        if(activeRobot ==0) activeRobot =robots.length-1;
        else activeRobot -=1;
        for (Block block: robots[activeRobot].blocks)
            block.show();
        robots[activeRobot].startAnim();
    }

    public void onNext(View view) {
        robots[activeRobot].stopAnim();
        for (Block block: robots[activeRobot].blocks)
            block.hide();
        if(activeRobot ==robots.length-1) activeRobot =0;
        else activeRobot +=1;
        for (Block block: robots[activeRobot].blocks)
            block.show();
        robots[activeRobot].startAnim();
    }

    public void update() {
        if (move) {//включается при нажатии ПУСК
            boolean ended=true;
            for (Robot robot: robots){
                if(ended)ended=robot.move();
                else robot.move();
            }
            move=!ended;
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
                        robots[activeRobot].blocks.add(new Block(this, command.x, command.y,command.type));
                        command.touched=false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Block to = null,that=null;
                for (Block block: robots[activeRobot].blocks){
                    if(block.touched) {
                        to=block.setXY(event.getX(), event.getY());
                        that=block;
                    }
                }                //добавление обьекта в список в нужное место
                if(to!=null){
                    robots[activeRobot].blocks.remove(that);
                    robots[activeRobot].blocks.add(robots[activeRobot].blocks.indexOf(to)+1,that);
                    refreshBlocks();
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean ok = false;
                while(!ok) {
                    if(robots[activeRobot].blocks.size()==1){//эта штука проверяет есть ли над командами блок и удаляет его в случае +
                        for (Command com: commands)
                            if(com.isUnderBlock(robots[activeRobot].blocks.get(0))) {
                                robots[activeRobot].blocks.get(0).delete();
                                robots[activeRobot].blocks.remove(robots[activeRobot].blocks.get(0));
                                break;
                            }
                    }
                   for (Block block : robots[activeRobot].blocks) {
                        if (!block.connected && robots[activeRobot].blocks.size() > 1) {
                            block.delete();
                            robots[activeRobot].blocks.remove(block);
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
        for(int i=1;i<robots[activeRobot].blocks.size();i++){
            robots[activeRobot].blocks.get(i).setXY(robots[activeRobot].blocks.get(i-1));
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
