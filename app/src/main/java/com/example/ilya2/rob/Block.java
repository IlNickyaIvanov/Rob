package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.logging.Logger;

import static com.example.ilya2.rob.GameActivity.afterPoint;
import static com.example.ilya2.rob.GameActivity.refreshBlocks;
import static com.example.ilya2.rob.GameActivity.touchedBlock;

public class Block {

    final static String colors[]={
            "#70ff4fff",//феолетовый
            "#705B42FF",//синий
            "#7042FFFD",//голубой
            "#704CFF42",//зеленый
            "#70FFE433",//желтый
            "#70FF9533",//оранжевый
            "#70FF3933",//красный
    };

    ImageView image;
    static int size=Command.size*95/100, alpha; // размер картинки, врещение, прозрачность
    boolean connected=false;

    int num=-1;//порядковый номер в листе blocks
    float loopNumO =-1;//номер во внешнем
    float loopNumI =-1;//номер своего цикла
    //пордковый номер [цикл.номер в цикле] внешнего цикла/внутреннего
    float loopSize=-1;//размер
    TextView loopBack;// задний фон цикла
    float x,y;
    int type;
    boolean newCom=true;
    private final MediaPlayer bubble,stone;
    Logger log = Logger.getLogger(Block.class.getName());
    int logy=0;
    Date discon;
    @SuppressLint("ClickableViewAccessibility")

    Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    Block(Activity main, float x, float y, int type, final int number) {
        this.activity = main;
        this.type = type;
        this.num = number;
        this.x=x;
        this.y=y;
        image = new ImageView(main);
        this.x=x;
        this.y=y;
        setBlockXY(x,y);
        switch (type){
            case (0):
                image.setImageResource(R.drawable.forward);
                break;
            case (1):
                image.setImageResource(R.drawable.right);
                break;
            case (2):
                image.setImageResource(R.drawable.left);
                break;
            case (3):
                image.setImageResource(R.drawable.x2);
                break;
        }
        main.addContentView(image, new RelativeLayout.LayoutParams(size, size));
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                logy=0;
                log.info("log: Block touched\n");
                setDiscon();
                GameActivity.longClick = new Date();
                GameActivity.still = true;
                if(num!=-1 && touchedBlock==null) {
                    GameActivity.touchedBlock = GameActivity.blocks.get(num);
                    GameActivity.blocks.remove(num);
                }
                if(loopNumO!=-1 && touchedBlock==null){
                    GameActivity.touchedBlock = GameActivity.loops.get((int)loopNumO).get(afterPoint(loopNumO)-1);
                    GameActivity.loops.get((int)loopNumO).remove(afterPoint(loopNumO)-1);
                }
                refreshBlocks();
                return false;
            }
        });
        bubble = MediaPlayer.create(main, R.raw.bubblepop);
        stone = MediaPlayer.create(main, R.raw.stonedry);
    }
    //установка координат с проверкой на присоединение
    Block setXY(){
        //setBlockXY(this.x,this.y);
        if((new Date()).getTime()-discon.getTime()<=501) {
            //log.info("log: выброс");
            setBlockXY(this.x,this.y);
            return null;
        }
        for (Block block: GameActivity.blocks){
            if(block.type==3) {
                Block to=checkRightConnection((int)block.loopNumI);
                if(to!=null) return to;
            }
            //присоединение во внешней последовательности
            if(getRoundX()==block.getRoundX() &&  getRoundY()==block.getRoundY(size)) {
                connected = true;
                discon=null;
                stone.start();
                loopNumO = -1;
                num = block.num+1;
                return block;
            }
        }
        return null;
    }

    Block checkRightConnection(int loopNum) {
         for (Block block: GameActivity.loops.get(loopNum)) {
            if (block.image == image)
                continue;
            if(block.type==3 && GameActivity.loops.get(loopNum).get(0)!=block) {//вторая проверка чтобы не входить в тот же цикл
                Block to=checkRightConnection((int)block.loopNumI);
                if(to!=null) return to;
            }
            if (getRoundX() == block.getRoundX(size) && getRoundY() == block.getRoundY()) {
                connected = true;
                discon = null;
                loopNumO = (block.type!=3)?block.loopNumO:block.loopNumI + 0.1f;
                num = -1;
                stone.start();
                return block;
            }
        }
        return null;
    }

    Block checkTopConnection(){
        connected = (GameActivity.blocks.size() ==1 && (GameActivity.blocks.get((0)).type!=3
                        || (GameActivity.blocks.get(0).type==3 && GameActivity.loops.get(0).size()==1 )));
        if((new Date()).getTime()-discon.getTime()<=501) {
            //log.info("log: выброс");
            return null;
        }
        if(getRoundY(size)== GameActivity.blocks.get(0).getRoundY() && getRoundX()== GameActivity.blocks.get(0).getRoundX()){
            connected = true;
            discon=null;
            stone.start();
            num = 0;
            return GameActivity.blocks.get(0);
        }
        return null;
    }

    //установка к предыдущему блоку
    void setXY(Block lastBlock){
        this.x = lastBlock.x;
        this.y = lastBlock.y+size;
        setBlockXY(this.x,this.y);
    }
    void setYX(Block lastBlock,int delta){
        if(lastBlock.type!=3)
            delta=0;
        this.x = lastBlock.x+size+delta;
        this.y = lastBlock.y;
        setBlockXY(this.x,this.y);
    }
    private int getRoundX(){
        return Math.round(x-x%100);
    }
    private int getRoundX(int size){
        return Math.round((x+size)-(x+size)%100);
    }
    private int getRoundY(){
        return Math.round(y-y%100);
    }
    private int getRoundY(int size){
        return Math.round((y+size)-(y+size)%100);
    }
    void delete(){
        FrameLayout parent = (FrameLayout) image.getParent();
        if(parent!=null){
            parent.removeView(image);
            if(newCom)
                GameActivity.newCom--;
        }
        parent=null;
        if(loopBack!=null)
            parent = (FrameLayout) loopBack.getParent();
        if(parent!=null)
            parent.removeView(loopBack);
        bubble.start();
    }
    void setOld(boolean o){
        if(o)
            image.setAlpha(0.6f);
        else
            image.setAlpha(1f);
        newCom = !o;
    }
    void setDiscon(){
        discon = new Date();
    }
    //чистая установка координат
    void setBlockXY(float x, float y){
        log.info("log: XY\n");
        logy=1;
        this.x = x;
        this.y = y;
        image.setX(x);
        image.setY(y);
    }
    void setNum(int number){
        num = number;
        loopNumO =-1;
    }
    void setNum(float number){
        loopNumO = number;
        if(afterPoint(loopNumO)!=1)
            num = -1;
    }

    void addToBlocks(){
        log.info("log: connected!\n");
        //присоединение во внешней последовательности
        if(num!=-1)
            GameActivity.blocks.add(num, GameActivity.touchedBlock);
        if(num==0 && GameActivity.blocks.size()>1)
            setBlockXY(GameActivity.blocks.get(1).x,GameActivity.blocks.get(1).y-size);
        //в цикле
        if(loopNumO!=-1)
            GameActivity.loops.get((int)loopNumO).add(afterPoint(loopNumO)-1,GameActivity.touchedBlock);
        refreshBlocks();
    }

    boolean setLoopBackground(){
        if(image.getParent()==null)
            return false;
        FrameLayout parent=null;
        if(loopBack!=null)
            parent = (FrameLayout) loopBack.getParent();
        if(parent!=null)
            parent.removeView(loopBack);
        loopBack = new TextView(activity);
        float width,height=size+10;
        if(loopNumI!=-1){
            width = loopSize;
            loopBack.setBackgroundColor(Color.parseColor(colors[(int)(loopNumI)%7]));
        }else{
            width = loopSize;
            loopBack.setBackgroundColor(Color.parseColor(colors[(int)(loopNumO)%7]));
        }
        loopBack.setWidth((int)width);
        loopBack.setHeight((int)height);
        loopBack.setX(x-5);
        loopBack.setY(y-5);
        activity.addContentView(loopBack,new RelativeLayout.LayoutParams((int)(width),(int)(height)));
        bringToTheFront();
        return true;
    }
    void bringToTheFront(){
        image.bringToFront();
    }
}