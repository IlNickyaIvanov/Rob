package com.example.ilya2.rob;

import android.app.Activity;
import java.util.ArrayList;


class KodParser {
    private static int symbolslENGTH;
    int action;
    int x, y;//положение робота
    Activity activity;
    int start, stop;
    int ARx[];//массивы пошаговых
    int ARy[];//координат положения робота
    int Anim[];//ключ анимации для робота
    private Square square[][];
    private int AnimID;
    private boolean pause;//это означает, что боб наткнулся на лаву или кислоту или конец поля
    private boolean kodERROR;//пауза для ошибок
    private boolean loop = false;
    private String text;
    //-----------------------------------------------------------------------------------------------------------------------------------
    //IF-ELSE
    private ArrayList<Integer> elsenum = new ArrayList<>();//номера елементов основного массива, содержащих IF и/или ELSE
    private String IEMainLine[];

    KodParser(int StartX, int StartY, Square square[][], int ComandsLimit, Activity activity) {
        this.activity = activity;
        this.square = square;
        ARx = new int[ComandsLimit];
        ARy = new int[ComandsLimit];
        Anim = new int[ComandsLimit];
        x = StartX;
        y = StartY;
    }

    int kodParser(String text) {
        pause = false;
        kodERROR = false;
        start = 0;
        stop = 0;
        String[] MainLine;
        text = text.replaceAll(" ", "");
        text = text.replaceAll("\n", "");
        this.text = text;
        if (text.replaceAll("\n", "").replaceAll(";", "").isEmpty()) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.wh_com);
            pause = true;
            kodERROR = true;
        } else try {
            String[] line;
            try {
                line = text.replaceAll("\n", "").split(";");
                MainLine = text.split(";");
            } catch (Throwable t) {//выделение подстрок по символу ";"
                MainActivity.AlertDialogMessage = activity.getString(R.string.ch_del);
                kodERROR = true;
                return action;
            }
            for (int i = 0; i < line.length; i++) {
                if (kodERROR || pause) break;
                line[i] = line[i].trim();
                if (CHECK_DELIMITOR(line[i], MainLine[i], text)) break;
                int num = 1;
                String cOmAnD = line[i];
                if (!cOmAnD.contains("if") && !cOmAnD.contains("while") && !cOmAnD.contains("eat") || cOmAnD.contains("repeat"))
                    try {
                        if (IS_NUM_EXIST(MainLine[i], line[i]) == 0)
                            break;//при несуществуюшей цифре - 0
                        else num = IS_NUM_EXIST(MainLine[i], line[i]);
                        cOmAnD = cOmAnD.substring(0, line[i].indexOf("(")).trim();
                    } catch (Throwable t) {
                        num = 1;
                    }
                else if (!cOmAnD.contains("eat")) {
                    try {
                        cOmAnD = cOmAnD.substring(0, line[i].indexOf("(")).trim();
                    } catch (Throwable t) {
                        MainActivity.AlertDialogMessage = activity.getString(R.string.bad_cond);
                        SELECTOR(MainLine[i], text, symbolslENGTH);
                        kodERROR = true;
                    }
                }
                //проверка на существование КОМАНДЫ ОБНОВЛЯТЬ ПО ДОБАВЛЕНИЮ НОВЫХ!
                if (IS_COMAND_EXIST(cOmAnD, MainLine[i], text)) break;
                if (!pause) i += executor(cOmAnD, num, i, MainLine, text);//исполнитель
                if (!loop) symbolslENGTH += MainLine[i].length() + 1;
                else loop = false;
            }
        } catch (Throwable t) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.ch_com);
            kodERROR = true;
        }
        symbolslENGTH = 0;
        return action;
    }

    //Если команда прошла все проверки, то она попадает в исполнитель.
    //Здесь она определяется и по ней уже задаются координаты в массивы.
    private int executor(String cOmAnD, int num, int Element, String MainLine[], String text) {
        int LOOP_JUMP = 0;
        loop = false;
        for (int g = 0; g < num; g++) {
            switch (cOmAnD) {
                case "eat":
                    if (IS_FOOD(0, 0))
                        Anim[action] = 5;
                    else if (!pause) {
                        pause = true;
                        MainActivity.AlertDialogMessage = activity.getString(R.string.no_food);
                    }
                    break;
                case "up":
                    if (y != 0 && !IS_LAVA(-1, 0, false)) y--;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(-1, 0, false))
                            MainActivity.AlertDialogMessage = activity.getString(R.string.up) + MainActivity.AlertDialogMessage;
                    }
                    break;
                case "down":
                    if (y != square.length - 1 && !IS_LAVA(1, 0, false)) y++;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(1, 0, false))
                            MainActivity.AlertDialogMessage = "" + activity.getString(R.string.down) + MainActivity.AlertDialogMessage;
                    }
                    break;
                case "right":
                    if (x != square[0].length - 1 && !IS_LAVA(0, 1, false)) x++;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(0, 1, false))
                            MainActivity.AlertDialogMessage = "" + activity.getString(R.string.right) + MainActivity.AlertDialogMessage;
                    }
                    break;
                case "left":
                    if (x != 0 && !IS_LAVA(0, -1, false)) x--;
                    else if (!pause) {
                        pause = true;
                        if (IS_LAVA(0, -1, false))
                            MainActivity.AlertDialogMessage = "" + activity.getString(R.string.left) + MainActivity.AlertDialogMessage;

                    }
                    break;
                case "repeat":
                    LOOP_JUMP = idetifyBODY(MainLine, Element, text);
                    if (!kodERROR) {
                        if (g == 0) symbolslENGTH +=
                                MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                        LOOP(Element, MainLine, text);
                    }
                    loop = true;
                    break;
                case "if":
                    LOOP_JUMP = IEidetifyBODY(MainLine, Element, text);
                    if (!kodERROR) {
                        if (g == 0) symbolslENGTH +=
                                MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                        DISTRIBUTOR();
                    }
                    loop = true;
                    break;
                case "while":
                    LOOP_JUMP = IEidetifyBODY(MainLine, Element, text);
                    String condition =
                            MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).
                                    substring(MainLine[Element].indexOf("(") + 1,
                                            MainLine[Element].indexOf(")"));
                    while (CONDITION_CHECKING(condition)) {
                        if (!kodERROR) {
                            if (g == 0) symbolslENGTH +=
                                    MainLine[Element].substring(0, MainLine[Element].indexOf("{") + 1).length();
                            if (!pause)
                                LOOP(Element, MainLine, text);
                            else break;
                        } else
                            break;
                    }
                    loop = true;
                    break;
                default:
                    break;
            }
            if (loop) continue;
            if (pause) {
                SELECTOR(MainLine[Element], text, symbolslENGTH);
                break;
            }
            //по умолчанию, в игре максимум - 100 положений робота. Но при желании, можно, конечно, и увеличить.
            if (action == ARx.length - 1) {
                MainActivity.AlertDialogMessage = (activity.getString(R.string.com_lim));
                kodERROR = true;
            }
            ARx[action] = x;
            ARy[action] = y;
            AnimID = 0;
            action++;
        }
        return LOOP_JUMP;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    //проверки
    private boolean CONDITION_CHECKING(String text) {
        boolean result = false;
        text = text.trim();
        int dX = 0, dY = 0;//направление проверки
        boolean isNOT = false;
        String dir_text;
        try {
            do {
                dir_text = text.substring(0, text.indexOf("_"));
                switch (dir_text) {
                    case ("not"):
                        text = text.substring(text.indexOf("_") + 1);
                        isNOT = !isNOT;
                        break;
                    case ("up"):
                        dY = -1;
                        dX = 0;
                        AnimID = 1;
                        break;
                    case ("down"):
                        dY = 1;
                        dX = 0;
                        AnimID = 2;
                        break;
                    case ("left"):
                        dY = 0;
                        dX = -1;
                        AnimID = 3;
                        break;
                    case ("right"):
                        dY = 0;
                        dX = 1;
                        AnimID = 4;
                        break;
                }
            }
            while (dir_text.equals("not"));
            //Еще одна проверка на INFINITY. Практика показала, что и здесь вылетают ошибки
            if (action == ARx.length - 1) {
                MainActivity.AlertDialogMessage = (activity.getString(R.string.com_lim));
                kodERROR = true;
                return false;
            }
            ARx[action] = x;
            ARy[action] = y;
            Anim[action] = AnimID;
            action++;
            if (text.substring(text.indexOf("_") + 1).equals("wall"))
                result = IS_LAVA(dY, dX, true);
            else if (text.substring(text.indexOf("_") + 1).equals("sweet"))
                result = IS_FOOD(dY, dX);
            if (isNOT) result = !result;
        } catch (Throwable t) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.bad_cond) + text;
            kodERROR = true;
        }
        return result;
    }

    //простая проверка на еду
    private boolean IS_FOOD(int dY, int dX) {
        boolean is_FOOD = false;
        switch (dX) {
            case (-1):
                dX = this.x - 1;
                break;
            case (1):
                dX = this.x + 1;
                break;
            default:
                dX = this.x;
                break;
        }
        switch (dY) {
            case (-1):
                dY = this.y - 1;
                break;
            case (1):
                dY = this.y + 1;
                break;
            default:
                dY = this.y;
                break;
        }
        if (dY < 0 || dY > square.length - 1) {
            is_FOOD = false;
        } else if (dX < 0 || dX > square[dY].length - 1) {
            is_FOOD = false;
        } else if (square[dY][dX].ID_NUMBER == 3) {
            is_FOOD = true;
        }
        return (is_FOOD);
    }

    //проверка на ';'
    private boolean CHECK_DELIMITOR(String line, String OriginLine, String text) {
        if (line.equals("")) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.exc);
            SELECTOR(OriginLine, text, symbolslENGTH);
            kodERROR = true;
            return true;
        } else if (!line.contains("repeat") && !line.contains("if") && !line.contains("while")
                && line.contains("(") && line.contains(")") && !line.endsWith(")")) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.after) + line.substring(0, line.indexOf(")") + 1) + activity.getString(R.string.wait);
            SELECTOR(OriginLine, text, symbolslENGTH);
            stop = symbolslENGTH + OriginLine.indexOf(")") + 1;
            kodERROR = true;
            return true;
        } else return false;
    }

    private boolean IS_COMAND_EXIST(String cOmAnD, String OriginLine, String text) {
        if (!cOmAnD.equals("up") && !cOmAnD.equals("down")
                && !cOmAnD.equals("left") && !cOmAnD.equals("right")
                && !cOmAnD.equals("repeat") && !cOmAnD.equals("if")
                && !cOmAnD.equals("while") && !cOmAnD.equals("eat")) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.unknown) + cOmAnD;
            SELECTOR(OriginLine, text, symbolslENGTH);
            kodERROR = true;
            return true;
        } else return false;
    }

    private int IS_NUM_EXIST(String OriginLine, String line) {
        int num;
        try {
            num = Integer.valueOf(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
            return num;
        } catch (NumberFormatException e) {
            MainActivity.AlertDialogMessage = activity.getString(R.string.unkn_num);
            if (OriginLine.contains("("))
                start = symbolslENGTH + OriginLine.indexOf("(");
            else start = symbolslENGTH + line.length() - 1;
            if (OriginLine.contains(")"))
                stop = symbolslENGTH + OriginLine.indexOf(")") + 1;
            else stop = symbolslENGTH + OriginLine.length() + 1;
            kodERROR = true;
            return 0;
        }
    }

    private boolean IS_LAVA(int dY, int dX, boolean justCheck) {
        boolean is_LAVA = false;
        switch (dX) {
            case (-1):
                dX = this.x - 1;
                break;
            case (1):
                dX = this.x + 1;
                break;
            default:
                dX = this.x;
                break;
        }
        switch (dY) {
            case (-1):
                dY = this.y - 1;
                break;
            case (1):
                dY = this.y + 1;
                break;
            default:
                dY = this.y;
                break;
        }
        if (dY < 0 || dY > square.length - 1) {
            is_LAVA = true;
            if (!justCheck)
                MainActivity.AlertDialogMessage = activity.getString(R.string.no_sq);
        } else if (dX < 0 || dX > square[dY].length - 1) {
            is_LAVA = true;
            if (!justCheck)
                MainActivity.AlertDialogMessage = activity.getString(R.string.no_sq);
        } else if (square[dY][dX].ID_NUMBER == 1) {
            is_LAVA = true;
            if (!justCheck)
                MainActivity.AlertDialogMessage = activity.getString(R.string.lava);
        } else if (square[dY][dX].ID_NUMBER == 2) {
            is_LAVA = true;
            if (!justCheck)
                MainActivity.AlertDialogMessage = activity.getString(R.string.kisl);
        } else if (!square[dY][dX].isVISIBLE) {
            is_LAVA = true;
            if (!justCheck)
                MainActivity.AlertDialogMessage = activity.getString(R.string.sq_dis);
        }
        return (is_LAVA);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    //ЦИКЛ (наверное, самое интересное)
    //этот метод главный, он вызывает остальные и по окончанию опять вызывает парсер кода, но уже для команд цикла
    private void LOOP(int LoopElement, String MainLine[], String text) {
        int index = MainLine[LoopElement].replaceAll(" ", "").indexOf(")");
        String test = MainLine[LoopElement].replaceAll(" ", "").substring(index, index + 2);
        if (!test.equals("){")) {
            MainActivity.AlertDialogMessage = (activity.getString(R.string.misd_op));
            SELECTOR(MainLine[LoopElement], text, symbolslENGTH);
            kodERROR = true;
        }

        String LoopComands = "";
        int length = idetifyBODY(MainLine, LoopElement, text);
        LoopComands += MainLine[LoopElement].substring(MainLine[LoopElement].indexOf("{") + 1, MainLine[LoopElement].length()) + ";";
        for (int g = 1; g < length; g++) {
            LoopComands += MainLine[LoopElement + g] + ";";
        }
        if (!kodERROR) kodParser(LoopComands);
    }

    //а вот метод ниже - МОЯ ГОРДОСТЬ. Он позволяет выделить тело цикла...даже использует самовызов XD
    private int idetifyBODY(String MainLine[], int LoopElement, String text) {
        boolean enLOOP_EXIST = false;
        String line[] = new String[MainLine.length - LoopElement];
        line[0] = MainLine[LoopElement].substring(MainLine[LoopElement].indexOf("{") + 1, MainLine[LoopElement].length());
        System.arraycopy(MainLine, LoopElement + 1, line, 1, MainLine.length - LoopElement - 1);
        for (int i = 0; i < line.length; i++) {
            if (line[i].contains("}") && !enLOOP_EXIST) return i;
            if (line[i].contains("{")) {
                if (!enLOOP_EXIST) enLOOP_EXIST = true;
                else if (idetifyBODY(MainLine, i + LoopElement, text) != -1) {
                    i += idetifyBODY(MainLine, i + LoopElement, text);
                } else break;
            } else if (line[i].contains("}")) enLOOP_EXIST = false;
        }
        MainActivity.AlertDialogMessage = (activity.getString(R.string.misd_cl));
        SELECTOR(MainLine[LoopElement], text, symbolslENGTH);
        kodERROR = true;

        return -1;
    }

    //определяет "прыжок" для основного парсера команд и создает метки елементов с IF/ELSE
    private int IEidetifyBODY(String MainLine[], int LoopElement, String text) {
        //---------------------------
        elsenum.clear();
        int elnum = 0;
        IEMainLine = MainLine;
        //обнуление
        elsenum.add(LoopElement);
        elnum++;
        elsenum.add(idetifyBODY(MainLine, elsenum.get(elnum - 1), text) + elsenum.get(elnum - 1));
        while (!MainLine[elsenum.get(elnum)].replaceAll("\n", "").equals("}")) {
            if (IS_ELSE_OKAY(MainLine[elsenum.get(elnum)])) {
                elnum++;
                elsenum.add(idetifyBODY(MainLine, elsenum.get(elnum - 1), text) + elsenum.get(elnum - 1));
            } else {
                MainActivity.AlertDialogMessage = activity.getString(R.string.wait_else) + MainLine[elsenum.get(elnum)];
                SELECTOR(MainLine[LoopElement], this.text, symbolslENGTH);
                kodERROR = true;
                return -1;
            }
        }
        return elsenum.get(elnum) - LoopElement;
    }

    //главный метод у if-els'ов. Он, как и в цикле, распределяет и по окончанию запускает парсер.
    private void DISTRIBUTOR() {
        for (int i = 0; i < elsenum.size(); i++) {
            String line = IEMainLine[elsenum.get(i)];
            if (IS_IF_OKAY(line)) {
                try {
                    String text = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    if (CONDITION_CHECKING(text)) {
                        String IF_ELSE_COMANDS = line.substring(line.indexOf("{") + 1) + ";";
                        for (int g = elsenum.get(i) + 1; g < elsenum.get(i + 1); g++)
                            IF_ELSE_COMANDS += IEMainLine[g] + ";";
                        kodParser(IF_ELSE_COMANDS);
                        break;
                    }
                } catch (Throwable t) {
                    MainActivity.AlertDialogMessage = activity.getString(R.string.bad_cond);
                    break;
                }
            } else if (i != elsenum.size() - 1) {
                String IF_ELSE_COMANDS = line.substring(line.indexOf("{") + 1) + ";";
                for (int g = elsenum.get(i) + 1; g < elsenum.get(i + 1); g++)
                    IF_ELSE_COMANDS += IEMainLine[g] + ";";
                kodParser(IF_ELSE_COMANDS);
                break;
            }
        }
    }

    //проверка 'нормальности' else
    private boolean IS_ELSE_OKAY(String line) {
        boolean result = false;
        if (line.contains("if")) {
            if (line.substring(line.indexOf("}") + 1, line.indexOf("i")).trim().equals("else"))
                result = true;
            else {
                MainActivity.AlertDialogMessage = activity.getString(R.string.bad_cond) + line;
                System.out.println("неизвестная команда");
            }
        } else {
            if (line.substring(line.indexOf("}") + 1, line.indexOf("{")).trim().equals("else"))
                result = true;
            else {
                MainActivity.AlertDialogMessage = activity.getString(R.string.bad_cond) + line;
                System.out.println("неизвестная команда");
            }
        }
        return result;
    }

    //нормальнось if
    private boolean IS_IF_OKAY(String line) {
        boolean result = false;
        if (line.contains("(")) {
            if (line.contains("else")) {
                if (line.substring(line.indexOf("else") + 4, line.indexOf("(")).trim().equals("if"))
                    result = true;
            } else if (line.substring(0, line.indexOf("(")).trim().equals("if"))
                result = true;
        }
        return result;
    }

    //-------------------------------------------------------------------------------------------------------
    //обнуление для очистки от старых значений
    void ZEROING() {
        for (int i = 0; i < ARx.length; i++) {
            ARx[i] = 0;
            ARy[i] = 0;
        }
        action = 0;
    }

    //выделитель ошибки
    private void SELECTOR(String OriginLine, String text, int symbolslENGTH) {
        if (OriginLine.equals("")) start = symbolslENGTH;
        else if (OriginLine.substring(0, 1).equals("\n"))
            start = symbolslENGTH + 1;
        else start = symbolslENGTH;
        if (symbolslENGTH + OriginLine.length() + 1 > text.length())
            stop = symbolslENGTH + OriginLine.length();
        else stop = symbolslENGTH + OriginLine.length() + 1;
    }

    void setAction(int action) {
        this.action = action;
    }

    boolean isKodERROR() {
        return kodERROR;
    }

    void setKodERROR(boolean kodERROR) {
        this.kodERROR = kodERROR;
    }

    boolean isPause() {
        return pause;
    }
}


