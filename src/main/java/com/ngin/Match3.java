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
import java.io.IOException;
import java.util.List;

import commander.Command.TouchMotion;

public class Match3 extends EventHandler {
    Nx nx;

    String[] fruits = {"Bananas", "Pineapple", "Cherries", "Orange", "Apple", "Melon", "Strawberry", "Kiwi"};

    void addFruit(int id, String name, float x, float y) throws IOException {
        NClip.Builder[] cs = {nx.clipBuilder("Items/Fruits/" + name + ".png", 32f, 32f, 0.05f)};
        NVisual.Builder v = nx.visualBuilder(cs, 0.5f + x, 0.5f + y);
        nx.sendObj(nx.objBuilder(id, "fruit").setVisual(v));   
    }

    int id;
    Vec2 ori;
    int[][] table;

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

            table = new int[(int)height][(int)width];

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

            nx.runEventLoop(this);
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }    
}
