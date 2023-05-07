package com.ngin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import commander.Command.*;

public class Recv extends Thread {
    InputStream in;
    HashMap<Integer, RemoteAction> remoteActions = new HashMap<>();
    BlockingQueue<NEvent> q;
    boolean running = true;

    Recv(InputStream inputStream, BlockingQueue<NEvent> queue) {
        in = inputStream;
        q = queue;
    }

    public RemoteAction addRemoteAction() throws InterruptedException {
        RemoteAction action = new RemoteAction();
        remoteActions.put(action.sn, action);
        return action;
    }

    public RemoteAction getRemoteAction(int sn) {
        return remoteActions.get(sn);
    }

    public boolean processRemoteAction(NEvent event) {
        RemoteAction action = getRemoteAction(event.getInts(1));
        if (action != null) {
            remoteActions.remove(action.sn);
            action.event = event;
            action.unlock();
            return true;
        }
        return false;
    }

    @Override
    public void run()  {
        try {
            eventLoop();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }   
    
    
    void eventLoop() throws IOException {
        while (running) {
            int code = -1;
            ByteBuffer bufLen = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());            
            in.read(bufLen.array(), 0, 4);
            int len = bufLen.getInt();
            //System.out.println("len:" + len);
    
            ByteBuffer bufData = ByteBuffer.allocate(len).order(ByteOrder.nativeOrder());

            int off = 0;
            byte[] bufBytes = bufData.array();
            while(off < len) {
                int count = in.read(bufBytes, off, len-off);
                if (count < 0) continue;
                off += count;
            }
            //System.out.println("data:" + bufData.array());
    
            NEvent event = NEvent.parseFrom(bufData);
            //System.out.println("data:" + NEvent);
            int head = event.getInts(0);
    
            switch(head) {
                case Head.ack_VALUE:
                    if (!processRemoteAction(event)) {
                        System.out.println("Unexpected ACK:" + event);
                    }
                    break;
                case Head.cmd_VALUE:
                    if (!processRemoteAction(event)) {
                        System.out.println("Unexpected CMD:" + event);
                    }                    
                    break;
                default:
                    q.add(event);
                    break;
            }
        }
    }
}
