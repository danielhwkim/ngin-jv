package com.ngin;

import commander.Command.NEvent;

public class ErrorInfo {
    String name;
    int code;
    ErrorInfo(NEvent event) {
        name = event.getStrings(0);
        code = event.getInts(1);
    }
    public String toString() {
        return String.format("ErrorInfo name:%s code:%d", name, code);
    }
}
