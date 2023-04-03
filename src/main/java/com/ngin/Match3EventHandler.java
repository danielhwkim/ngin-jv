package com.ngin;

import java.io.IOException;
import java.util.List;

import commander.Command.TouchMotion;

public class Match3EventHandler extends EventHandler {
    Nx nx;
    float x, y;
    int[][] table;
    String[] fruits;
    Match3EventHandler(Nx n, int[][] t, String[] f) {
        nx = n;
        table = t;
        fruits = f;
    }

    @Override
    public void onTap(TapInfo info) throws IOException {
        if (info.event == TouchMotion.DOWN_VALUE) {
            x = info.x;
            y = info.y;
            int xn = (int)Math.floor(info.x);
            int yn = (int)Math.floor(info.y);

            System.out.println(fruits[table[yn][xn]]);
        }
    }
    
}
