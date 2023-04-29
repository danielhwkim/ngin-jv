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
import commander.Command.NClipType;
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
 
    Board board;

    Vec2 posOrigin;
    Int2 other;

    final String[] fruitNames = {"Bananas", "Pineapple", "Cherries", "Orange", "Apple", "Melon", "Strawberry", "Kiwi"};

    void addFruit(Item item) throws IOException {
        NClip.Builder[] cs = new NClip.Builder[fruitNames.length+1];

        for (int i=0; i<fruitNames.length; i++) {
            cs[i] = nx.clipBuilder("Items/Fruits/" + fruitNames[i] + ".png", 32f, 32f, 0.05f);
        }

        cs[fruitNames.length] = nx.clipBuilder("Items/Fruits/Collected.png", 32f, 32f, NClipType.once, 0.05f);
        NVisual.Builder v = nx.visualBuilder(cs, 0.5f + item.pos.x, 0.5f + item.pos.y);
        v.setCurrent(item.fruit);
        nx.sendObj(nx.objBuilder(item.id, "fruit").setVisual(v));   
    }

    class Item {
        int fruit;
        int id;
        Int2 pos;
        boolean removed = false;

        Item(Int2 p, int fruitId, int objId) {
            pos = p;
            fruit = fruitId;
            id = objId;
        }

        @Override
        public String toString() {
            return String.format("%s [%d] %s", fruitNames[fruit], id, pos.toString());
        }
    }

    class Board {
        private Item[][] items;
        int width;
        int height;

        Board(int w, int h) {
            width = w;
            height = h;
            items = new Item[width][height];
        }

        Item get(int x, int y) {
            return items[x][y];
        }

        Item get(Int2 pos) {
            return get(pos.x, pos.y);
        }

        void set(Item item) {
            items[item.pos.x][item.pos.y] = item;
        }
        
        void exchange(Int2 i1, Int2 i2) {
            Item item1 = get(i1);
            Item item2 = get(i2);
            item1.pos = i2;
            item2.pos = i1;
            set(item1);
            set(item2);
        }

        boolean inside(Int2 pos) {
            return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
        }
    }



    class Int2 {
        int x;
        int y;
        Int2(int i0, int i1) {
            x = i0;
            y = i1;
        }

        Int2 add(Int2 int2) {
            return new Int2(x+int2.x, y+int2.y);
        }

        @Override
        public String toString() {
            return String.format("Int2(%d, %d)", x, y);
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

        Int2 floor() {
            return new Int2(xFloored(), yFloored());
        }

        double distanceSquared(float x2, float y2) {
            double dx = x2 - x;
            double dy = y2 - y;
            return dx*dx + dy*dy;
        }

        @Override
        public String toString() {
            return String.format("[%f, %f]", x, y);
        }
    }

    enum MoveType {
        IDLE,
        USER,
        AUTO
    }

    MoveType moveType = MoveType.IDLE;



    void move(Int2 d) throws IOException {
        Int2 origin = posOrigin.floor();
        other = origin.add(d);
        float time = 0.3f;

        if (board.inside(other)) {
            nx.sendTransform(new Transform().setTranslating(origin.x + 0.5f, origin.y + 0.5f), board.get(other).id, time);
            nx.sendTransformEvent(new Transform().setTranslating(other.x + 0.5f, other.y + 0.5f), board.get(origin).id, time);
            board.exchange(origin, other);
        } else {
            other = null;            
            nx.sendTransformEvent(new Transform().setTranslating(origin.x + 0.5f, origin.y + 0.5f), board.get(origin).id, time);
        }
    }

    int countFruits(Int2 ori, int fruit, Int2 d) {
        int count = 0;
        Int2 pos = ori.add(d);
        while(board.inside(pos) && board.get(pos).fruit == fruit) {
            count += 1;            
            pos = pos.add(d);
        }
        return count;
    }

    void checkFruits(Item item) throws IOException, InterruptedException {
        Int2 pos = item.pos;
        int fruit = item.fruit;

        int left = countFruits(pos, fruit, new Int2(-1, 0));
        int right = countFruits(pos, fruit, new Int2(1, 0));
        int up = countFruits(pos, fruit, new Int2(0, -1));
        int down = countFruits(pos, fruit, new Int2(0, 1));

        System.out.println(String.format("%s left:%d, right:%d, up:%d, down:%d", item.toString(), left, right, up, down));

        if (left + right > 1) {
            for (int i = pos.x-left; i <= pos.x+right; i++) {
                Item m = board.get(i, pos.y);
                if (!m.removed) {
                    m.removed = true;
                    nx.playClipWait(m.id, 1);
                }

            }
        }

        if (up + down > 1) {
            for (int i = pos.y-up; i <= pos.y+down; i++) {
                Item m = board.get(pos.x, i);
                if (!m.removed) {
                    m.removed = true;
                    nx.playClipWait(m.id, 1);
                }

            }
        }
    }

    @Override
    public void onEvent(EventInfo info) throws IOException, InterruptedException {
        if (moveType == MoveType.AUTO) {
            if (other != null) {
                Int2 origin = posOrigin.floor();
                System.out.println(origin);
                System.out.println(other);
                Item i1 = board.get(origin);
                Item i2 = board.get(other);

                if (i1.fruit != i2.fruit) {
                    checkFruits(i1);
                    checkFruits(i2);
                }
            }
            moveType = MoveType.IDLE;
        }
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        if (info.event == TouchMotion.DOWN_VALUE && moveType == MoveType.IDLE) {
            posOrigin = new Vec2(info.x, info.y);
            if (board.inside(posOrigin.floor())) {
                moveType = MoveType.USER;
            }
        } else if (info.event == TouchMotion.MOVE_VALUE && moveType == MoveType.USER) {
            double dx = info.x - posOrigin.x;
            double dy = info.y - posOrigin.y;
            double dx2 = dx*dx;
            double dy2 = dy*dy;

            if (dx2 + dy2 < 0.5*0.5) {
                nx.sendTransform(new Transform().setTranslating(info.x, info.y), board.get(posOrigin.floor()).id, 0);
            } else {
                moveType = MoveType.AUTO;

                if (dx2 > dy2) {
                    if (dx>0) {
                        move(new Int2(1, 0));
                    } else {
                        move(new Int2(-1, 0));                        
                    }
                } else {
                    if (dy>0) {
                        move(new Int2(0, 1));                        
                    } else {
                        move(new Int2(0, -1));                        
                    }                    
                }
            }
        } else {
            if (moveType == MoveType.USER) {
                nx.sendTransform(new Transform().setTranslating(posOrigin.xFloored() + 0.5f, posOrigin.yFloored() + 0.5f), board.get(posOrigin.floor()).id, 0); 
                moveType = MoveType.IDLE;
            }      
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

            board = new Board((int)width, (int)height);

            
            for (int x =0; x<width; x++) {
                for (int y = 0; y<height; y++) {
                    int fruit = rand.nextInt(fruitNames.length);
                    Item item = new Item(new Int2(x, y), fruit, x*100 + y);
                    board.set(item);
                    addFruit(item);
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
