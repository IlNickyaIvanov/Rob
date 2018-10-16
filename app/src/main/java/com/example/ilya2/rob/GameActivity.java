package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

public class GameActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private long time = 0L;

    int count=0;//это счетчик для таймера
    final static int FPS_FOR_ANIMATION=60;
    static boolean gameOver=false;

    static boolean still;
    static Date longClick;
    int roundX=-1,roundY=-1;//штуки для определения долгого нажатия
    boolean moveALL=false;

    static int activeRobot=0;
    static int comLim=1;
    static int newCom=0;

    static int map[][];
    static Square [][] squares;
    static Robot robots[];
    static Hunter hunter;
    static String AlertDialogMessage;
    static boolean move=false;
    static Command commands[];
    static Command trash;
    static ArrayList<Block> blocks;
    static int touchedBlock=-1;//-1 значение, при котором нет тронутых))) блоков
    static ArrayList<Stuff>stuff;
    static int screenWidth,screenHeight;

    static TextView textLim;
    TextView t;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_TUTOR = "tutor";
    static SharedPreferences mSettings;

    Logger log = Logger.getLogger(GameActivity.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//штука, чтобы экран не выключался :D не знаю зачем
        if (time == 0L) {
            time = SystemClock.uptimeMillis(); // вычисляем время
            handler.removeCallbacks(myRun);
            handler.postDelayed(myRun, 100); // через столько милисикунд стартует
        }
        textLim = findViewById(R.id.stepLim);
        t = findViewById(R.id.just_move);
        t.setText("Просто переместите блок сюда...");
        startGame(this);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(!mSettings.contains(APP_PREFERENCES_TUTOR)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_TUTOR, false);
            editor.apply();
        }
        if(!mSettings.getBoolean(APP_PREFERENCES_TUTOR,false )){
            new Tutorial(this);
        }
        else if(StartActivity.sw.isChecked())Utils.TwoButtonAllertDialog(this,"Туториал",
                "Вы уже прошли обучение. Хотите повторить?","Нет","Ага");

        commands = new Command[3];
        blocks = new ArrayList<>();
        int step = screenHeight/6+(screenHeight*4/5-Command.size*commands.length)/2;
        trash = new Command(this,5,step-Command.size-5,-1);
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

    static void setNewMap(int [][] mapy,Activity activity){
        for (Square[] sqs:squares)
            for (Square sq:sqs)
                sq.delete();
        map = mapy;
        squares = new Square[map.length][map[0].length];
        Square.startX = (screenWidth/2+(screenWidth/2-Square.size*map[0].length)/2);
        Square.startY = (screenHeight-Square.size*map.length)/2;
        for(int i=0;i<map.length;i++){
            for(int j=0;j<map[0].length;j++){
                squares[i][j] = new Square(activity,j*(Square.size)+Square.startX ,i*(Square.size)+Square.startY,map[i][j]);
            }
        }
        for(int i = 0;i<robots.length;++i){
            if(robots[i].image==null)continue;
            robots[i].delete();
            robots[i] = new Robot(activity,robots[i].sqX,robots[i].sqY,robots[i].direction,squares);
        }
        hunter.delete();
        hunter = new Hunter(activity,hunter.sqX,hunter.sqY,map);
    }

    static void startGame(Activity activity){
        activeRobot=0;
        gameOver=false;
        createMap(activity);
        if(robots!=null) {
            for (Robot robot : robots)
                robot.delete();
        }
        robots = new Robot[SettingsActivity.robotsNum];//кол-во роботов из настроек
        robots[0] = new Robot(activity,0,0,2,squares);
        if(robots.length==2)robots[1] = new Robot(activity,squares[0].length-1,squares.length-1,0,squares);
        else if(robots.length>1) robots[1] = new Robot(activity,squares[0].length-1,0,3,squares);
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

        if(stuff!=null) {
            for (Stuff stf : stuff)
                stf.delete();
            stuff.removeAll(stuff);
        }
        else stuff = new ArrayList<>();
        //рандомные вопросы из настроек
        for (int i = 0; i< SettingsActivity.stuffNum; i++){
            stuff.add(new Stuff(activity,(float)Math.random()));
        }
        if(blocks !=null){
            for (Block block: blocks)
                block.delete();
            blocks.removeAll(blocks);
        }
        comLim=1;
        newCom=0;
        textLim.setText("Энергия "+String.valueOf(comLim-newCom));
    }

    public void onPassStep(View view) {
        if(!move){
            robots[activeRobot].stopAnim();
            hunter.steps=comLim;
            move=true;
            count=-1;
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
            count=-1;
        }

        for (Block block:blocks) {
            block.newCom = false;
            block.setOld(true);
        }
        newCom=0;
        do {
            if (activeRobot == robots.length - 1) activeRobot = 0;
            else activeRobot += 1;
        }
        while (robots[activeRobot].broken);
    }

    //единый таймер для всей игры
    public void update() {
        if (count>60 || count==-1) {//включается при нажатии ПУСК
            boolean ended= true;//true когда робот закончил движение
            boolean gameOver=true;
            boolean isStillAnimate=false;//проверка на окнчание анимации у всех роботов
            for (int i=0;i<robots.length;i++){
                if(ended)ended=robots[i].move();
                else robots[i].move();
                isStillAnimate=isStillAnimate||robots[i].anim;
                gameOver=gameOver&&robots[i].broken;//gameOver если все роботы сломаны
            }
            if(!isStillAnimate && ended) {
                if (hunter.moveXY.size() == 0 && !gameOver && hunter.steps > 0) {
                    hunter.hunt();//метод, генерирующий путь до ближайшего робота
                }
                move = !hunter.botMove();//метод возвращает steps==0
                move = hunter.anim;
                if (!move) {
                    count = 1;
                    robots[activeRobot].startAnim();
                }
                textLim.setText("Энергия " + String.valueOf(comLim - newCom));
                count=0;
            }}
        hunter.update();
        for (int i=0;i<robots.length;++i)
            robots[i].update();
        ++count;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                //log.info("log: Touch event\n");
                if(newCom>=comLim)break;
                for (int i = 0; i<commands.length;++i)
                    if (commands[i].touched) {
                        newCom++;
                        t.setText("");
                        blocks.add(new Block(this, commands[i].x, commands[i].y,commands[i].type,blocks.size()));
                        touchedBlock=blocks.size()-1;
                        commands[i].touched=false;
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(touchedBlock!=-1){
                    if(trash.image.getAlpha()!=1f && blocks.size()==1)
                        trash.startAnim();
                    if(moveALL) {
                        moveBlocks(event.getX(), event.getY());
                        break;
                    }//установка координатов касания
                    int size=blocks.get(touchedBlock).size;
                    blocks.get(touchedBlock).setBlockXY(event.getX()-size/2,event.getY()-size/2);

                    if(still)
                        checkLongClick(event.getX()-size/2,event.getY()-size/2);

                    Block to = null, that = blocks.get(touchedBlock);//to - к чему присоединяет, that - что присоединяет
                    if(that.discon==null)that.setDiscon();
                    if (that.checkTopConnection(event.getX(), event.getY())) {//проверка на присоединение сверху устанавливает координаты косания
                        blocks.remove(that);
                        blocks.add(0, that);
                        refreshBlocks();
                        log.info("log: connected!\n");
                    } else {//установка координат косания и проверка на присоединение снизу
                        to = that.setXY();
                    }
                    //добавление обьекта в список в нужное место
                    if (to != null) {
                        log.info("log: connected!\n");
                        blocks.remove(that);
                        blocks.add(blocks.indexOf(to) + 1, that);
                        refreshBlocks();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //log.info("log: Up event\n");
                if(blocks.size()==1){//эта штука проверяет есть ли над командами блок и удаляет его в случае +
                    for (Command com: commands)
                        if(com.isUnderBlock(blocks.get(0))) {
                            if(blocks.get(0).newCom)
                                newCom--;
                            blocks.get(0).delete();
                            blocks.remove(blocks.get(0));
                            touchedBlock=-1;
                            break;
                        }
                        if(blocks.size()==0)
                            t.setText("Просто переместите блок сюда...");
                }
                if(blocks.size()==1 && touchedBlock!=-1 && trash.isUnderBlock(blocks.get(touchedBlock))){
                    if(blocks.get(touchedBlock).newCom)
                        newCom--;
                    blocks.get(touchedBlock).delete();
                    blocks.remove(blocks.get(0));
                    touchedBlock=-1;
                    break;
                }
                if(trash.image.getAlpha()!=0f)
                    trash.stopAnim();
                //удаление не присоединенного блока
                if (touchedBlock!=-1 && blocks.size()>0 &&!blocks.get(touchedBlock).connected && !moveALL) {
                    if(blocks.get(touchedBlock).newCom)newCom--;
                    blocks.get(touchedBlock).delete();
                    blocks.remove(blocks.get(touchedBlock));
                } //else block.touched = false; !!!!!
                touchedBlock=-1;

                moveALL=false;
                longClick = null;
                still = false;

                refreshBlocks();
                textLim.setText("Энергия "+String.valueOf(comLim-newCom));
                //в коде используется кол-во новых блоков и лимит на их установку!
        }
        return true;
    }

     static void refreshBlocks(){
        //ориентация всего блока команд по первому
         for(int i=0;i<blocks.size();i++){
            blocks.get(i).setNum(i);
            if(i>0)blocks.get(i).setXY(blocks.get(i-1));
         }
    }

    void checkLongClick(float x,float y){
        if(roundX==-1 && roundY==-1){
            roundX = Math.round(x-x%100);
            roundY = Math.round(y-y%100);
        }else if(roundX!=Math.round(x-x%100) || roundY!=Math.round(y-y%100)){
            still=false;
            longClick = null;
            roundX = -1;
            roundY = -1;
        }
        if(still && (new Date()).getTime()-longClick.getTime()>500) {
            moveALL = true;
            still = false;
            blocks.get(touchedBlock).connected = true;
            blocks.get(touchedBlock).discon = null;
            long mills = 100L;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(mills);
            }
        }

    }

    void moveBlocks(float x,float y){
        float deltaY = blocks.get(touchedBlock).y-y;
        blocks.get(0).setBlockXY(x,blocks.get(0).y-deltaY);
        refreshBlocks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        move = false;
        Tutorial.onTutorComplete();
    }

    private Runnable myRun = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000/FPS_FOR_ANIMATION);
            if(move)update(); // вызываем обновлялку игры
            if(roundX!=-1 && roundY!=-1)
                checkLongClick(roundX,roundY);
        }
    };

}
