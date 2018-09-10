package com.example.ilya2.rob;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Tutorial {

    static boolean isTutorial,task;
    static MyTimer timer;
    private Activity activity;
    private ArrayList<String> steps;
    private int stepNum=0;
    private int rX,rY,hX,hY,direction,power;//координаты в момент начала выполнения задания
    private ArrayList<Integer> sblocks;//лист содержит типы блоков, из оргинальной последовательности
    Tutorial(Activity main){
        isTutorial = true;
        activity = main;
        timer = new MyTimer();
        timer.start();
        sblocks = new ArrayList<>();
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
            Utils.AlertDialog(activity, "Обучение"+ " " + "1" + "." + stepNum, setTask(steps.get(stepNum)), "оK");
            stepNum++;
        }
    }

    static void  onTutorComplete(){
        isTutorial = false;
        task = false;
        timer.cancel();
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
            if(stepNum == 8){
                for(int i=0;i<GameActivity.stuff.size();++i)
                    GameActivity.stuff.remove(0);
                GameActivity.robots[1] = new Robot(activity,GameActivity.squares[0].length-1,GameActivity.squares.length-1,0,GameActivity.squares);
                GameActivity.hunter.findNewActiveRobot();
                task = false;
                return false;
            }
            Utils.AlertDialog(activity,"УПС","Похоже охотник тебя поймал.\n Попробуй еще разок...","ок");
            GameActivity.robots[0].delete();
            GameActivity.robots[0] = new Robot(activity,rX,rY,direction,GameActivity.squares);
            for (Stuff stf: GameActivity.stuff){
                stf.close();
            }
            GameActivity.comLim = power;
            rebuildBlocks();
            GameActivity.hunter.setXY(hX,hY);
        }
        else{
            task = false;
            for (int i = 0; i < GameActivity.stuff.size(); i++) {
                task = !GameActivity.stuff.get(i).opened || task;
            }
            if(!task){
                for (Stuff stf:GameActivity.stuff)
                    stf.delete();
                for(int i=0;i<GameActivity.stuff.size();++i)
                    GameActivity.stuff.remove(0);
            }
        }
        return task;
    }

    void safe(){
        rX = GameActivity.robots[0].sqX;
        rY = GameActivity.robots[0].sqY;
        hX = GameActivity.hunter.sqX;
        hY = GameActivity.hunter.sqY;
        direction = GameActivity.robots[0].direction;
        power = GameActivity.comLim;
        copyBlocks();
    }

    void copyBlocks(){
        for(Block block: GameActivity.blocks){
            sblocks.add(block.type);
        }
    }

    void rebuildBlocks(){
        float x,y;
        x = GameActivity.blocks.get(0).x;
        y = GameActivity.blocks.get(0).y;
        for(Block block: GameActivity.blocks){
            block.delete();
        }
        GameActivity.blocks.removeAll(GameActivity.blocks);
        GameActivity.blocks.add(new Block(activity,x,y,sblocks.get(0),0));
        GameActivity.blocks.get(0).setOld();
        for(int i=1;i<sblocks.size();++i){
            GameActivity.blocks.add(new Block(activity,0,0,sblocks.get(i),0));
            GameActivity.blocks.get(i).setOld();
        }
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
            if (steps != null  && !Utils.isADVisible())update();
        }
        @Override
        public void onFinish() {
        }
    }
}
