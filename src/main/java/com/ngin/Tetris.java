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
        protected Part[] parts;
        protected int angle;
        protected Vector8[] v8;

        protected void initV8(float x, float y, Vector8 a0, Vector8 a90, Vector8 a180, Vector8 a270) {
            angle = 0;
            parts = new Part[4];
            parts[0] = new Part(newId(), 0.5f + x + a0.data[0], 0.5f + y + a0.data[1]);
            parts[1] = new Part(newId(), 0.5f + x + a0.data[2], 0.5f + y + a0.data[3]);
            parts[2] = new Part(newId(), 0.5f + x + a0.data[4], 0.5f + y + a0.data[5]);
            parts[3] = new Part(newId(), 0.5f + x + a0.data[6], 0.5f + y + a0.data[7]);
                        
            v8 = new Vector8[4];            
            v8[0] = new Vector8(a90.subtract(a0));
            v8[1] = new Vector8(a180.subtract(a90));
            v8[2] = new Vector8(a270.subtract(a180));
            v8[3] = new Vector8(a0.subtract(a270));   
        }

       class Vector8 {
            protected float[] data;
            Vector8(float[] data) {
                this.data = data;
            }

            float[] subtract(Vector8 other) {
                return new float[] {
                    data[0] - other.data[0],
                    data[1] - other.data[1],
                    data[2] - other.data[2],
                    data[3] - other.data[3],
                    data[4] - other.data[4],
                    data[5] - other.data[5],
                    data[6] - other.data[6],
                    data[7] - other.data[7],                                                                                                                                            
                };
            }

            float[] add(float x, float y) {
                return new float[] {
                    data[0] + x,
                    data[1] + y,
                    data[2] + x,
                    data[3] + y,
                    data[4] + x,
                    data[5] + y,
                    data[6] + x,
                    data[7] + y,                                                                                                                                            
                };                
            }
        }        


        Shape(int x, int y) {

            Vector8 a0 = new Vector8(new float[] {0f, 1f, 1f, 1f, 2f, 1f, 2f, 0f});
            Vector8 a90 = new Vector8(new float[] {1f, 0f, 1f, 1f, 1f, 2f, 2f, 2f});
            Vector8 a180 = new Vector8(new float[] {2f, 1f, 1f, 1f, 0f, 1f, 0f, 2f});
            Vector8 a270 = new Vector8(new float[] {1f, 2f, 1f, 1f, 1f, 0f, 0f, 0f});

            initV8(x, y, a0, a90, a180, a270);            
        }

        Shape() {
            this(0, 0);
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
                return v8[0].data;
            } else if (angle == 90) {
                if (x < 1) {
                    return v8[1].add(1, 0);
                } else {
                    return v8[1].data;
                }
            } else if (angle == 180) {
                return v8[2].data;
            } else {
                if (x > 9) {
                    return v8[2].add(-1, 0);
                } else {
                    return v8[3].data;
                }

            }
        }            

    }

    class ShapeL extends Shape {

    }

    class ShapeI extends Shape {
        ShapeI(int x, int y) {
            Vector8 a0 = new Vector8(new float[] {0f, 1f, 1f, 1f, 2f, 1f, 3f, 1f});
            Vector8 a90 = new Vector8(new float[] {2f, 0f, 2f, 1f, 2f, 2f, 2f, 3f});
            Vector8 a180 = new Vector8(new float[] {0f, 2f, 1f, 2f, 2f, 2f, 3f, 2f});
            Vector8 a270 = new Vector8(new float[] {1f, 0f, 1f, 1f, 1f, 2f, 1f, 3f});
            initV8(x, y, a0, a90, a180, a270);                  
        }

        @Override
        public float[] getChanges() {
            float x = parts[0].x;
            if (angle == 0) {
                return v8[0].data;
            } else if (angle == 90) {
                return v8[1].data;
            } else if (angle == 180) {
                return v8[2].data;
            } else {
                return v8[3].data;
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
        //shape = new ShapeL();
        shape = new ShapeI(0, 0);
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
