package com.example.ilya2.rob;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    static final int startEnergy = 1;
    static int comLim=startEnergy;
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

    static ArrayList<ArrayList<Block>> loops;
    static int loopNum=-1;
    static float touchedBlock=-1;//-1 значение, при котором нет тронутых))) блоков
    // + может быть дробным, где целая часть - номер цикла, а дробная номер блока в цикле
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

        commands = new Command[4];
        blocks = new ArrayList<>();
        loops = new ArrayList<>();
        int step = screenHeight/7+(screenHeight*6/7-Command.size*commands.length)/2;
        trash = new Command(this,squares[0][0].x-Command.size,screenHeight-Command.size*1.5f,-1);
        for (int i=0;i<commands.length;i++) {
            commands[i] = new Command(this,5 , step+i*(Command.size+10), i);
        }
    }
    public static void createMap(GameActivity activity){
        map = new int[SettingsActivity.mapSize][SettingsActivity.mapSize];//брать из настроек
        if(SettingsActivity.switchUseMap!=null && SettingsActivity.switchUseMap.isChecked()){
            for(int i=0;i<SettingsActivity.map.size();i++){
                for(int j=0;j<SettingsActivity.map.get(0).size();j++){
                    map[i][j] = SettingsActivity.map.get(i).get(j).ID_NUMBER;
                }
            }
            stuff = new ArrayList<>();
        }
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
                if(Math.round(map[0].length/2)!=j || Math.round(map.length/2)!=i) {//чтобы не ущемлять хантера
                    squares[i][j] = new Square(activity, j * (Square.size) + Square.startX, i * (Square.size) + Square.startY, map[i][j]);
                    if (map[i][j] == 3)
                        stuff.add(new Stuff(activity, j, i, (Math.random() > 0.1) ? 1 : 0));
                }else
                    squares[i][j] = new Square(activity, j * (Square.size) + Square.startX, i * (Square.size) + Square.startY, 0);
            }
        }
    }

    static void setNewMap(int [][] mapy,GameActivity activity){
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

    static void startGame(GameActivity activity){
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

        if(SettingsActivity.switchUseMap==null || !SettingsActivity.switchUseMap.isChecked()) {
            //рандомные стены из настроек
            for (int i = 0; i < SettingsActivity.wallNum; i++) {
                int[] xy = Stuff.randomSqXY();
                map[xy[1]][xy[0]] = 2;
                squares[xy[1]][xy[0]].ID_NUMBER = 2;
                squares[xy[1]][xy[0]].image.setAlpha(0f);
            }

            if (stuff != null) {
                for (Stuff stf : stuff)
                    stf.delete();
                stuff.removeAll(stuff);
            } else stuff = new ArrayList<>();
            //рандомные вопросы из настроек
            for (int i = 0; i < SettingsActivity.stuffNum; i++) {
                stuff.add(new Stuff(activity, (float) Math.random()));
            }
        }
        if(blocks !=null){
            for (Block block: blocks)
                block.delete();
            blocks.removeAll(blocks);
        }
        if(loops!=null) {
            for (ArrayList<Block> loop : loops) {
                for (int i = 0; i < loop.size(); i++)
                    loop.get(i).delete();
                loop.removeAll(loop);
            }
            loops.removeAll(loops);
        }
        comLim=startEnergy;
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

                        if(commands[i].type==3) {
                            loops.add(new ArrayList<Block>());
                            loops.get(loops.size()-1).add(blocks.get(blocks.size()-1));
                            loops.get(loops.size()-1).get(0).setNum(loops.size()-1+0.1f);
                        }

                        touchedBlock=blocks.size()-1;
                        commands[i].touched=false;
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(touchedBlock!=-1){
                    if(trash.image.getAlpha()!=1f)
                        trash.startAnim();
                    if(moveALL) {
                        moveBlocks(event.getX(), event.getY());
                        break;
                    }
                    int size=blocks.get(0).size;
                    if(still)
                        checkLongClick(event.getX()-size/2,event.getY()-size/2);

                    Block to = null, that=getThat();//to - к чему присоединяет, that - что присоединяет

                    //установка координатов касания
                    that.setBlockXY(event.getX()-size/2,event.getY()-size/2);

                    if(that.discon==null)that.setDiscon();
                    if (that.checkTopConnection(event.getX(), event.getY())) {//проверка на присоединение сверху устанавливает координаты касания
                        if(that.num!=-1)
                            blocks.remove(that);
                        else
                            loops.get(loopNum).remove(that);
                        blocks.add(0, that);
                        refreshBlocks();
                        log.info("log: connected!\n");
                    } else {//установка координат косания и проверка на присоединение снизу
                        to = that.setXY();
                    }
                    //добавление обьекта в список в нужное место
                    if (to != null) {
                        log.info("log: connected!\n");
                        //если изначально блок был в главной
                        if(that.num!=-1)
                            blocks.remove(that);
                        else // или в цикле loopNum
                            loops.get(loopNum).remove(that);
                        //если присоединение в цикле, то loopNumO !=-1
                        if(that.loopNumO ==-1) {
                            blocks.add(blocks.indexOf(to) + 1, that);
                            refreshBlocks();
                        //во внутренний
                        }else if(to.loopNumI ==-1){
                            loops.get((int)to.loopNumO).add( loops.get((int)to.loopNumO).indexOf(to)+1,that);
                            //refreshLoop((int)to.loopNumO);
                            loopNum=-1;
                        //или внешний
                        }else{
                            loops.get((int)to.loopNumI).add( loops.get((int)to.loopNumI).indexOf(to)+1,that);
                            //refreshLoop((int)to.loopNumI);
                            loopNum=-1;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //log.info("log: Up event\n");
                Block that=getThat();// that - этот блок
                if(blocks.size()==1 && that == blocks.get(0)){//эта штука проверяет есть ли над командами блок и удаляет его в случае +
                    for (Command com: commands)
                        if(com.isUnderBlock(blocks.get(0))) {
                            blocks.get(0).delete();
                            blocks.remove(blocks.get(0));
                            if(touchedBlock%1!=0) {
                                removeLoop((int)touchedBlock);
                            }
                            touchedBlock=-1;
                            break;
                        }
                        if(blocks.size()==0)
                            t.setText("Просто переместите блок сюда...");
                    //Удаление над корзиной
                    if(touchedBlock!=-1 && trash.isUnderBlock(that)){
                        that.delete();
                        blocks.remove(blocks.get(0));
                        if(that.type==3) {
                            removeLoop((int)touchedBlock);
                        }
                        touchedBlock=-1;
                        break;
                    }
                }
                if(trash.image.getAlpha()!=0f)
                     trash.stopAnim();
                //удаление не присоединенного блока
                if(blocks.size()==1 && blocks.get(0).type==3 && that == blocks.get(0))
                    that.connected=true;
                if (touchedBlock!=-1 && blocks.size()>0 &&!that.connected && !moveALL) {
                    that.delete();
                    if(touchedBlock%1==0)
                        blocks.remove(that);
                    else {
                        if(that.num!=-1){
                            blocks.remove(that);
                        }
                        if(that.type==3){
                            removeLoop((that.loopNumI!=-1)?(int)that.loopNumI:(int)that.loopNumO);
                            //newCom++;
                            }
                        else loops.get((int)touchedBlock).remove(that);
                        //if(loops.get((int)touchedBlock).size()==0)
                           // loops.remove((int)touchedBlock);
                    }
                } //else block.touched = false; !!!!!
                touchedBlock=-1;
                loopNum=-1;

                moveALL=false;
                longClick = null;
                still = false;

                refreshBlocks();
                getThat();
                textLim.setText("Энергия "+String.valueOf(comLim-newCom));
                //в коде используется кол-во новых блоков и лимит на их установку!
        }
        return true;
    }

     static void refreshBlocks(){
        //ориентация всего блока команд по первому
         for(int i=0;i<blocks.size();i++){
            blocks.get(i).setNum(i);
            if(i>0) blocks.get(i).setXY(blocks.get(i-1));//xy!
         }
         ArrayList<Integer>nums=new ArrayList<>();
         for(int i=0;i<loops.size();i++){
             if(nums.contains(i))continue;
             refreshLoop(i);
             nums.addAll(setLoopBack(i));
         }
    }
    static int refreshLoop(int loopNum){
        //ориентация всего блока команд по первому
        int delta=0;
        int c=1;//сколько подциклов
        for(int i=0;i<loops.get(loopNum).size();i++){
            if(loopNum != (int)loops.get(loopNum).get(i).loopNumI)//чтобы не менять номер внешнего цикла, если мы внутри него
                loops.get(loopNum).get(i).setNum(loopNum + (float) (i + 1) / 10);
            if(i>0){
                loops.get(loopNum).get(i).setYX(loops.get(loopNum).get(i - 1),delta);//yx!
                if(loops.get(loopNum).get(i).type==3) {
                    delta += refreshLoop((int) loops.get(loopNum).get(i).loopNumI);
                    c++;
                }
            }
        }
        loops.get(loopNum).get(0).loopSize = delta + (loops.get(loopNum).size())*(Block.size)+c*10;
        return delta + (loops.get(loopNum).size()-1)*(Block.size)+c*10;
    }

    static ArrayList<Integer> setLoopBack(int loopNum){
        ArrayList<Integer> nums = new ArrayList<>();
        nums.add(loopNum);
        loops.get(loopNum).get(0).setLoopBackground();
        for (int i=1;i<loops.get(loopNum).size();i++){
            loops.get(loopNum).get(i).bringToTheFront();
            if(loops.get(loopNum).get(i).type==3)
                nums.addAll(setLoopBack((int) loops.get(loopNum).get(i).loopNumI));

        }
        return nums;
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
        Block that = getThat();// that - этот блок
        if(still && (new Date()).getTime()-longClick.getTime()>500) {
            moveALL = true;
            still = false;
            that.connected = true;
            that.discon = null;
            long mills = 100L;
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(mills);
            }
        }

    }

    void removeLoop(int loopNum){
        //удаление во внешнем цикле если мы во внутреннем
        if(loopNum == (int)loops.get(loopNum).get(0).loopNumI)
            loops.get((int)loops.get(loopNum).get(0).loopNumO).remove(loops.get(loopNum).get(0));
        int startSize = loops.get(loopNum).size();//чтобы не залезть в тот же цикл по первму блоку
        while (loops.get(loopNum).size()!=0){
            if(loops.get(loopNum).get(0).type==3 && loops.get(loopNum).size()!=startSize)
                removeLoop((int)loops.get(loopNum).get(0).loopNumI);
            else {
                loops.get(loopNum).get(0).delete();
                loops.get(loopNum).remove(0);
            }
        }
        loops.remove(loopNum);
    }

    void moveBlocks(float x,float y){
        Block that = getThat();// that - этот блок
        float deltaY = that.y-y;
        blocks.get(0).setBlockXY(x,blocks.get(0).y-deltaY);
        refreshBlocks();
    }

    static int afterPoint(float a){
        String t = String.valueOf(a);
        return Integer.valueOf(t.substring(t.indexOf('.')+1));
    }

    Block getThat(){
        Block that=null;
        if(touchedBlock%1!=0){
            that = loops.get((int)touchedBlock).get(afterPoint(touchedBlock)-1);
        }else if(touchedBlock!=-1)that = blocks.get((int)touchedBlock);
        return that;
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
