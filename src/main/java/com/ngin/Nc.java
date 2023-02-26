package com.ngin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import commander.Command.Cmd;
import commander.Command.Head;

public class Nc extends Ngin {
    public Nc() throws IOException {
        super(4041);
    }

    private Cmd.Builder buildRelayCmd(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats) {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("relay");

        c.addAllStrings(strings);
        c.addInts(Head.relay_VALUE);
        if (ints != null) {
            c.addAllInts(ints);
        }
        if (floats != null) {
            c.addAllFloats(floats);
        }
        return c;
    }

    public void relay(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats, boolean needAck) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(strings, ints, floats);
        if (needAck) {
            sendCmdWait(c);
        } else {
            sendCmd(c);
        }
    }

    public void relay(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(strings, ints, floats);        
        sendCmd(c);
    }

    public void relayWait(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(strings, ints, floats);
        sendCmdWait(c);        
    }

    public int relayCmd(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(strings, ints, floats);

        RemoteAction remoteAction = sendCmdWait(c);
        return remoteAction.event.getInts(2);
    }

    public void relayAStringWait(String name) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(Arrays.asList(name), null, null);
        sendCmdWait(c);
    }

    public int relayCmdAString(String name) throws IOException, InterruptedException {
        Cmd.Builder c = buildRelayCmd(Arrays.asList(name), null, null);

        RemoteAction remoteAction = sendCmdWait(c);
        return remoteAction.event.getInts(2);
    }    

    public void moveRight() throws IOException, InterruptedException {
        relayAStringWait("moveRight");
    }
    public void moveLeft() throws IOException, InterruptedException {
        relayAStringWait("moveLeft");
    }
    public void pick() throws IOException, InterruptedException {
        relayAStringWait("pick");
    }
    public int compare() throws IOException, InterruptedException {
        return relayCmdAString("compare");
    }        
    public int submit() throws IOException, InterruptedException {
        return relayCmdAString("submit");
    }    
}
