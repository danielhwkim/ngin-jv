package com.ngin;
import java.io.IOException;

import commander.Command.*;

public class EventHandler {
    boolean completed = false;

    protected void unexpected(String info) {
        System.out.println("Unexpected Event?:" + info);
    }
    public void handle(NEvent info) throws IOException, InterruptedException {
        int head = info.getInts(0);

        switch(head) {
            case Head.key_VALUE:
                keyHandler(new KeyInfo(info));
                break;
            case Head.contact_VALUE:
                contactHandler(new ContactInfo(info));
                break;
            case Head.event_VALUE:
                eventHandler(new EventInfo(info));
                break;
            case Head.tap_VALUE:
                tapHandler(new TapInfo(info));
                break;
            case Head.directional_VALUE:
                directionalHandler(new DirectionalInfo(info));
                break;
            case Head.button_VALUE:
                buttonHandler(new ButtonInfo(info));
                break;
            case Head.queryresult_VALUE:
                queryResultHandler(new QueryResultInfo(info));
                break;
            default:
                unexpected(info.toString());       
                break;
        }
    }

    public void keyHandler(KeyInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void contactHandler(ContactInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void eventHandler(EventInfo info) throws IOException {
        unexpected(info.toString());
    }
    public void tapHandler(TapInfo info) throws IOException {
        unexpected(info.toString());
    }    
    public void directionalHandler(DirectionalInfo info) throws IOException {
        unexpected(info.toString());
    }
    public void buttonHandler(ButtonInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void queryResultHandler(QueryResultInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }    
}
