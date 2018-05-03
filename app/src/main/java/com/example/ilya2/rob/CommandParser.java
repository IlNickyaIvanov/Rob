package com.example.ilya2.rob;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CommandParser {
    private int []direction;
    private Square squares[][];
    private int x,y;
    private int turn,turns[][]={{-1,0},{0,-1},{1,0},{0,1}};//налево 0,вверх 1,направо 2,вниз 3
    private Queue<int[]> moveXY;
    String error=null;
    CommandParser(Square squares[][],int x,int y,int turn){
        this.turn = turn;
        direction=turns[turn];
        this.squares = squares;
        this.x = x;this.y = y;
        moveXY = new LinkedList<>();
    }

    Queue<int[]> parser(ArrayList<Block> blocks){
        for (Block block : blocks) {
            switch (block.type) {
                case (0):
                    if (direction[0] == 1 && x < squares[0].length-1
                            || direction[0] == -1 && x > 0 || direction[0] == 0)
                        x += direction[0];
                    else {
                        if (direction[0] == 1) error = "Справа клеток нет!";
                        else error = "Слева клеток нет!";
                    }
                    if (direction[1] == 1 && y < squares.length-1
                            || direction[1] == -1 && y > 0 || direction[1] == 0)
                        y += direction[1];
                    else {
                        if (direction[1] == 1) error = "Внизу клеток нет!";
                        else error = "Сверху клеток нет!";
                    }
                    int xy[] = {x, y};
                    moveXY.add(xy);
                    break;
                case (1):
                    if (turn == turns.length - 1)
                        turn = 0;
                    else turn += 1;
                    direction = turns[turn];
                    int[] rxy = {90};
                    moveXY.add(rxy);
                    break;
                case (2):
                    if (turn == 0)
                        turn = turns.length - 1;
                    else turn -= 1;
                    direction = turns[turn];
                    int[] lxy = {-90};
                    moveXY.add(lxy);
                    break;
            }
            if(error!=null)
                break;
        }
        return moveXY;
    }

}
