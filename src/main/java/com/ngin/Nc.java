package com.ngin;

import java.io.IOException;

import commander.Command.Cmd;
import commander.Command.Head;

public class Nc extends Ngin {
    public Nc() throws IOException {
        super(4041);
    }

    public void relay(Iterable<String> strings, Iterable<Integer> ints, Iterable<Float> floats) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("relay");
        c.addAllStrings(strings);
        c.addInts(Head.relay_VALUE);
        c.addAllInts(ints);
        c.addAllFloats(floats);             
        sendCmd(c);
    }    
}
