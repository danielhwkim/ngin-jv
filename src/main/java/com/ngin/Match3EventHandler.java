package com.ngin;

import java.io.IOException;
import java.util.List;

public class Match3EventHandler extends EventHandler {
    Nx nx;
    float x_old;
    float y_old;
    int[][] table;
    List<String> fruits;
    Match3EventHandler(Nx n, int[][] t, List<String> f) {
        nx = n;
        table = t;
        fruits = f;
    }

    @Override
    public void onTap(TapInfo info) throws IOException {
        //unexpected(info.toString());
        //System.out.println(info.toString());

        if (info.event == 1) {
            x_old = info.x;
            y_old = info.y;
            int x = (int)Math.floor(info.x);
            int y = (int)Math.floor(info.y);
            int oid = y*10 + x;
            System.out.println(fruits.get(table[y][x]));
            //nx.remove(y*10 + x);
            //nx.linearTo(oid, x, y+1, 5);

        } else if (info.event == 4) {
            float x_diff = info.x - x_old;
            float y_diff = info.y - y_old;

            float x_abs2 = x_diff>0?x_diff:-x_diff;

            float x_abs = Math.abs(x_diff);
            float y_abs = Math.abs(y_diff);

            if (x_abs > y_abs) {
                if (x_diff > 0) {
                    System.out.println("RIGHT");
                } else {
                    System.out.println("LEFT");
                }
            } else {
                if (y_diff > 0) {
                    System.out.println("DOWN");
                } else {
                    System.out.println("UP");
                }
            }


        }

        //int x = (int)Math.floor(info.x);
        //int y = (int)Math.floor(info.y);
        //nx.transform();
        //nx.remove(y*10 + x);
    }
    
}
