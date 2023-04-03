package com.ngin;

import java.io.IOException;
import java.util.List;

import commander.Command.TouchMotion;

public class Match3EventHandler extends EventHandler {
    Nx nx;
    int id;
    int[][] table;
    String[] fruits;
    Match3EventHandler(Nx n, int[][] t, String[] f) {
        nx = n;
        table = t;
        fruits = f;
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        if (info.event == TouchMotion.DOWN_VALUE) {
            int xn = (int)Math.floor(info.x);
            int yn = (int)Math.floor(info.y);
            id = yn*10+xn;

            System.out.println(fruits[table[yn][xn]]);
        } else if (info.event == TouchMotion.MOVE_VALUE) { 
            nx.sendTransform(new Transform().setTranslating(info.x, info.y), id, 0);
        }
    }
    
}
