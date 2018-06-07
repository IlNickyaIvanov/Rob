package com.example.ilya2.rob;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.widget.Toast;
//полезные и универсальные методы
class Utils {
    private static boolean ADPositBut;
    private static boolean ADVisible;
    static void makeToast(Activity main, String text){
        Toast.makeText(main,text,Toast.LENGTH_SHORT).show();
    }
    public static void AlertDialog(final Activity main, String title, String Message, String TextButton ){
        MainActivity.AlertDialogMessage=null;

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
                                MainActivity.startGame(main);
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