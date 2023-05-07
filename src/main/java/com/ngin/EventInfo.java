package com.ngin;

import commander.Command.NEvent;

public class EventInfo {
    int id;
    int sn;
    String type;
    String info;
    boolean on;
    float distance;
    int id2;
    float x;
    float y;
    boolean completed;

    EventInfo(NEvent c) {
        type = c.getStrings(0);
        if ("distance".equals(type)) {
            id = c.getInts(2);            
            on = c.getInts(1) == 1;
            distance = c.getFloats(0);
            id2 = c.getInts(3);
        } else if ("timeout".equals(type)) {
            sn = c.getInts(1);
            if (sn == 0) {
                info = c.getStrings(1);
                id = c.getInts(2);
            }
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
        } else if ("timeout".equals(type)) {
            if (sn == 0) {
                return String.format("Timeout sn:%d", sn);
            } else {
                return String.format("Timeout %d %s", id, info);
            }
        }else {
            return String.format("EventInfo %s - %s for %d %s (%f, %f)", type, completed? "completed":"not completed", id, info, x, y);
        }
    }
}
