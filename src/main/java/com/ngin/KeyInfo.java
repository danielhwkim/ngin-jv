package com.ngin;

import commander.Command.NEvent;

public class KeyInfo {
    String name;
    boolean isPressed;

    KeyInfo(NEvent c) {
        name = c.getStrings(0);
        isPressed = c.getInts(1) == 1;
    }

    public String toString() {
        return String.format("KeyInfo isPressed:%s name:%s", isPressed? "true":"false", name);
    }    
}
