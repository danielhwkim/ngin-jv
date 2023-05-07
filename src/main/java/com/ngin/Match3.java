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
        float height = 10;

        nx.new Stage(width, height).enableTap().enableDebug().sendWithAck();
        nx.new Visible(0.5f, 0.5f).addClip("Items/Fruits/Bananas.png", 32f, 32f).send(100, "fruit");
        nx.timer(3, () -> {
            try {
                nx.new Visible(1.5f, 1.5f).addClip("Items/Fruits/Bananas.png", 32f, 32f).send(100, "fruit");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        nx.runEventLoop(this);
    }
    public static void main( String[] args ) throws IOException, InterruptedException {
        new Match3().run();
    }
}
