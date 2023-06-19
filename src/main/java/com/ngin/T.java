package com.ngin;
import java.io.IOException;

import com.ngin.Nx.Visible;

public class T extends EventHandler {
    Nx nx;

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        System.out.println(info);
        nx.new Transform().translate(info.x, info.y).send(100); 
    }

    public void run() throws IOException, InterruptedException {
        nx = new Nx();
        float width = 10;
        float height = 20.5f;

        nx.new Stage(width, height).enableTap().enableDebug().sendWithAck();
        Visible v = nx.new Visible(0.5f, 0.5f).addClip("Background/Yellow.png", 64f, 64f).send(100).setPos(3f, 5f).send(300);
        v.setPos(0.5f, 1.5f).send(301);
        v.setPos(0.5f, 2.5f).send(302);
        v.setPos(1.5f, 2.5f).send(303);

        nx.runEventLoop(this);
    }
    public static void main( String[] args ) throws IOException, InterruptedException {
        new T().run();
    }
}
