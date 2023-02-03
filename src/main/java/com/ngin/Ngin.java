package com.ngin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.FileReader;
import java.util.EventListener;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import commander.*;
import commander.Command.*;


public class Ngin {
    String host;
    int port;
    Socket socket;
    PrintStream out;
    InputStream in;
    float precision = 3;

    int gid = 100;
    Recv receiver;
    LinkedBlockingQueue<NEvent> queue = new LinkedBlockingQueue<>();

    public Ngin() throws IOException {
        File file = new File(".ngin");
        Scanner reader = new Scanner(file);
        String conf = reader.nextLine();
        reader.close();
        String[] confs = conf.split(":");
        host = confs[0];
        port = Integer.parseInt(confs[1]);
        System.out.println(host);
        System.out.println(port);
        socket = new Socket(host, port);
        out = new PrintStream( socket.getOutputStream() );
        in = socket.getInputStream();
        receiver = new Recv(in, queue);
        receiver.start();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void send(Head head, byte[] data) throws IOException {
        byte[] bhead = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(head.getNumber()).array();
        byte[] bsize = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(data.length).array();            

        out.write(bhead);
        out.write(bsize);
        out.write(data);
        out.flush();
    }

    public RemoteAction sendStageWait(NStageInfo.Builder builder) throws IOException, InterruptedException  {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        System.out.println(String.format("sendStageWait(%d)", action.sn));
        send(Head.stage, builder.build().toByteArray());
        action.lock();
        return action;
    }

    public void sendStage(NStageInfo.Builder builder) throws IOException  {
        send(Head.stage, builder.build().toByteArray());      
    }

    public RemoteAction sendCmdWait(Cmd.Builder builder) throws IOException, InterruptedException {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        System.out.println(String.format("sendCmdWait(%d)", action.sn));
        send(Head.cmd, builder.build().toByteArray());
        action.lock();
        return action;
    }

    public void sendCmd(Cmd.Builder builder) throws IOException {
        send(Head.cmd, builder.build().toByteArray());
    }

    public RemoteAction sendObjWait(NObject.Builder builder) throws IOException, InterruptedException {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        System.out.println(String.format("sendObjWait(%d)", action.sn));
        send(Head.object, builder.build().toByteArray());
        action.lock();
        return action;
    }

    public void sendObj(NObject.Builder builder) throws IOException {
        send(Head.object, builder.build().toByteArray());
    }

    public NStageInfo.Builder stageBuilder(float width, float height) {
        NStageInfo.Builder c = NStageInfo.newBuilder();
        c.setBackground("Blue");
        c.setGravityX(0);
        c.setGravityY(0);
        c.setWidth(width);
        c.setHeight(height);
        c.setDebug(true);
        c.setJoystickDirectionals(JoystickDirectionals.none);
        c.setJoystickPrecision(3);
        c.setButton1(TouchMotion.DOWN);
        c.setButton2(TouchMotion.DOWN);
        return c;
    }

    public NObject.Builder tilesBuilder(String path, float tileWidth, float tileHeight, float width, float height, Iterable<Integer> data) {
        NObject.Builder c = NObject.newBuilder();
        c.setTid(0);
        NVisual.Builder v = c.getVisualBuilder();
        v.setCurrent(NClipType.tiles);
        v.setPriority(0);
        v.setX(0);
        v.setY(0);
        v.setWidth(width);
        v.setHeight(height);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0);
        v.setAnchorY(0);
        NClip.Builder a = NClip.newBuilder();
        a.addAllIndices(data);
        a.setPath(path);
        a.setStepTime(0.5f);
        a.setWidth(tileWidth);
        a.setHeight(tileHeight);
        a.setRepeat(false);
        a.setType(NClipType.tiles);
        v.addClips(a);
        return c;
    }

    public NObject.Builder objBuilder(int id, String info) {
        NObject.Builder c = NObject.newBuilder();
        c.setTid(0);
        c.setId(id);
        c.setInfo(info);
        return c;
    }

    public NBody.Builder bodyBuilder(BodyShape shape, float x, float y) {
        NBody.Builder p = NBody.newBuilder();
        p.setX(x);
        p.setY(y);
        p.setWidth(1f);
        p.setHeight(1f);
        p.setRestitution(0);
        p.setFriction(0);
        p.setDensity(0);
        p.setAngle(0);
        p.setIsSensor(false);
        p.setCategoryBits(0x0001);
        p.setMaskBits(0xFFFF);
        p.setFixedRotation(true);
        p.setType(BodyType.dynamicBody);
        p.setTrackable(true);
        p.setContactReport(true);
        p.setPassableBottom(false);
        p.setShape(shape);
        return p;
    }

    public NVisual.Builder visualBuilder(NClip.Builder[] builders) {
        NVisual.Builder v = NVisual.newBuilder();
        v.setCurrent(NClipType.idle);
        v.setPriority(0);
        v.setX(0);
        v.setY(0);
        v.setWidth(1);
        v.setHeight(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0.5f);
        v.setAnchorY(0.5f);
        for (int i=0; i<builders.length; i++) {
            v.addClips(builders[i]);
        }
        return v;
    }

    public NClip.Builder clipBuilder(String path, float width, float height, Iterable<Integer> indices, NClipType type, boolean repeat) {
        NClip.Builder a = NClip.newBuilder();
        a.addAllIndices(indices);
        a.setPath(path);
        a.setStepTime(0.5f);
        a.setX(0f);
        a.setY(0f);
        a.setWidth(width);
        a.setHeight(height);
        a.setRepeat(false);
        a.setType(type);
        a.setRepeat(repeat);
        return a;
    }

    public NClip.Builder clipBuilder(String path, float width, float height, Iterable<Integer> indices) {
        return clipBuilder(path, width, height, indices, NClipType.idle, true);
    }

    public void mainLoop(EventHandler handler) throws IOException, InterruptedException {
        while (!handler.completed) {
            handler.handle(queue.take());
        }
    }

    public void follow(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("follow");
        c.addInts(id);
        sendCmd(c);
    }

    public void remove(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("remove");
        c.addInts(id);
        sendCmd(c);
    }
    
    public void submit(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("submit");
        c.addInts(id);
        c.addInts(4041);
        sendCmd(c);
    }


    public NObjectInfo getObjInfo(int id) throws IOException, InterruptedException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("objinfo");
        c.addInts(id);
        RemoteAction action = sendCmdWait(c);
        return new NObjectInfo(action.event);
    }

    public void setClipType(int id, NClipType type, boolean isFlipHorizonal) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("clipType");
        c.addInts(id);
        c.addInts(isFlipHorizonal? 1:0);
        c.addInts(type.getNumber());                
        sendCmd(c);
    }

    public void setClipType(int id, NClipType type) throws IOException {
        setClipType(id, type, false);
    }
    
    public NEvent linearTo(int id, float x, float y, float speed) throws IOException, InterruptedException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linearTo");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);
        c.addFloats(speed);
        RemoteAction action = sendCmdWait(c);
        return action.event;        
    }

    public void forward(int id, float angle, float speed) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("forward");
        c.addInts(id);
        c.addFloats(angle);  
        c.addFloats(speed);                  
        sendCmd(c);
    }

    public void linear(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linear");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }

    public void force(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("force");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }

    public void impluse(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("impluse");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }    

    public void angular(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("angular");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }    

    public void torque(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("torque");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }    
    

    public void linearx(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linearx");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }        


    public void lineary(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("lineary");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }  

    public void constx(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("constx");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }  

    public void consty(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("consty");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }      

    public void timer(int id, float time) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("timer");
        c.addInts(id);
        c.addFloats(time);                  
        sendCmd(c);
    }
}
