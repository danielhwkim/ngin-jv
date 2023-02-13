package com.ngin;

import commander.Command.Cmd;

public class Transform {
    boolean translating = false;
    float tx;
    float ty;
    boolean scaling = false;
    float sx;
    float sy;
    boolean rotating = false;
    float a;

    public Transform setTranslating(float x, float y) {
        this.tx = x;
        this.ty = y;
        this.translating = true;
        return this;
    }

    public Transform setScaling(float x, float y) {
        this.sx = x;
        this.sy = y;
        this.scaling = true;
        return this;
    }

    public Transform setRotating(float a) {
        this.a = a;
        this.rotating = true;
        return this;
    }

    Cmd.Builder builder(int id, float time, String type, boolean needAck) {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("transform");
        c.addStrings(type);
        c.addInts(id);
        c.addInts(needAck? 1:0);
        c.addFloats(time);
    
        if (this.translating) {
          c.addInts(1);
          c.addFloats(this.tx);
          c.addFloats(this.ty);
        } else {
          c.addInts(0);
          c.addFloats(0);
          c.addFloats(0);
        }
    
        if (this.scaling) {
          c.addInts(1);
          c.addFloats(this.sx);
          c.addFloats(this.sy);
        } else {
          c.addInts(0);
          c.addFloats(0);
          c.addFloats(0);
        }
    
        if (this.rotating) {
          c.addInts(1);
          c.addFloats(this.a);
        } else {
          c.addInts(0);
          c.addFloats(0);
        }
    
        return c;        
    }

    Cmd.Builder builder(int id, float time) {
        return builder(id, time, "easeInOut", false);
    }
}
