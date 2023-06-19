package com.ngin;
import java.io.IOException;

import com.google.common.util.concurrent.AbstractIdleService;

import commander.Command.TouchMotion;

public class Switch2 extends EventHandler {
    Nx nx;
    boolean first = true;

    float ax, ay, bx, by;
    int ida, idb;
    int selectedId = 0;

    double distanceSquared(float x1, float y1, float x2, float y2) {
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        System.out.println(info);
        //nx.new Transform().translate(info.x, info.y).send(100); 
        if (info.motion == TouchMotion.DOWN) {

            if (distanceSquared(info.x, info.y, ax, ay) < 2) {
                selectedId = ida;
            } else if (distanceSquared(info.x, info.y, bx, by) < 2) {
                selectedId = idb;
            } else {
                first = !first;

                if (first) {
                    ida = 100;
                    idb = 200;
                } else {
                    ida = 200;
                    idb = 100;
                }

                nx.new Transform().translate(ax, ay).send(ida, 0.5f);
                nx.new Transform().translate(bx, by).send(idb, 0.5f);  
            }
        } else if (info.motion == TouchMotion.MOVE) {
            if (selectedId != 0) {
                nx.new Transform().translate(info.x, info.y).send(selectedId);
            }
        } else {
            if (selectedId == ida) {
                ax = info.x;
                ay = info.y;
            } else if (selectedId == idb) {
                bx = info.x;
                by = info.y;
            }
            selectedId = 0;
        }
    }

    public void run() throws IOException, InterruptedException {
        nx = new Nx();
        float width = 5;
        float height = 10.5f;

        ax = 0.5f;
        ay = 0.5f;
        bx = 5.5f;
        by = 7.5f;
        ida = 100;
        idb = 200;

        nx.new Stage(width, height).enableTap().enableDebug().sendWithAck();
        nx.new Visible(ax, ay).addClip("Items/Fruits/Strawberry.png", 32f, 32f).send(ida);
        nx.new Visible(bx, by).addClip("Items/Fruits/Bananas.png", 32f, 32f).send(idb);

        nx.runEventLoop(this);
    }
    public static void main( String[] args ) throws IOException, InterruptedException {
        new Switch2().run();
    }
}
