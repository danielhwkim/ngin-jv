package com.ngin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.HashMultimap;
import com.ngin.Nx.Vector2;
import com.ngin.Nx.Visible;

public class Tetris extends EventHandler {
    Nx nx;
    HashMap<Integer, Visible> m;
    int width, height;
    
    boolean[][] map;

    Tetris() throws IOException {
        width = 10;
        height = 20;
        nx = new Nx();
        map = new boolean[width][height];
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        super.onTap(info);
    }

    @Override
    public void onKey(KeyInfo info)  throws IOException, InterruptedException {
        System.out.println(info);
        if (info.isPressed) {
            if (info.name.equals("Arrow Right")) {
                if (getRight()<9.5) {
                    move(1f, 0.1f);
                }
            } else if (info.name.equals("Arrow Left")) {
                if (getLeft()>0.5) {
                    move(-1f, 0.1f);
                }
            }
        }
    }

    public void moveDown(float dist, float time) throws IOException {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            Vector2 v2 = v.getPos().add(nx.new Vector2(0f, dist));

            v.setPos(v2);
            nx.new Transform().translate(v2).send(i, time);
        }  
    }

    public void move(float dist, float time) throws IOException {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            Vector2 v2 = v.getPos().add(nx.new Vector2(dist, 0f));

            v.setPos(v2);
            nx.new Transform().translate(v2).send(i, time);
        }  
    }

    public float getBottom() {
        float bottom = 0;
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (v.getPos().y > bottom) {
                bottom = v.getPos().y;
            }
        }
        return bottom;
    }

    public boolean canMoveDown() {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (map[(int)v.getPos().x][(int)v.getPos().y+1]) {
                return false;
            }
        }
        return true;
    }    

    public float getLeft() {
        float left = 9;
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (v.getPos().x < left) {
                left = v.getPos().x;
            }
        }
        return left;
    } 
    
    public float getRight() {
        float right = 0;
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (v.getPos().x > right) {
                right = v.getPos().x;
            }
        }
        return right;
    }

    public void updateMap() {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            map[(int)v.getPos().x][(int)v.getPos().y] = true;
        }
    }    
    
    public void newPiece() throws IOException {
        m = new HashMap<>(); 

        m.put(100, nx.new Visible(0.5f, 0.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(101, nx.new Visible(0.5f, 1.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(102, nx.new Visible(0.5f, 2.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(103, nx.new Visible(1.5f, 2.5f).addClip("Background/Yellow.png", 64, 64));

        for (int i : m.keySet()) {
            m.get(i).send(i);
        }
    }

    public void run() throws IOException, InterruptedException {
        nx.new Stage(width, height).enableDebug(true).sendWithAck();
        newPiece();

        Timer timer = new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Print in every second");
                try {
                    //System.out.println(getBottom());
                    if (getBottom()<19 && canMoveDown()) {
                        moveDown(1f, 0.1f);
                    } else {
                        updateMap();
                        newPiece();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
		}, 0, 300);



        nx.runEventLoop(this);
    }

    public static void main(String[] args) {
        System.out.println("Hello");
        try {
            new Tetris().run();
        } catch (Exception e) {
            System.out.println("Exception - " + e.toString());
        }
    }
    
}
