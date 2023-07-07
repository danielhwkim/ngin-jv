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
    //HashMap<Integer, Visible> m;
    Shape shape;
    int width, height;

    private int steps = 0;
    private boolean highSpeed = false;

    boolean[][] map;

    Tetris() throws IOException {
        width = 10;
        height = 20;
        nx = new Nx();
        map = new boolean[width][height];
    }

    class Part {
        float x;
        float y;
        int id;

        Part(int id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        Part add(float x, float y) {
            this.x += x;
            this.y += y;
            return this;
        }
    }
    
    private int _newId = 99;
    int newId() {
        _newId += 1;
        return _newId;
    }
    
    class Shape {
        private Part[] parts;
        int angle;

        Shape() {
            angle = 0;
            parts = new Part[4];
            parts[0] = new Part(newId(), 0.5f, 0.5f);
            parts[1] = new Part(newId(), 0.5f, 1.5f);
            parts[2] = new Part(newId(), 0.5f, 2.5f);
            parts[3] = new Part(newId(), 1.5f, 2.5f);
        }

        public void send() throws IOException {
            for (Part p: parts) {
                nx.new Visible(p.x, p.y).addClip("Background/Yellow.png", 64, 64).send(p.id);
            }
        }

        public boolean canTransform(float[] changes) {
            int index = 0;
            for (Part p : parts) {
                if (p.y > 19.5) return false;
                if (map[(int)(p.x + changes[index])][(int)(p.y + changes[index+1])]) return false;
                index += 2;                
            }
            return true;
        }

        public void transform(float[] changes, float time) throws IOException {
            int index = 0;

            for (Part p: parts) {
                p.add(changes[index], changes[index+1]);
                nx.new Transform().translate(p.x, p.y).send(p.id, time);
                index += 2;                
            }
        }


        public void rotate(float time) throws IOException {
            
            // shape: L
            float[] changes = getChanges();

            if (canTransform(changes)) {
                transform(changes, time);
                angle += 90;
                if (angle == 360) angle = 0;
            } else {
                //
            }
        }

        public void moveDown(float dist, float time) throws IOException {
            for (Part p : parts) {
                p.y += dist;
                nx.new Transform().translate(p.x, p.y).send(p.id, time);
            }  
        }

        public void move(float dist, float time) throws IOException {
            for (Part p : parts) {
                p.x += dist;
                nx.new Transform().translate(p.x, p.y).send(p.id, time);
            }
        }

        public float getBottom() {
            float bottom = 0;
            for (Part p : parts) {
                if (p.y > bottom) {
                    bottom = p.y;
                }
            }
            return bottom;
        }

        public boolean canMoveDown() {
            for (Part p : parts) {
                if (map[(int)p.x][(int)p.y+1]) {
                    return false;
                }
            }
            return true;
        }

        public boolean canMoveLeft() {
            for (Part p : parts) {
                if (map[(int)p.x-1][(int)p.y]) {
                    return false;
                }
            }
            return true;
        }

        public boolean canMoveRight() {
            for (Part p : parts) {
                if (map[(int)p.x+1][(int)p.y]) {
                    return false;
                }
            }
            return true;
        }

        public float getLeft() {
            float left = 9;
            for (Part p : parts) {
                if (p.x < left) {
                    left = p.x;
                }
            }
            return left;
        } 
        
        public float getRight() {
            float right = 0;
            for (Part p : parts) {
                if (p.x > right) {
                    right = p.x;
                }
            }
            return right;
        }

        public void updateMap() {
            for (Part p : parts) {
                map[(int)p.x][(int)p.y] = true;
            }
        }

        public float[] getChanges() {
            float x = parts[0].x;

            if (angle == 0) {
                if (x < 1) {
                    return new float[] {2f, 1f, 1f, 0f, 0f, -1f, -1f, 0f};
                } else {
                    return new float[] {1f, 1f, 0f, 0f, -1f, -1f, -2f, 0f};
                }
            } else if (angle == 90) {
                return new float[] {-1f, 1f, 0f, 0f, 1f, -1f, 0f, -2f};
            } else if (angle == 180) {
                if (x > 9) {
                    return new float[] {-2f, -1f, -1f, 0f, 0f, 1f, 1f, 0f};          
                } else {
                    return new float[] {-1f, -1f, 0f, 0f, 1f, 1f, 2f, 0f};
                }
            } else {
                return new float[] {1f, -1f, 0f, 0f, -1f, 1f, 0f, 2f};
            }
        }

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
                if (shape.getRight()<9.5 && shape.canMoveRight()) {
                    shape.move(1f, 0.1f);
                }
            } else if (info.name.equals("Arrow Left")) {
                if (shape.getLeft()>0.5 && shape.canMoveLeft()) {
                    shape.move(-1f, 0.1f);
                }
            } else if (info.name.equals("Arrow Up")) {
                shape.rotate(0.1f);
            } else if (info.name.equals("Arrow Down")) {
                highSpeed = true;
            }
        }
    }

    public void newPiece() throws IOException {
        highSpeed = false;
        steps = 0;
        shape = new Shape();
        shape.send();
    }

    public void run() throws IOException, InterruptedException {
        nx.new Stage(width, height).enableDebug(true).sendWithAck();
        newPiece();


        Timer timer = new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (highSpeed == false) {
                    
                    if (steps == 0)
                    {
                        //System.out.println("Print in every second");
                        try {
                            //System.out.println(getBottom());
                            if (shape.getBottom()<19 && shape.canMoveDown()) {
                                shape.moveDown(1f, 0.1f);
                            } else {
                                shape.updateMap();
                                newPiece();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    steps += 1;
                    if (steps == 6) steps = 0;
                    
                } else {
                    //System.out.println("Print in every second");
                    try {
                        //System.out.println(getBottom());
                        if (shape.getBottom()<19 && shape.canMoveDown()) {
                            shape.moveDown(1f, 0.05f);
                        } else {
                            shape.updateMap();
                            newPiece();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
		}, 0, 50);
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
