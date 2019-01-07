import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Rotations{
    String[] moves2x2 = {"F", "F'", "F2", "U", "U'", "U2", "R", "R'", "R2"};

    String[] moves3x3 = {"F", "F'", "U", "U'", "R", "R'", "L", "L'", "B", "B'",
    "D", "D'", "F2", "U2", "R2", "L2", "B2", "D2"};

    //Moves for the 4x4 and 5x5
    String[] moves4x4 = {"F", "F'", "F2", "f", "f'", "f2", "B", "B'", "B2", "b", "b'", "b2",
    "R", "R'", "R2", "r", "r'", "r2", "L", "L'", "L2", "l", "l'", "l2",
    "U", "U'", "U2", "u", "u'", "u2", "D", "D'", "D2", "d", "d'", "d2"};


    //Moves for the 6x6 and 7x7
    String[] moves6x6 = {"F", "F'", "F2", "f", "f'", "f2", "FC", "FC'", "FC2", "B", "B'", "B2", "b", "b'", "b2", "BC", "BC'", "BC2",
    "R", "R'", "R2", "r", "r'", "r2", "RC", "RC'", "RC2", "L", "L'", "L2", "l", "l'", "l2", "LC", "LC'", "LC2",
    "U", "U'", "U2", "u", "u'", "u2", "UC", "UC'", "UC2", "D", "D'", "D2", "d", "d'", "d2", "DC", "DC'", "DC2"};

    public String getMoves(int type){
        String[] moves;
        String[] moveSelections = {"error", "fail"};

        if (type == 2){
            moves = new String[9];
            moveSelections = moves2x2;
        }
        else if (type == 3){
            moves = new String[25];
            moveSelections = moves3x3;
        }
        else if (type == 4){
            moves = new String[30];
            moveSelections = moves4x4;
        }
        else if (type == 5){
            moves = new String[35];
            moveSelections = moves4x4;
        }
        else if (type == 6){
            moves = new String[40];
            moveSelections = moves6x6;
        }
        else if (type == 7){
            moves = new String[45];
            moveSelections = moves6x6;
        }
        else{
            moves = new String[10];
            //moveSelections = {"Hello", "test"};
        }

        String finalMove = "";

        for (int i = 0; i < moves.length; i++){
            Random r = new Random();
            int pos = r.nextInt(moveSelections.length);
            char typeM = moveSelections[pos].charAt(0);
            boolean sameType = false;

            //Checks to make sure we dont have the move type twice (Ex: R, R')
            if (i > 0){
                if (typeM == (moves[i-1].charAt(0)))
                sameType = true;

                while (sameType){
                    pos = r.nextInt(moveSelections.length);
                    typeM = moveSelections[pos].charAt(0);

                    if (typeM == (moves[i-1].charAt(0)))
                    sameType = true;
                    else
                    sameType = false;
                }

                /*Checks that all moves are productive.
                (Ex: L, R2, L' -> not productive because L is set back to a non changed position)
                (Ex: L, U', L' -> productive because L is crossed from the U rotation)
                */
                if (i > 1){
                    //System.out.println("test");
                    boolean risk = false;
                    boolean nonProductive = false;

                    if (!fairMoves(moves[i-2], moves[i-1]))
                    risk = true;

                    if (risk)
                    if(!fairMoves(moves[i-1], moveSelections[pos])){
                        nonProductive = true;

                        while(nonProductive){
                            pos = r.nextInt(moveSelections.length);

                            if (!fairMoves(moves[i-1], moveSelections[pos]))
                            nonProductive = true;
                            else
                            nonProductive = false;
                        }
                    }
                }
            }

            moves[i] = moveSelections[pos];
        }


        for (int i = 0; i < moves.length; i++){
            // if (moves.length >= 10){
            //     if (i == moves.length / 2)
            //     finalMove += "\n";
            //     else
            //     finalMove += moves[i] + "  ";
            // }
            // else
            finalMove += moves[i] + "  ";

        }

        return finalMove;
    }

    public boolean fairMoves(String a1, String b1){
        //a1 = a1.toUpperCase();
        //b1 = b1.toUpperCase();
        char a = a1.charAt(0);
        char b = b1.charAt(0);


        if (a == 'L' || a == 'R')
        if (b == 'L' || b == 'R')
        return false;

        if (a == 'D' || a == 'U')
        if (b == 'D' || b == 'U')
        return false;

        if (a == 'F' || a == 'B')
        if (b == 'F' || b == 'B')
        return false;

        return true;
    }


    public static void main(String[] args){
        Rotations r = new Rotations();

        //String m = r.get3x3Moves();
        // System.out.println(m);
    }


}
