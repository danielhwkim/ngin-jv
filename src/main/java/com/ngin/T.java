package com.ngin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.HashMultimap;
import com.ngin.Nx.Visible;

public class T extends EventHandler {
    Nx nx;

    T() throws IOException {
        nx = new Nx();
    }

    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        super.onTap(info);
    }

    public void run() throws IOException, InterruptedException {
        nx.new Stage(10, 20).enableDebug(true).sendWithAck();
        HashMap<Integer, Visible> m = new HashMap<>();
        
        m.put(100, nx.new Visible(0.5f, 0.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(101, nx.new Visible(0.5f, 1.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(102, nx.new Visible(0.5f, 2.5f).addClip("Background/Yellow.png", 64, 64));
        m.put(103, nx.new Visible(1.5f, 2.5f).addClip("Background/Yellow.png", 64, 64));

        for (int i : m.keySet()) {
            m.get(i).send(i);
        }

        for (int i : m.keySet()) {
            Visible v = m.get(i);
            v.setPos(v.v.getX(), v.v.getY()+10);
            nx.new Transform().translate(v.v.getX(), v.v.getY()).send(i, 2.5f);
        }        

        nx.runEventLoop(this);
    }

    public static void main(String[] args) {
        System.out.println("Hello");
        try {
            new T().run();
        } catch (Exception e) {
            System.out.println("Exception - " + e.toString());
        }
    }
    
}
