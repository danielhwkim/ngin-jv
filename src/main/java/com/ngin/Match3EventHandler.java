package com.ngin;

import java.io.IOException;
import java.util.List;

import commander.Command.TouchMotion;

public class Match3EventHandler extends EventHandler {
    Nx nx;
    int id;
    Vec2 ori;
    int[][] table;
    String[] fruits;

    class Vec2 {
        float x;
        float y;
        Vec2(float f0, float f1) {
            x = f0;
            y = f1;
        }

        int xFloored() {
            return (int)Math.floor(x);
        }
        int yFloored() {
            return (int)Math.floor(y);
        }        
    }

    Match3EventHandler(Nx n, int[][] t, String[] f) {
        nx = n;
        table = t;
        fruits = f;
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        if (info.event == TouchMotion.DOWN_VALUE) {
            ori = new Vec2(info.x, info.y);
            id = ori.yFloored()*10+ori.xFloored();

            //System.out.println(fruits[table[y][x]]);
        } else if (info.event == TouchMotion.MOVE_VALUE) { 
            nx.sendTransform(new Transform().setTranslating(info.x, info.y), id, 0);
        } else {
            nx.sendTransform(new Transform().setTranslating(ori.xFloored() + 0.5f, ori.yFloored() + 0.5f), id, 0);            
        }
    }
    
}
