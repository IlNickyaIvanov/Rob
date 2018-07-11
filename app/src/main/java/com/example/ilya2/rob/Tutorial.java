package com.example.ilya2.rob;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

public class Tutorial {

    static boolean isTutorial=false;
    MyTimer timer;
    Activity activity;
    String lessons []= {"Добро Пожаловать, дорогой друг!\nДавай скорее начнем)))","Цель игры - собрать все знаки вопроса,\n" +
            " для этого нужно давать роботу команды, с помощью блоков слева...","Попробуй собрать этот...\n P.S. Для запуска алгоритма - жми step>",
    "В реальной игре ты будешь управлять 4 роботами сразу!\n Собирай вопросы, чтобы увеличить лимит новых команд за ход.","И главное, не дай охотнику поймать всех роботов! Удачи!" +
            "\n P.S. Также ты можешь пропускать ход: PASS>>"};
    int lesCount=0;

    Tutorial(Activity main){
        isTutorial = true;
        activity = main;
        timer = new MyTimer();
        timer.start();
        for (Stuff stf: GameActivity.stuff)
            stf.delete();
        GameActivity.robots[1].delete();
        GameActivity.comLim = 3;
    }
    void update(){
        if(!Utils.isADVisible() && lesCount<lessons.length && isGameOver()){
            Utils.AlertDialog(activity,"Туториал",lessons[lesCount],"Вперёд");
            if(lesCount==2){
                GameActivity.stuff = new Stuff[1];
                GameActivity.stuff[0] = new Stuff(activity,1);
                GameActivity.stuff[0].setXY();
            }
            if(lesCount==3) GameActivity.move = false;
            lesCount++;
        }else if(GameActivity.robots[0].broken && isTutorial){
            Utils.AlertDialog(activity,"Туториал","Охотник тебя поймал...попробуй по-другому :с","Еще раз...");
            GameActivity.robots[0].delete();
            GameActivity.robots[0] = new Robot(activity,0,0,2, GameActivity.squares);
            GameActivity.hunter.setXY();
        }else if(lesCount==lessons.length && !Utils.isADVisible() && isGameOver()){
                isTutorial=false;
                SharedPreferences.Editor editor = GameActivity.mSettings.edit();
                editor.putBoolean(GameActivity.APP_PREFERENCES_TUTOR, true);
                editor.apply();
                timer.cancel();
                Utils.AlertDialog(activity,"Туториал","Поздравляем! Вы прошли обучение...","Вперёд.");
        }
    }

    boolean isGameOver(){
        boolean gameOver=true;
        for (Stuff stuff: GameActivity.stuff) {
            gameOver=gameOver&&stuff.opened;
        }
        return gameOver;
    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 1000/60);
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
