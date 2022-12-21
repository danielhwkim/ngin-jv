package com.ngin;
import java.io.IOException;

import commander.Command.*;

public class EventHandler {

    protected void unexpected(CmdInfo info) {
        System.out.println("Unexpected Event?:" + info);
    }
    public void handle(CmdInfo info) throws IOException {
        switch(info.getHead()) {
            case key:
                keyHandler(info);
                break;
            case contact:
                contactHandler(info);
                break;
            case event:
                eventHandler(info);
                break;
            case directional:
                directionalHandler(info);
                break;
            case button:
                buttonHandler(info);
                break;
            default:
                unexpected(info);       
                break;
        }
    }

    public void keyHandler(CmdInfo info) throws IOException {
        unexpected(info);
    }
    public void contactHandler(CmdInfo info) throws IOException {
        unexpected(info);
    }
    public void eventHandler(CmdInfo info) throws IOException {
        unexpected(info);
    }
    public void directionalHandler(CmdInfo info) throws IOException {
        unexpected(info);
    }
    public void buttonHandler(CmdInfo info) throws IOException {
        unexpected(info);
    }
}
