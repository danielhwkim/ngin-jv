package com.ngin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
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
    Socket socket;
    PrintStream out;
    InputStream in;
    Recv receiver;
    float precision = 3;

    LinkedBlockingQueue<NEvent> queue = new LinkedBlockingQueue<>();

    public Ngin(int port) throws IOException {
        File file = new File(".ngin");
        Scanner reader = new Scanner(file);
        String conf = reader.nextLine();
        reader.close();
        String[] confs = conf.split(":");
        String host = confs[0];
        if (port == 0) {
            port = Integer.parseInt(confs[1]);
        }
        System.out.println(host);
        System.out.println(port);
        socket = new Socket(host, port);
        out = new PrintStream( socket.getOutputStream() );
        in = socket.getInputStream();
        receiver = new Recv(in, queue);
        receiver.start();
    }

    public Ngin() throws IOException {
        this(0);
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

    public RemoteAction sendCmdWait(Cmd.Builder builder) throws IOException, InterruptedException {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        //System.out.println(String.format("sendCmdWait(%d)", action.sn));
        send(Head.cmd, builder.build().toByteArray());
        action.lock();
        return action;
    }

    public void sendCmd(Cmd.Builder builder) throws IOException {
        send(Head.cmd, builder.build().toByteArray());
    }
}
