package com.ngin;

import commander.Command.NEvent;

public class EventInfo {
    int id;
    String type;
    String info;
    boolean on;
    float distance;
    int id2;
    float x;
    float y;
    boolean completed;

    EventInfo(NEvent c) {
        id = c.getInts(2);
        type = c.getStrings(0);
        if ("distance".equals(type)) {
            on = c.getInts(1) == 1;
            distance = c.getFloats(0);
            id2 = c.getInts(3);
        } else {
            completed = c.getInts(1) == 1;
            info = c.getStrings(1);
            x = c.getFloats(0);
            y = c.getFloats(1);
        }
    }

    public String toString() {
        if ("distance".equals(info)) {
            return String.format("Distance %s for %d and %d", on? "on":"off", id, id2);
        } else {
            return String.format("EventInfo %s - %s for %d %s (%f, %f)", type, completed? "completed":"not completed", id, info, x, y);
        }
    }
}
