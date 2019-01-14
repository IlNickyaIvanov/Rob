package com.example.ilya2.rob;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Tutorial {
    //прочитай, всяк сюда входящий
    //если надумаешь изменять текст обучения и сценарий, придется менять код, ибо лень делать все универсальным
    //в частности, нужно поменять switch который привязывает table к view и исключение в возрождалке, если нужно
    static boolean isTutorial,task;
    static MyTimer timer;
    private GameActivity activity;
    private ArrayList<String> steps;
    private Table table;
    private int stepNum=0;
    private int rX,rY,r2X,r2Y,hX,hY,direction,direction2,power;//координаты в момент начала выполнения задания
    private ArrayList<int[]> sblocks;//лист содержит типы блоков, из оргинальной последовательности, в методах update и checkComplTask
    //private ArrayList<ArrayList<int[]>> sloops;
    Tutorial(GameActivity main){
        isTutorial = true;
        activity = main;
        timer = new MyTimer();
        timer.start();
        sblocks = new ArrayList<>();
        //sloops = new ArrayList<>();
        //вот эт удаляет весь стафф на карте, ага костыль
        for (Stuff stf: GameActivity.stuff) {
            stf.delete();
        }
        GameActivity.stuff.removeAll(GameActivity.stuff);
        for(int i=0;i<GameActivity.robots.length;i++) {
            GameActivity.robots[i].delete();
        }
        //и создает робота в 0 0
        GameActivity.robots[0]=new Robot(main,0,0,2,GameActivity.squares);
        String text = getStringFromAssetFile(activity, "tutorial");
        steps = new ArrayList<>(Arrays.asList(text.split("#")));
        if(steps.get(0).contains("&"))setMAP();
    }

    void setMAP(){
        int c=0;
        while(steps.get(c).contains("&")){
            c++;
        }
        int map [][] = new int [c][steps.get(0).length()-1];
        for(int i=0;i<c;++i) {
            String line = steps.get(i).replaceAll("\n|\r\n", "");
            for(int j=1;j<line.length();++j){
                map[i][j-1]=Character.getNumericValue(line.charAt(j));
            }
        }
        for(int i=0;i<c;i++){
            steps.remove(0);
        }
        GameActivity.setNewMap(map,activity);
    }

    //метод вызывает новые таблички
    void update(){
        if (task) checkComplTask();
        else if (stepNum == steps.size()) {//конец туториала
            Utils.AlertDialog(activity,"Обучение"+ " " + "1" + "."+stepNum,
                    "Поздравляем астрокадет! Вы успешно закончили курсы!","Вперёд, за приключениями!");
            GameActivity.startGame(activity);
            SharedPreferences.Editor editor = GameActivity.mSettings.edit();
            editor.putBoolean(GameActivity.APP_PREFERENCES_TUTOR, true);
            editor.apply();
            onTutorComplete();
        }
        else {//следующее сообщение
            String msg = setTask(steps.get(stepNum));
            if(Table.isTableVisible)
                table.delete();
            switch (stepNum){
                case 1:
                    table = new Table(activity,msg,GameActivity.robots[0].image,"down");break;
                case 2:
                    new Table(activity,msg,GameActivity.commands[0].image,"right");
                    table = new Table(activity,"",1);
                    break;
                case 3:
                    table = new Table(activity,msg,GameActivity.textLim,"down");break;
                case 4:
                    Utils.AlertDialog(activity, "Обучение"+ " " + "1" + "." + stepNum,
                            msg, "оK");
                    table = new Table(activity,"присоедини к старому блоку новые и запускай",2);
                    break;
                case 5:
                    table = new Table(activity,msg,GameActivity.hunter.image,"down");
                    break;
                case 7:
                    table = new Table(activity,msg,GameActivity.robots[0].image,"down");
                    break;
                default:
                    Utils.AlertDialog(activity, "Обучение"+ " " + "1" + "." + stepNum,
                            msg, "оK");
                    break;
            }
            stepNum++;
        }
    }

    static void  onTutorComplete(){
        isTutorial = false;
        task = false;
        if(timer!=null )timer.cancel();
    }

    private String setTask(String step) {
        String message;
        if (step.contains("[") && step.contains("]")) {
            message = step.substring(0, step.indexOf("["));
            int stuffCount = 0;
            //эт штука считает кол во всего стафа и потом создает его
            for (int i = 0; i < step.length(); i++) {
                if (step.charAt(i) == '[') stuffCount++;
            }
            for (int j = 0; j < stuffCount; j++) {
                //для stuff [x|y/type]
                int x = Integer.parseInt(step.substring(step.indexOf("[") + 1, step.indexOf("|")));
                int y = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("/")));
                int type = Integer.parseInt(step.substring(step.indexOf("/") + 1, step.indexOf("]")));
                step = step.substring(step.indexOf("]") + 1, step.length());
                GameActivity.stuff.add(new Stuff(activity,x,y,type));
            }
            task = true;
            safe();
        } else {
            task = false;
            message = step.substring(0, step.length());
        }
        return message;
    }

    private boolean checkComplTask() {
        if(GameActivity.move)
            return false;
        else if(GameActivity.robots[0].broken && GameActivity.robots[1].broken) {
            //это исключение когда по сценарию в ресурсе молния
            if(stepNum == 7 && GameActivity.robots[0].sqX==GameActivity.stuff.get(0).sqX
                    && GameActivity.robots[0].sqY==GameActivity.stuff.get(0).sqY){
                for(int i=0;i<GameActivity.stuff.size();++i)
                    GameActivity.stuff.remove(0);
                GameActivity.robots[1] = new Robot(activity,GameActivity.squares[0].length-1,GameActivity.squares.length-1,0,GameActivity.squares);
                GameActivity.hunter.findNewActiveRobot();
                GameActivity.robots[0].stopAnim();
                GameActivity.robots[1].startAnim();
                for (Stuff stf:GameActivity.stuff)
                    stf.delete();
                for(int i=0;i<GameActivity.stuff.size();++i)
                    GameActivity.stuff.remove(0);
                task = false;
                return false;
            }
            //стандарт, игрок косячит его возрождают
            Utils.AlertDialog(activity,"УПС","Похоже охотник тебя поймал.\n Попробуй еще разок...","ок");
            GameActivity.robots[0].delete();
            GameActivity.robots[0] = new Robot(activity,rX,rY,direction,GameActivity.squares);
            if(GameActivity.robots[1].image!=null){
                GameActivity.robots[0].setBroken(true);
                GameActivity.robots[1].delete();
                GameActivity.robots[1] = new Robot(activity,r2X,r2Y,direction2,GameActivity.squares);
                GameActivity.robots[1].startAnim();
            }
            else
                GameActivity.robots[0].startAnim();
            for (Stuff stf: GameActivity.stuff){
                stf.close();
            }
            GameActivity.comLim = power;
            if(sblocks.size()>0)rebuildBlocks();
            GameActivity.hunter.setXY(hX,hY);
        }
        else{
            task = false;
            for (int i = 0; i < GameActivity.stuff.size(); i++) {
                task = !GameActivity.stuff.get(i).opened || task;
            }
            //для проверки последнего задания
            if(stepNum==9 && GameActivity.robots[0].broken){
                task=true;
            }
            if(!task){
                for(int i=0;i<GameActivity.stuff.size();++i) {
                    GameActivity.stuff.get(0).delete();
                }
                GameActivity.stuff.removeAll(GameActivity.stuff);
                table.delete();
            }
        }
        return task;
    }

    void safe(){
        rX = GameActivity.robots[0].sqX;
        rY = GameActivity.robots[0].sqY;
        r2X = GameActivity.robots[1].sqX;
        r2Y = GameActivity.robots[1].sqY;
        hX = GameActivity.hunter.sqX;
        hY = GameActivity.hunter.sqY;
        direction = GameActivity.robots[0].direction;
        direction2 = GameActivity.robots[1].direction;
        power = GameActivity.comLim;
        copyBlocks();
    }

    void copyBlocks(){
        sblocks.removeAll(sblocks);
        for(Block block: GameActivity.blocks){
            sblocks.add(new int[]{block.type,(block.newCom)?1:0});
        }
        /*sloops.removeAll(sloops);
        for(int i=0;i<GameActivity.loops.size();i++){
            ArrayList<int[]> loop = new ArrayList<>();
            for (Block block:GameActivity.loops.get(i)){
                loop.add(new int[]{block.type,(block.newCom)?1:0});
            }
            sloops.add(loop);
        }
        */
    }

    void rebuildBlocks(){
        float x,y;
        x = GameActivity.blocks.get(0).x;
        y = GameActivity.blocks.get(0).y;

        for(Block block: GameActivity.blocks){
            block.delete();
        }
        GameActivity.blocks.removeAll(GameActivity.blocks);

        /*for(int i=0;i<GameActivity.loops.size();i++) {
            for (Block block : GameActivity.loops.get(i))
                block.delete();
            GameActivity.loops.get(i).removeAll(GameActivity.loops.get(i));
        }
        GameActivity.loops.removeAll(GameActivity.loops);
        */
        GameActivity.blocks.add(new Block(activity, x, y, sblocks.get(0)[0], 0));
        GameActivity.blocks.get(0).setOld(sblocks.get(0)[1] == 0);

        for(int i=1;i<sblocks.size();++i){
                GameActivity.blocks.add(new Block(activity, 0, 0, sblocks.get(i)[0], 0));
                GameActivity.blocks.get(i).setOld(sblocks.get(i)[1] == 0);
            /*if(GameActivity.blocks.get(i).type==3) {
                GameActivity.loops.add(new ArrayList<Block>());
                GameActivity.loops.get(GameActivity.loops.size()).add(GameActivity.blocks.get(i));
                GameActivity.loops.get(GameActivity.loops.size()).get(0).setNum(GameActivity.loops.size()-1+0.1f);
            }*/
        }

        /*for(int i=0;i<sloops.size();i++){
            for (int j =1;j<sloops.get(i).size();j++) {
                if (sloops.get(i).get(j)[0] != 3){
                    GameActivity.loops.get(i).add(new Block(activity, 0, 0, sloops.get(i).get(j)[0], -1));
                    GameActivity.loops.get(i).get(j).setOld(sblocks.get(i)[1] == 0);
                }else{
                    try{
                        GameActivity.loops.get()
                    }catch (Throwable t){

                    }
                }
            }
        }*/
        GameActivity.refreshBlocks();
    }


    private String getStringFromAssetFile(Activity activity, String filename) {
        byte[] buffer = null;
        InputStream is;
        try {
            is = activity.getAssets().open(filename);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert buffer != null;
        return new String(buffer);
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 1000/60);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            if (steps != null  && !Utils.isADVisible() && !Table.isTableVisible)
                update();
        }
        @Override
        public void onFinish() {
        }
    }
}
