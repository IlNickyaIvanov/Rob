package com.example.ilya2.rob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Date;
import java.util.logging.Logger;

public class Block {
    ImageView image;
    static int size=Command.size*95/100, alpha; // размер картинки, врещение, прозрачность
    boolean connected=false;

    int num=-1;//порядковый номер в листе blocks
    float loopNumO =-1, loopNumI =-1;//пордковый номер [цикл.номер в цикле] внешнего цикла/внутреннего
    float x,y;
    int type;
    boolean newCom=true;
    private final MediaPlayer bubble,stone;
    Logger log = Logger.getLogger(Block.class.getName());
    int logy=0;
    Date discon;
    @SuppressLint("ClickableViewAccessibility")
    Block(Activity main, float x, float y, int type, final int number) {
        this.num = number;
        this.type = type;
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
        image.setOnTouchListener(new View.OnTouchListener()
        {@Override
        public boolean onTouch(View v, MotionEvent event)
        {
            logy=0;
            log.info("log: Block touched\n");
            setDiscon();
            GameActivity.longClick = new Date();
            GameActivity.still = true;
            if(loopNumO !=-1)
                GameActivity.loopNum=(int) loopNumO;
            GameActivity.touchedBlock=(loopNumO ==-1)?num: loopNumO;
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
            if(block.num == num)
                continue;
            if(block.type==3) {
                Block to=checkRightConnection((int)block.loopNumO);
                if(to!=null){
                    if(type == 3 && this.loopNumI==-1)
                        this.loopNumI = this.loopNumO;
                    this.loopNumO = to.loopNumO;//для того чтобы понимать справва /снизу
                    return to;
                }
            }
            if(getRoundX()==block.getRoundX() &&  getRoundY()==block.getRoundY(size)) {
                connected = true;
                discon=null;
                stone.start();
                GameActivity.touchedBlock=-1;
                loopNumO = -1;
                loopNumI = -1;
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
                if(to!=null){
                    if(type == 3 && this.loopNumI==-1)
                        this.loopNumI =  this.loopNumO;
                    this.loopNumO = to.loopNumO;
                    return to;
                }
            }
            if (getRoundX() == block.getRoundX(size) && getRoundY() == block.getRoundY()) {
                connected = true;
                discon = null;
                stone.start();
                GameActivity.touchedBlock = -1;
                return block;
            }
        }
        return null;
    }

    boolean checkTopConnection(float x,float y){
        this.x=x-size/2;
        this.y=y-size/2;
        connected = (GameActivity.blocks.size() ==1 && (GameActivity.blocks.get((0)).type!=3
                        || (GameActivity.blocks.get(0).type==3 && GameActivity.loops.get(0).size()==1 )));
        boolean isTop=false;
        if(GameActivity.blocks.get(0).y == y)isTop=true;
        if((new Date()).getTime()-discon.getTime()<=501) {
            //log.info("log: выброс");
            setBlockXY(this.x,this.y);
            return false;
        }
        if(getRoundY(size)== GameActivity.blocks.get(isTop?1:0).getRoundY() && getRoundX()== GameActivity.blocks.get(isTop?1:0).getRoundX()){
            this.x = GameActivity.blocks.get(0).x;
            this.y = GameActivity.blocks.get(0).y- size;
            setBlockXY(this.x,this.y);
            connected = true;
            discon=null;
            stone.start();
            GameActivity.touchedBlock=-1;
            loopNumO = -1;
            loopNumI = -1;
            return true;
        }
        return false;
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
        if(logy==0)log.info("log: XY\n");
        logy=1;
        this.x = x;
        this.y = y;
        image.setX(x);
        image.setY(y);
    }
    void setNum(int number){
        num = number;
        loopNumO =-1;
        loopNumI =-1;
    }
    void setNum(float number){
        loopNumO = number;
        if(GameActivity.afterPoint(loopNumO)!=1)
            num = -1;
    }
}