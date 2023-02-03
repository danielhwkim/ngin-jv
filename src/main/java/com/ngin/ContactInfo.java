package com.ngin;

import commander.Command.NEvent;

public class ContactInfo {
    boolean isEnded;
    String info1;
    String info2;
    int id1;
    int id2;
    float x;
    float y;
    float x1;
    float y1;
    float x2;
    float y2;

    ContactInfo(NEvent c) {
        isEnded = c.getInts(1) == 1;
        info1 = c.getStrings(0);
        info2 = c.getStrings(1);
        id1 = c.getInts(2);
        id2 = c.getInts(3);
        x = c.getFloats(0);
        y = c.getFloats(1);
        x1 = c.getFloats(2);
        y1 = c.getFloats(3);
        x2 = c.getFloats(4);
        y2 = c.getFloats(5);
    }
    public String toString() {
        return String.format("ContactInfo %s (x:%f, y:%f) (%s, id:%d, x:%f, y:%f) (%s, id:%d, x:%f, y:%f)", (isEnded)? "Ended":"Begun", x, y, info1, id1, x1, y1, info2, id2, x2, y2);
    }
}
