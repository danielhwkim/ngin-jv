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

    List<String> fruits = Arrays.asList("Bananas", "Pineapple", "Cherries", "Orange", "Apple", "Melon", "Strawberry", "Kiwi");

    void addFruit(int id, String name, float x, float y) throws IOException {
        Builder o = nx.objBuilder(id, "fruit");
        
        NClip.Builder[] cs = new NClip.Builder[1];
        cs[0] = nx.clipBuilder("Items/Fruits/" + name + ".png", 32f, 32f, new ArrayList<Integer>());
        cs[0].setStepTime(0.05f);
        NVisual.Builder v = nx.visualBuilder(cs);
        v.setX(0.5f + x);
        v.setY(0.5f + y);
        o.setVisual(v);
        nx.sendObj(o);   
    }
    
    
    public void run() {
        try
        {
            nx = new Nx();
            float width = 5;
            float height = 10;

            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setBackground("");
            builder.setTap(TouchMotion.ALL);
            //builder.setJoystickDirectionals(JoystickDirectionals.horizontal);
            //builder.setDebug(false);
            nx.sendStageWait(builder);
            int size = fruits.size();
            Random rand = new Random();

            int[][] table = new int[(int)height][(int)width];
            addFruit(100, fruits.get(5), 1, 1);
            Transform t = new Transform();
            nx.sendTransformWait(t.setTranslating(4,7).setScaling(3, 3).setRotating((float)Math.PI*2), 100, 1);

            /* for (int y = 0; y<height; y++) {
                for (int x =0; x<width; x++) {
                    int index = rand.nextInt(size);
                    table[y][x] = index;
                    addFruit(y*10+x, fruits.get(index), x, y);
                }
            }

            for (int y = 0; y<height; y++) {
                for (int x =0; x<width; x++) {
                    System.out.println(table[y][x]);
                }
            }  */

          
            

            nx.mainLoop(new Match3EventHandler(nx, table, fruits));
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
