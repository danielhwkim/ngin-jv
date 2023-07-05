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
    int angle = 0;
    
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
                if (getRight()<9.5 && canMoveRight()) {
                    move(1f, 0.1f);
                }
            } else if (info.name.equals("Arrow Left")) {
                if (getLeft()>0.5 && canMoveLeft()) {
                    move(-1f, 0.1f);
                }
            } else if (info.name.equals("Arrow Up")) {
                rotate(0.1f);
            }
        }
    }

    public void rotate(float time) throws IOException {
        // shape: L
        Visible v0 = m.get(100);
        Visible v1 = m.get(101);
        Visible v2 = m.get(102);
        Visible v3 = m.get(103);


        if (angle == 0) {
            Vector2 v0_newValue = v0.getPos().add(nx.new Vector2(1f, 1f));
            v0.setPos(v0_newValue);
            nx.new Transform().translate(v0_newValue).send(100, time);
            
            Vector2 v2_newValue = v2.getPos().add(nx.new Vector2(-1f, -1f));
            v2.setPos(v2_newValue);
            nx.new Transform().translate(v2_newValue).send(102, time);

            Vector2 v3_newValue = v3.getPos().add(nx.new Vector2(-2f, 0f));
            v3.setPos(v3_newValue);
            nx.new Transform().translate(v3_newValue).send(103, time);
            angle = 90;
        } else if (angle == 90) {


            angle = 180;
        } else if (angle == 180) {

            angle = 270;
        } else if (angle == 270) {

            angle = 0;
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

    public boolean canMoveLeft() {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (map[(int)v.getPos().x-1][(int)v.getPos().y]) {
                return false;
            }
        }
        return true;
    }

    public boolean canMoveRight() {
        for (int i : m.keySet()) {
            Visible v = m.get(i);
            if (map[(int)v.getPos().x+1][(int)v.getPos().y]) {
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
