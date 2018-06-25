package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    static boolean gameOver=false;

    static int activeRobot=0;
    static int comLim=1;
    static int newCom=0;

    static int map[][] = {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0}};
    static Square [][] squares;
    static Robot robots[];
    static Hunter hunter;
    static String AlertDialogMessage;
    boolean pause=false;
    static boolean move=false;
    static Command commands[];
    static ArrayList<Block> blocks;
    static int touchedBlock=-1;//-1 значение, при котором нет тронутых))) блоков
    static Stuff stuff[];
    static int screenWidth,screenHeight;

    static TextView textLim;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TUTOR = "tutor";
    static SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//штука, чтобы экран не выключался :D не знаю зачем
        MyTimer timer = new MyTimer();
        timer.start();
        textLim = findViewById(R.id.stepLim);
        startGame(this);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(!mSettings.contains(APP_PREFERENCES_TUTOR)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_TUTOR, false);
            editor.apply();
        }
        if(!mSettings.getBoolean(APP_PREFERENCES_TUTOR,false )){
            Tutorial tutor = new Tutorial(this);
        }
        else Utils.TwoButtonAllertDialog(this,"Туториал",
                "Вы уже прошли обучение. Хотите повторить?","Нет","Ага");
        commands = new Command[3];
        blocks = new ArrayList<>();
        int step = screenHeight/6+(screenHeight*4/5-Command.size*commands.length)/2;
        for (int i=0;i<commands.length;i++) {
            commands[i] = new Command(this,5 , step+i*(Command.size+10), i);
        }
    }
    public static void createMap(Activity activity){
        map = new int[SettingsActivity.mapSize][SettingsActivity.mapSize];//брать из настроек
        if(squares!=null)
            for (Square[] sqs:squares)
                for (Square sq:sqs)
                    sq.delete();
        squares = new Square[map.length][map[0].length];
        screenWidth = activity.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = activity.getApplicationContext().getResources().getDisplayMetrics().heightPixels-50;
        if (map.length>map[0].length)
            Square.size = screenHeight/(map.length);
        else
            Square.size = (screenWidth/2-50)/(map[0].length);
        Square.startX = (screenWidth/2+(screenWidth/2-Square.size*map[0].length)/2);
        Square.startY = (screenHeight-Square.size*map.length)/2;
        for (int i=0;i<map.length;i++){
            for(int j=0;j<map[0].length;j++){
                squares[i][j] = new Square(activity,j*(Square.size)+Square.startX ,i*(Square.size)+Square.startY,map[i][j]);
            }
        }
    }

    static void startGame(Activity activity){
        gameOver=false;
        createMap(activity);
        if(robots!=null) {
            for (Robot robot : robots)
                robot.delete();
        }
        robots = new Robot[SettingsActivity.robotsNum];//кол-во роботов из настроек
        robots[0] = new Robot(activity,0,0,2,squares);
        if(robots.length>1)robots[1] = new Robot(activity,squares[0].length-1,0,3,squares);
        if(robots.length>2)robots[2] = new Robot(activity,squares[0].length-1,squares.length-1,0,squares);
        if(robots.length>3)robots[3] = new Robot(activity,0,squares[0].length-1,1,squares);
        if(hunter!=null)
            hunter.delete();
        hunter = new Hunter(activity,Math.round(squares[0].length/2),Math.round(squares.length/2),map);
        robots[activeRobot].startAnim();

        //рандомные стены из настроек
        for (int i=0;i<SettingsActivity.wallNum;i++){
            int[]xy=Stuff.randomSqXY();
            map[xy[1]][xy[0]]=2;
            squares[xy[1]][xy[0]].ID_NUMBER=2;
            squares[xy[1]][xy[0]].image.setAlpha(0f);
        }

        if(stuff!=null)
            for (Stuff stf:stuff)
                stf.delete();
        //рандомные вопросы из настроек
        stuff = new Stuff[SettingsActivity.stuffNum];
        for (int i=0;i<stuff.length;i++){
            stuff[i]=new Stuff(activity,(int) Math.round(Math.random()),squares);
        }
        if(blocks !=null){
            for (Block block: blocks)
            block.delete();
            blocks.removeAll(blocks);
        }
        comLim=1;
        newCom=0;
        textLim.setText("comLim "+String.valueOf(comLim-newCom));
    }

    public void onPassStep(View view) {
        if(!move){
            robots[activeRobot].stopAnim();
            hunter.steps=comLim;
            move=true;
        }
        newCom=0;
        do {
            if (activeRobot == robots.length - 1) activeRobot = 0;
            else activeRobot += 1;
        }
        while (robots[activeRobot].broken);
    }

    public void onNextStep(View view) {

        if (!move) {
            robots[activeRobot].stopAnim();
            robots[activeRobot].execute(blocks);
            hunter.steps=comLim;
            move = true;
        }

        for (Block block:blocks) {
            block.newCom = false;
            block.setOld();
        }
        newCom=0;
        do {
            if (activeRobot == robots.length - 1) activeRobot = 0;
            else activeRobot += 1;
        }
        while (robots[activeRobot].broken);
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
            if(ended )move=!hunter.botMove();//метод возвращает steps==0
            if(!move)robots[activeRobot].startAnim();
            textLim.setText("comLim "+String.valueOf(comLim-newCom));
//            if(ended)
////                for (Stuff stuff: stuff)
////                    if(stuff.opened)stuff.delete();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                if(newCom<comLim)for (Command command : commands) {
                    refreshBlocks();
                    if (command.touched) {
                        newCom++;
                        blocks.add(new Block(this, command.x, command.y,command.type,blocks.size()));
                        touchedBlock=blocks.size()-1;
                        command.touched=false;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(GameActivity.touchedBlock!=-1) {
                    Block to = null,that;//to - к чему присоединяет, that - что присоединяет
                    that=blocks.get(touchedBlock);
                    if(that.checkTopConnection(event.getX(),event.getY())) {//проверка на присоединение сверху устанавливает координаты косания
                        blocks.remove(that);
                        blocks.add(0,that);
                        refreshBlocks();
                    }else{
                        to=that.setXY();
                    }
                    //добавление обьекта в список в нужное место
                    if(to!=null){
                        blocks.remove(that);
                        blocks.add(blocks.indexOf(to)+1,that);
                        refreshBlocks();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(blocks.size()==1){//эта штука проверяет есть ли над командами блок и удаляет его в случае +
                    for (Command com: commands)
                        if(com.isUnderBlock(blocks.get(0))) {
                            if(blocks.get(0).newCom)
                                newCom--;
                            blocks.get(0).delete();
                            blocks.remove(blocks.get(0));
                            break;
                        }
                }
                if (touchedBlock!=-1 && blocks.size()>0 &&!blocks.get(touchedBlock).connected) {
                    if(blocks.get(touchedBlock).newCom)newCom--;
                    blocks.get(touchedBlock).delete();
                    blocks.remove(blocks.get(touchedBlock));
                } //else block.touched = false; !!!!!
                touchedBlock=-1;
                refreshBlocks();
                textLim.setText("comLim "+String.valueOf(comLim-newCom));
                //в коде используется кол во новых блоков и лимит на их установку!
        }
        return true;
    }

     void refreshBlocks(){
        //ориентация всего блока команд по первому
         for(int i=0;i<blocks.size();i++){
            blocks.get(i).setNum(i);
            if(i>0)blocks.get(i).setXY(blocks.get(i-1));
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
