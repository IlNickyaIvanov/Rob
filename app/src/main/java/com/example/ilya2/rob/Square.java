package com.example.ilya2.rob;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

 class Square {
    ImageView image;
    float x,y;
    int sqX,sqY;
    static int size=50;
    static int startX,startY;
    int ID_NUMBER;// 1-роботы у hunter'а, 2-стены, 3-вопросы
     Activity activity;
     Square(GameActivity main, float x, float y,int id) {
        ID_NUMBER=id;
        image = new ImageView(main);
        image.setX(x); // координаты
        image.setY(y);
        image.setImageResource(R.drawable.sqdark);
        activity = main;
        if(id==2)
            image.setAlpha(0f);
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        this.x = x;
        this.y = y;
    }
    //перегруженный метод специально для Настроек
     Square(SettingsActivity main,float x,final int sqX,float y,final int sqY,int id) {
         ID_NUMBER=id;
         image = new ImageView(main);
         this.x = x;this.sqX = sqX;
         this.y = y;this.sqY = sqY;
         image.setX(x); // координаты
         image.setY(y);
         image.setImageResource(R.drawable.sqdark);
         if(id==2)
             image.setAlpha(0f);
         activity = main;
         main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
         image.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 switch (SettingsActivity.editType) {
                     case(3):
                         if(image.getAlpha()==0f && SettingsActivity.wallNum==1
                                 || SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER==3 )
                             break;
                         if(SettingsActivity.stuffNum < SettingsActivity.mapSize-1){
                             SettingsActivity.stuff.add(new Stuff(activity,SettingsActivity.map,sqX,sqY));
                             SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER=3;
                             setNewStuffNum(SettingsActivity.stuffNum+1);
                         }
                     case(0):
                         if(SettingsActivity.wallNum==1 && image.getAlpha()==0f)
                             break;
                         if(SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER==3 &&
                                 SettingsActivity.stuffNum==1)
                             break;
                         if(image.getAlpha()==0f)
                             setNewWallNum(SettingsActivity.wallNum-1);
                         image.setAlpha(1f);
                         if(SettingsActivity.editType!=3) {
                             delStuff();
                             SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER=0;
                         }
                         break;
                     case(2):
                         if(SettingsActivity.wallNum<Math.round(SettingsActivity.mapSize*SettingsActivity.mapSize*0.16)
                                 && image.getAlpha()!=0f) {
                             if(SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER==3 &&
                                     SettingsActivity.stuffNum==1)
                                 break;
                             image.setAlpha(0f);
                             SettingsActivity.map.get(sqY).get(sqX).ID_NUMBER=2;
                             setNewWallNum(SettingsActivity.wallNum+1);
                             delStuff();
                         }
                         break;
                 }
             }
         });
     }

     void setNewStuffNum(int n){
         SettingsActivity.stuffNum=n;
         SettingsActivity.textStuff.setText("Кол-во вопросов: "+ SettingsActivity.stuffNum);
         SettingsActivity.seekBar3.setProgress(SettingsActivity.stuffNum*100
                 /(SettingsActivity.mapSize-1));
     }

     void setNewWallNum(int n){
         SettingsActivity.wallNum=n;
         SettingsActivity.textWall.setText("Кол-во стен: "+SettingsActivity.wallNum);
         SettingsActivity.seekBar2.setProgress(SettingsActivity.wallNum*100
                 /(int)(SettingsActivity.mapSize*SettingsActivity.mapSize*0.16));
     }

     void delStuff(){
         for (int i=0;i<SettingsActivity.stuff.size();i++){
             if(SettingsActivity.stuff.get(i).sqX==sqX &&
                     SettingsActivity.stuff.get(i).sqY==sqY){
                 SettingsActivity.stuff.get(i).delete();
                 SettingsActivity.stuff.remove(i);
                 setNewStuffNum(SettingsActivity.stuffNum-1);
                 break;
             }
         }
     }

     void delete(){
            FrameLayout parent = (FrameLayout) image.getParent();
            parent.removeView(image);
     }
}
