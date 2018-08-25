package com.example.ilya2.rob;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Tutorial {

    static boolean isTutorial=false;
    MyTimer timer;
    Activity activity;
    int lesCount=0;
    String steps[];
    int stepNum=0;
    private ArrayList<Stuff> stuffs;
    boolean task;
    Tutorial(Activity main){
        isTutorial = true;
        activity = main;
        timer = new MyTimer();
        timer.start();
        stuffs = new ArrayList<>();
        for (Stuff stf: GameActivity.stuff)
            stf.delete();
        for(Robot robot:GameActivity.robots)
            robot.delete();
        GameActivity.robots[0]=new Robot(main,0,0,2,GameActivity.squares);
        String text = getStringFromAssetFile(activity, "tutorial");
        steps = text.split("#");
    }
    void update(){
        if (task) checkComplTask();
        else if (stepNum == steps.length && !task)//конец туториала
            onTutorComplete();
        else{//следующее сообщение
            Utils.AlertDialog(activity, "Обучение"+ " " + "1" + "." + stepNum, setTask(steps[stepNum]), "оK");
            stepNum++;
        }
    }

    void onTutorComplete(){
        Utils.AlertDialog(activity,"Обучение"+ " " + "1" + "."+stepNum,
                "Поздравляем астрокадет! Вы успешно закончили курсы!","Вперёд, за приключениями!");
    }

    private String setTask(String step) {
        String message;
        if (step.contains("[") && step.contains("]")) {
            message = step.substring(0, step.indexOf("["));
            int stuffCount = 0;
            for (int i = 0; i < step.length(); i++) {
                if (step.charAt(i) == '[') stuffCount++;
            }
            for (int j = 0; j < stuffCount; j++) {
                //для stuff [x|y/type]
                int x = Integer.parseInt(step.substring(step.indexOf("[") + 1, step.indexOf("|")));
                int y = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("/")));
                int type = Integer.parseInt(step.substring(step.indexOf("/") + 1, step.indexOf("]")));
                step = step.substring(step.indexOf("]") + 1, step.length());
                stuffs.add(new Stuff(activity,x,y,type));
            }
            task = true;
        } else {
            task = false;
            message = step.substring(0, step.length());
        }
        return message;
    }

    private boolean checkComplTask() {
        if (stuffs.size() != 0) {
            task = false;
            for (int i = 0; i < stuffs.size(); i++) {
                if (!stuffs.get(i).opened)
                    task = true;
            }
            if (!task) stuffs.clear();
        }
        return task;
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
            if (steps != null)update();
        }
        @Override
        public void onFinish() {
        }
    }
}
