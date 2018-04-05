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
            {0,0,0,0,0},};
    Square [][] squares;
    static Robot robot;
    static String AlertDialogMessage;
    boolean pause=false;
    boolean move=false;
    int count,action;
    KodParser kodParser;
    EditText editText;
    String toast = "Все ок";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTimer timer = new MyTimer();
        timer.start();
        squares = new Square[map.length][map[0].length];
        editText = findViewById(R.id.editText);
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
        robot = new Robot(this,squares[Math.round(map.length/2)][Math.round(map[0].length/2)].x,squares[Math.round(map.length/2)][Math.round(map[0].length/2)].y);
        kodParser = new KodParser(Math.round(map.length/2),Math.round(map[0].length/2),squares,100,this);
    }

    public void onStart(View view) {
        if (!move) {
            //при туториале возвращение в стартовые только мешает
            String text = editText.getText().toString();
            //а вот здесь самое интересное)
            action = kodParser.kodParser(text);
            move = true;
        }

    }

    public void update() {
        //начало выполнения программы
        if (move) {//включается при нажатии ПУСК
            Handler();
        } else {
            robot.MoveMySelf(false);
        }
        //конец списка команд
        if (count >= action && move) {//конец движения
            //restartBLINKY();
            move = false;
            kodParser.action = 0;
            count = 0;

            if (AlertDialogMessage != null) {
                Utils.AlertDialog(this, getString(R.string.cant), AlertDialogMessage, "ок");
                editText.setSelection(kodParser.start, kodParser.stop);
            }
            //по прохождению уровня...
//            else if (checkTask())
//                if (!getIntent().getBooleanExtra("own_level", false))
//                    Utils.TwoButtonAllertDialog(this, getString(R.string.level) + " " + level_name + getString(R.string.compl2),
//                            onComplete,
//                            getString(R.string.menu), getString(R.string.next), LEVEL_NUM);
//                else
//                    Utils.AlertDialog(this, getString(R.string.level) + " " + level_name + getString(R.string.compl2),
//                            onComplete,
//                            getString(R.string.ok));
//            else if (!Tutorial.task) {
//                Utils.makeToast(this, getString(R.string.try_more));
// }
            else Utils.makeToast(this, toast);//отчет об выполении

        }

    }

    void Handler() {
        //если в коде ошибка
        if (AlertDialogMessage != null && kodParser.isKodERROR()) {
            Utils.AlertDialog(this,"Ошибка в коде!", AlertDialogMessage, "ок");
            editText.setSelection(kodParser.start, kodParser.stop);
            move = false;
            count = 0;
            action = 0;
            kodParser.setAction(0);
        } else if (action != 0) {
            //вот здесь и запускается то, что видет пользователь
//            if (kodParser.Anim[count] != 0) {
//                if (kodParser.Anim[count] == 5) for (int i = 0; i < foodSquares.size(); i++) {
//                    int food[] = foodSquares.get(i);
//                    if ((kodParser.ARy[count]) == food[0] && (kodParser.ARx[count]) == food[1] && !squares[food[0]][food[1]].food.isEaten())
//                        squares[food[0]][food[1]].EAT();
//                }
//                else robot.SearchAnim(kodParser.Anim[count]);
//                kodParser.Anim[count] = 0;
//            }
            robot.RobotMove(
                    squares[(kodParser.ARy[count])][(kodParser.ARx[count])].y,
                    squares[(kodParser.ARy[count])][(kodParser.ARx[count])].x,
                    kodParser.ARy[count], kodParser.ARx[count], false);//перемещение в клетку [sqY][sqX]
            count++;//перебор элементов массивов "положения" до action
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
