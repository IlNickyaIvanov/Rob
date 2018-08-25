package com.example.ilya2.rob;

import android.view.MotionEvent;

public class BlockControler extends GameActivity implements Runnable{

    MotionEvent event;
    BlockControler(MotionEvent event){
        this.event = event;
    }

    @Override
    public void run() {
        if (touchedBlock != -1) {
            Block to = null, that = blocks.get(touchedBlock);//to - к чему присоединяет, that - что присоединяет
            if(that.discon==null)that.setDiscon();
            if (that.checkTopConnection(event.getX(), event.getY())) {//проверка на присоединение сверху устанавливает координаты косания
                blocks.remove(that);
                blocks.add(0, that);
                refreshBlocks();
                log.info("log: connected!\n");
            } else {//установка координат косания и проверка на присоединение снизу
                to = that.setXY();
            }
            //добавление обьекта в список в нужное место
            if (to != null) {
                log.info("log: connected!\n");
                blocks.remove(that);
                blocks.add(blocks.indexOf(to) + 1, that);
                refreshBlocks();
            }
        }
    }

}
