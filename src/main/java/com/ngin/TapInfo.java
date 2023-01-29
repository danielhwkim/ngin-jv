package com.ngin;

import commander.Command.NEvent;

public class TapInfo {
    int info;
    int event;
    float x;
    float y;
    TapInfo(NEvent e) {
        info = e.getInts(1);
        event = e.getInts(2);
        x = e.getFloats(0);
        y = -e.getFloats(1);
    }

    public String toString() {
        return String.format("TapInfo info:%d event:%d (%f,%f)", info, event, x, y);
    }    
}
