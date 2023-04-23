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

    float width = 5;
    float height = 10;    
    Item[][] items;

    Item itemOrigin;
    Vec2 posOrigin;    

    final String[] fruits = {"Bananas", "Pineapple", "Cherries", "Orange", "Apple", "Melon", "Strawberry", "Kiwi"};

    void addFruit(int id, String name, float x, float y) throws IOException {
        NClip.Builder[] cs = {nx.clipBuilder("Items/Fruits/" + name + ".png", 32f, 32f, 0.05f)};
        NVisual.Builder v = nx.visualBuilder(cs, 0.5f + x, 0.5f + y);
        nx.sendObj(nx.objBuilder(id, "fruit").setVisual(v));   
    }

    class Item {
        int fruit;
        int id;
        Item(int fruitId, int objId) {
            fruit = fruitId;
            id = objId;
        }
    }
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
            posOrigin = new Vec2(info.x, info.y);
            itemOrigin = items[posOrigin.xFloored()][posOrigin.yFloored()];
        } else if (info.event == TouchMotion.MOVE_VALUE) { 
            nx.sendTransform(new Transform().setTranslating(info.x, info.y), itemOrigin.id, 0);
        } else {
            nx.sendTransform(new Transform().setTranslating(posOrigin.xFloored() + 0.5f, posOrigin.yFloored() + 0.5f), itemOrigin.id, 0);            
        }
    }
    
    public void run() {
        try
        {
            nx = new Nx();


            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setTap(TouchMotion.ALL);
            nx.sendStageWait(builder);

            Random rand = new Random();

            items = new Item[(int)width][(int)height];

            
            for (int x =0; x<width; x++) {
                for (int y = 0; y<height; y++) {
                    int fruit = rand.nextInt(fruits.length);
                    int id = x*100 + y;
                    items[x][y] = new Item(fruit, id);
                    addFruit(id, fruits[fruit], x, y);
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
