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

import commander.*;
import commander.Command.*;


public class Ngin {
    String host;
    int port;
    Socket socket;
    PrintStream out;
    InputStream in;

    double width = 12;
    double height = 12;
    double margin = 3;
    int gid = 100;
    Recv receiver;

    public Ngin() throws IOException {
        File file = new File(".ngin");
        Scanner reader = new Scanner(file);
        String conf = reader.nextLine();
        //System.out.println();
        reader.close();
        String[] confs = conf.split(":");
        host = confs[0];
        port = Integer.parseInt(confs[1]);
        System.out.println(host);
        System.out.println(port);
        socket = new Socket(host, port);
        out = new PrintStream( socket.getOutputStream() );
        in = socket.getInputStream();
        /*
        out.close();
        in.close();
        socket.close();
        socket = new Socket(host, port);
        out = new PrintStream( socket.getOutputStream() );
        in = socket.getInputStream();*/
        receiver = new Recv(in);
    }

    public void setEventHandler(EventHandler eventHandler) {
        receiver.handler = eventHandler;
    }

    public void close() throws IOException {
        // Close our streams
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

    public int receiveAck() throws IOException {
        return receiver.receiveAck();
    }

    public void sendStage(CStageInfo.Builder builder) throws IOException  {
        send(Head.stage, builder.build().toByteArray());
        //receiveAck();        
    }

    public void sendCmd(Cmd.Builder builder) throws IOException {
        send(Head.cmd, builder.build().toByteArray());
    }

    public void sendCObj(CObject.Builder builder) throws IOException {
        send(Head.cobject, builder.build().toByteArray());
    }

    /*
    public void sendCmd(Cmd.Builder builder, boolean needsAck) throws IOException {
        send(Head.cmd, builder.build().toByteArray());

        if (needsAck) {
            receiveAck();
        }
    }

    public void sendCObj(CObject.Builder builder, boolean needsAck) throws IOException {
        send(Head.cobject, builder.build().toByteArray());

        if (needsAck) {
            receiveAck();
        }        
    } 

    public void sendCmd(Cmd.Builder builder) throws IOException {
        sendCmd(builder, false);
    }

    public void sendCObj(CObject.Builder builder) throws IOException {
        sendCObj(builder, false);      
    }*/



    public CStageInfo.Builder stageBuilder(float width, float height) {
        CStageInfo.Builder c = CStageInfo.newBuilder();
        c.setBackground("Blue");
        c.setGravityX(0);
        c.setGravityY(0);
        c.setWidth(width);
        c.setHeight(height);
        c.setDebug(true);
        c.setJoystickDirectionals(JoystickDirectionals.none);
        c.setJoystickPrecision(3);
        c.setButton1(ActionEvent.DOWN);
        c.setButton2(ActionEvent.DOWN);
        return c;
    }

    public CObject.Builder tilesBuilder(String path, float tileSize, float width, float height, Iterable<Integer> data) {
        CObject.Builder c = CObject.newBuilder();
        c.setTid(0);
        CVisible.Builder v = c.getVisibleBuilder();
        v.setCurrent(CActionType.tiles);
        v.setPriority(0);
        v.setX(0);
        v.setY(0);
        v.setWidth(width);
        v.setHeight(height);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0);
        v.setAnchorY(0);
        CAction.Builder a = CAction.newBuilder();
        a.addAllIndices(data);
        a.setPath(path);
        a.setStepTime(200/1000);
        a.setTileSizeX(tileSize);
        a.setTileSizeY(tileSize);
        a.setRepeat(false);
        a.setType(CActionType.tiles);
        v.addActions(a);
        return c;
    }

    public CObject.Builder objBuilder(int id, String info) {
        CObject.Builder c = CObject.newBuilder();
        c.setTid(0);
        c.setId(id);
        c.setInfo(info);
        return c;
    }

    public CPhysical.Builder physicalBuilder(BodyShape shape, float x, float y) {
        CPhysical.Builder p = CPhysical.newBuilder();
        p.setX(x);
        p.setY(y);
        p.setWidth(0);
        p.setHeight(0);
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

    public CVisible.Builder visibleBuilder(CAction.Builder[] builders) {
        CVisible.Builder v = CVisible.newBuilder();
        v.setCurrent(CActionType.idle);
        v.setPriority(0);
        v.setX(0);
        v.setY(0);
        v.setWidth(1);
        v.setHeight(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0.5);
        v.setAnchorY(0.5);
        for (int i=0; i<builders.length; i++) {
            v.addActions(builders[i]);
        }
        return v;
    }

    public CAction.Builder actionBuilder(String path, float tileSize, Iterable<Integer> indices, CActionType type, boolean repeat) {
        CAction.Builder a = CAction.newBuilder();
        a.addAllIndices(indices);
        a.setPath(path);
        a.setStepTime(200./1000.);
        a.setTileSizeX(tileSize);
        a.setTileSizeY(tileSize);
        a.setRepeat(false);
        a.setType(type);
        a.setRepeat(repeat);
        return a;
    }

    public CAction.Builder actionBuilder(String path, float tileSize, Iterable<Integer> indices) {
        return actionBuilder(path, tileSize, indices, CActionType.idle, true);
    }

    public void mainLoop() throws IOException {
        receiver.eventLoop();
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


    public Cmd getObjInfo(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("objinfo");
        c.addInts(id);
        sendCmd(c);
        return receiver.receiveCmd();
    }

    public void setActionType(int id, CActionType type, boolean isFlipHorizonal) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("actionType");
        c.addInts(id);
        c.addInts(isFlipHorizonal? 1:0);
        c.addInts(type.getNumber());                
        sendCmd(c);
    }

    public void setActionType(int id, CActionType type) throws IOException {
        setActionType(id, type, false);
    }
    
    public Cmd linearTo(int id, float x, float y, float speed) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linearTo");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);
        c.addFloats(speed);
        sendCmd(c);
        return receiver.receiveCmd();        
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
