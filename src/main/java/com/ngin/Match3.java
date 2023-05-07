package com.ngin;
import java.io.IOException;

public class Match3 extends EventHandler {
    Nx nx;

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        System.out.println(info);
        nx.new Transform().translate(info.x, info.y).send(100); 
    }

    public void run() throws IOException, InterruptedException {
        nx = new Nx();
        float width = 5;
        float height = 10.5f;

        nx.new Stage(width, height).enableTap().enableDebug().sendWithAck();
        nx.new Visible(0.5f, 0.5f).addClip("Items/Fruits/Bananas.png", 32f, 32f).send(100);
        nx.timer(1, () -> { 
            nx.new Visible(1.5f, 1.5f).addClip("Items/Fruits/Bananas.png", 32f, 32f).sendWithAck(200);
            nx.new Transform().translate(5, 5).rotate((float)Math.PI*6).scale(2, 2).setOpacity(0.1f).send(200, 2, () -> {
                nx.new Transform().translate(1.5f, 1.5f).rotate(0).scale(1, 1).setOpacity(1f).send(200, 2);
            });
        });

        nx.runEventLoop(this);
    }
    public static void main( String[] args ) throws IOException, InterruptedException {
        new Match3().run();
    }
}
