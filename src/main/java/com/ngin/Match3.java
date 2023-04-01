package com.ngin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.*;

import commander.Command.BodyShape;
import commander.Command.JoystickDirectionals;
import commander.Command.NClip;
import commander.Command.NObject;
import commander.Command.NStageInfo;
import commander.Command.NBody;
import commander.Command.NVisual;
import commander.Command.TouchMotion;
import commander.Command.NObject.Builder;

public class Match3 {
    Nx nx;

    String[] fruits = {"Bananas", "Pineapple", "Cherries", "Orange", "Apple", "Melon", "Strawberry", "Kiwi"};

    void addFruit(int id, String name, float x, float y) throws IOException {
        NClip.Builder[] cs = {nx.clipBuilder("Items/Fruits/" + name + ".png", 32f, 32f, 0.05f)};
        NVisual.Builder v = nx.visualBuilder(cs, 0.5f + x, 0.5f + y);
        nx.sendObj(nx.objBuilder(id, "fruit").setVisual(v));   
    }
    
    
    public void run() {
        try
        {
            nx = new Nx();
            float width = 5;
            float height = 10;

            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setTap(TouchMotion.ALL);
            nx.sendStageWait(builder);

            Random rand = new Random();

            int[][] table = new int[(int)height][(int)width];
            //addFruit(100, fruits[5], 1, 1);
            //Transform t = new Transform();
            //nx.sendTransformWait(t.setTranslating(4,7).setScaling(3, 3).setRotating((float)Math.PI*2), 100, 1);

            for (int y = 0; y<height; y++) {
                for (int x =0; x<width; x++) {
                    int index = rand.nextInt(fruits.length);
                    table[y][x] = index;
                    addFruit(y*10+x, fruits[index], x, y);
                }
            }

            for (int y = 0; y<height; y++) {
                for (int x =0; x<width; x++) {
                    System.out.println(table[y][x]);
                }
            }

          
            

            nx.runEventLoop(new Match3EventHandler(nx, table, fruits));
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
