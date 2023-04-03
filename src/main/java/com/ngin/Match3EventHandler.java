package com.ngin;

import java.io.IOException;
import java.util.List;

import commander.Command.TouchMotion;

public class Match3EventHandler extends EventHandler {
    Nx nx;
    int id;
    int x;
    int y;
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
            x = (int)Math.floor(info.x);
            y = (int)Math.floor(info.y);
            id = y*10+x;

            System.out.println(fruits[table[y][x]]);
        } else if (info.event == TouchMotion.MOVE_VALUE) { 
            nx.sendTransform(new Transform().setTranslating(info.x, info.y), id, 0);
        } else {
            nx.sendTransform(new Transform().setTranslating(x + 0.5f, y + 0.5f), id, 0);            
        }
    }
    
}
