package com.ngin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import commander.Command.*;

public class Recv {
    InputStream in;
    boolean returnAck = false;
    boolean returnCmd = false;
    EventHandler handler;

    Recv(InputStream inputStream) {
        in = inputStream;
        handler = new EventHandler();
    }

    public int receiveAck() throws IOException {
        returnAck = true;
        CmdInfo info = eventLoop();
        returnAck = false;

        AckInfo ackInfo = info.getAck();

        System.out.println("code:" + ackInfo.getCode());
        return ackInfo.getCode();      
    }

    public Cmd receiveCmd() throws IOException {
        returnCmd = true;
        CmdInfo info = eventLoop();
        returnCmd = false;
        
        Cmd cmd = info.getCmd();

        //System.out.println("cmd:" + cmd);
        return cmd;      
    }    
    
    
    CmdInfo eventLoop() throws IOException {
        while (true) {
            int code = -1;
            ByteBuffer bufLen = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());            
            in.read(bufLen.array(), 0, 4);
            int len = bufLen.getInt();
            //System.out.println("len:" + len);
    
            ByteBuffer bufData = ByteBuffer.allocate(len).order(ByteOrder.nativeOrder());

            int off = 0;
            byte[] bufBytes = bufData.array();
            while(off < len) {
                int count = in.read(bufBytes, off, len);
                if (count < 0) continue;
                off += count;
            }
            //System.out.println("data:" + bufData.array());
    
            CmdInfo cmdInfo = CmdInfo.parseFrom(bufData);
            //System.out.println("data:" + cmdInfo);
    
            switch(cmdInfo.getHead()) {
                case ack:
                    if (returnAck) {
                        return cmdInfo;
                    } else {
                        AckInfo ackInfo = cmdInfo.getAck();

                        System.out.println("Unexpected ACK:" + ackInfo.getCode());
                    }
                    break;
                case cmd:
                    if (returnCmd) {
                        return cmdInfo;
                    } else {
                        Cmd cmd = cmdInfo.getCmd();

                        System.out.println("Unexpected CMD:" + cmd);
                    }                
                    break;
                default:
                    handler.handle(cmdInfo);
                    break;
            }
        }
    }
}
