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
                onKey(new KeyInfo(info));
                break;
            case Head.contact_VALUE:
                onContact(new ContactInfo(info));
                break;
            case Head.event_VALUE:
                onEvent(new EventInfo(info));
                break;
            case Head.tap_VALUE:
                onTap(new TapInfo(info));
                break;
            case Head.directional_VALUE:
                onDirectional(new DirectionalInfo(info));
                break;
            case Head.button_VALUE:
                onButton(new ButtonInfo(info));
                break;
            case Head.queryresult_VALUE:
                onQueryResult(new QueryResultInfo(info));
                break;
            default:
                unexpected(info.toString());       
                break;
        }
    }

    public void onKey(KeyInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void onContact(ContactInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void onEvent(EventInfo info) throws IOException {
        unexpected(info.toString());
    }
    public void onTap(TapInfo info) throws IOException {
        unexpected(info.toString());
    }    
    public void onDirectional(DirectionalInfo info) throws IOException {
        unexpected(info.toString());
    }
    public void onButton(ButtonInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }
    public void onQueryResult(QueryResultInfo info) throws IOException, InterruptedException {
        unexpected(info.toString());
    }    
}
