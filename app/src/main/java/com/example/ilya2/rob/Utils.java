package com.example.ilya2.rob;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.widget.Toast;
//полезные и универсальные методы
class Utils {
    private static boolean ADPositBut;
    private static boolean ADVisible;
    static void makeToast(Activity main, String text){
        Toast.makeText(main,text,Toast.LENGTH_SHORT).show();
    }
    public static void AlertDialog(final Activity main, String title, String Message, final String TextButton ){
        GameActivity.AlertDialogMessage=null;
        //if(TextButton.equals("Еще раз")) GameActivity.gameOver = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton(TextButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if(TextButton.equals("Еще раз") || TextButton.equals("Вперёд."))
                                    GameActivity.startGame(main);
                                ADVisible=false;
                            }
                        });

        AlertDialog alert = builder.create();
        if(!( main).isFinishing())
        {
            alert.show();
            ADVisible=true;
        }
    }

    static AlertDialog.Builder TwoButtonAllertDialog(final Activity main,
                                                     String title, String message,
                                                     String TextLeftButton, String TextRightButton){
        AlertDialog.Builder ad = new AlertDialog.Builder(main);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setNegativeButton(TextLeftButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
                ADVisible=false;
            }
        });
        ad.setPositiveButton(TextRightButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                SharedPreferences.Editor editor = GameActivity.mSettings.edit();
                editor.putBoolean(GameActivity.APP_PREFERENCES_TUTOR, false);
                editor.apply();
                Tutorial tutor = new Tutorial(main);
                dialog.cancel();
                ADVisible=false;
            }
        });
        ad.setCancelable(false);
        if(!( main).isFinishing())
        {
            ad.show();
            ADVisible=true;
        }
        return ad;
    }


    static boolean isADVisible() {
        return ADVisible;
    }
    static boolean isADPositBut() {
        return ADPositBut;
    }
    static void setADPositBut(boolean ADPositBut) {
        Utils.ADPositBut = ADPositBut;
    }
}