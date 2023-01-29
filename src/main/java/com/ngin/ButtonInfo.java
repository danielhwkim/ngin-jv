package com.ngin;

import commander.Command.NEvent;

public class ButtonInfo {
    int input;
    int event;

    ButtonInfo(NEvent c) {
        input = c.getInts(1);
        event = c.getInts(2);
    }

    @Override
    public String toString() {
        return String.format("ButtonInfo input:%d event:%d", input, event);
    }
}
