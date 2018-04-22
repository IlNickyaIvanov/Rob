package com.example.ilya2.rob;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    int map[][] = {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0}};
    static Square [][] squares;
    int robotNum=0;
    String commands[];
    Robot robots[];
    Bot bot;
    static String AlertDialogMessage;
    boolean pause=false;
    boolean move=false;
    int counts[],actions[];
    KodParser kodParsers[];
    EditText editText;
    String toast = "Все ок";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTimer timer = new MyTimer();
        timer.start();
        editText = findViewById(R.id.editText);
        startGame();
        robots = new Robot[2];
        kodParsers = new KodParser[2];
        actions = new int[2];
        counts = new int[2];
        commands = new String[2];
        robots[0] = new Robot(this,0,0);
        robots[1] = new Robot(this,squares.length-1,0);
        bot = new Bot(this,squares.length-1,squares.length-1);
        kodParsers[0] = new KodParser(robots[0].sqX,robots[0].sqY,squares,100,this);
        kodParsers[1] = new KodParser(robots[1].sqX,robots[1].sqY,squares,100,this);
        robots[robotNum].startAnim();
    }



    public void startGame(){
        squares = new Square[map.length][map[0].length];
        int screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels-50;
        if (map.length>map[0].length)
            Square.size = screenHeight/2/(map.length);
        else
            Square.size = (screenWidth-50)/(map[0].length);
        Square.startX = (screenWidth-Square.size*map[0].length)/2;
        Square.startY = (screenHeight/2-Square.size*map.length)/2+50;
        for (int i=0;i<map.length;i++){
            for(int j=0;j<map[0].length;j++){
                squares[i][j] = new Square(this,j*(Square.size)+Square.startX ,i*(Square.size)+Square.startY);
            }
        }
    }

    public void onStart(View view) {
        if (!move) {
            robots[robotNum].stopAnim();
            commands[robotNum] = editText.getText().toString();
            actions[0] = kodParsers[0].kodParser(commands[0]);
            actions[1] = kodParsers[1].kodParser(commands[1]);
            move = true;
        }
    }

    public void onPrevious(View view) {
        robots[robotNum].stopAnim();
        commands[robotNum] = editText.getText().toString();
        if(robotNum==0) robotNum=robots.length-1;
        else robotNum-=1;
        editText.setText(commands[robotNum]);
        robots[robotNum].startAnim();
    }

    public void onNext(View view) {
        robots[robotNum].stopAnim();
        commands[robotNum] = editText.getText().toString();
        if(robotNum==robots.length-1) robotNum=0;
        else robotNum+=1;
        editText.setText(commands[robotNum]);
        robots[robotNum].startAnim();
    }

    public void update() {
        //начало выполнения программы
        if (move) {//включается при нажатии ПУСК
            if(counts[0] < actions[0])Handler(0);
            if(counts[1] < actions[1])Handler(1);
            if(counts[0] == 0 && counts[1] == 0)
                move = false;
        }
    }

    void Handler(int num) {
        //если в коде ошибка
        if (AlertDialogMessage != null && kodParsers[0].isKodERROR()) {
            Utils.AlertDialog(this,"Ошибка в коде!", AlertDialogMessage, "ок");
            move = false;
            counts[num] = 0;
            actions[num] = 0;
            kodParsers[num].setAction(0);
        } else if (actions[num] != 0) {
            robots[num].RobotMove(kodParsers[num].ARy[counts[num]], kodParsers[num].ARx[counts[num]]);//перемещение в клетку [sqY][sqX]
            bot.hunt(robots[num].sqX,robots[num].sqY);
            if(bot.sqX == robots[num].sqX && bot.sqY==robots[num].sqY) {
                Utils.AlertDialog(this, "Конец игры", "Вас поймали...", "Заново");
                robots[0].RobotMove(0,0);
                robots[1].RobotMove(0,squares.length-1);
                robots[robotNum].startAnim();
                bot.botMove(squares.length-1,squares.length-1);
            }
            counts[num]++;//перебор элементов массивов "положения" до action
        }
        if (counts[num] >= actions[num]) {//конец движения
            kodParsers[num].action = 0;
            counts[num] = 0;
            robots[robotNum].startAnim();
            if (AlertDialogMessage != null) {
                Utils.AlertDialog(this, getString(R.string.cant), AlertDialogMessage, "ок");
            }
            else Utils.makeToast(this, toast);//отчет об выполении
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
