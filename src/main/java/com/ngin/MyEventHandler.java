package com.ngin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.lang.Math;

import commander.Command.*;

public class MyEventHandler extends EventHandler {
    Ngin nx;       
    boolean keyDownLeft = false;
    boolean keyDownRight = false;
    Set<Integer> actor_contacts = new HashSet<>();
    
    int actorJumpCount = 0;
    int dynamicId = 1000;
    boolean facingLeft = false;  
    boolean ready = false;      
    MyEventHandler(Ngin ngin) {
        nx = ngin;
    }

    @Override
    public void contactHandler(CmdInfo info) throws IOException {
        ContactInfo contact = info.getContact();

        if (contact.getInfo2().equals("missile")) {
            nx.remove(contact.getId2());

            CObject.Builder obj = nx.objBuilder(getDynamicId(), "fire");
            obj.setTid(contact.getId1());
            
            CAction.Builder[] as = new CAction.Builder[1];
            List<Integer> tileList = new ArrayList<>();
            tileList.add(1);
            as[0] =nx.actionBuilder("kenney_pixelshmup/tiles_packed.png", 16, tileList);
            CVisible.Builder v = nx.visibleBuilder(as);
            obj.setVisible(v);

            nx.sendCObj(obj);
            nx.receiveAck();
        }        
    }    
    
    @Override
    public void eventHandler(CmdInfo info) throws IOException {
        EventInfo event = info.getEvent();

        if (event.getInfo().equals("missile")) {
            nx.remove(event.getId());
        }
    }


    public void goRight() throws IOException {
        nx.angular(100, 1);
    }

    public void goLeft() throws IOException {
        nx.angular(100, -1);
    }

    public void stop() throws IOException {
        nx.angular(100, 0);
    }

    public int getDynamicId() {
        dynamicId += 1;
        return dynamicId;
    }


    public void missile() throws IOException
    {

        CObjectInfo info = new CObjectInfo(nx.getObjInfo(100));
        System.out.println("angle:" + info.angle);
        int newId = getDynamicId();

        CObject.Builder obj = nx.objBuilder(newId, "missile");

        CPhysical.Builder p = nx.physicalBuilder(BodyShape.rectangle,(float)(info.x-0.5 + 2*Math.sin(info.angle)), (float)(info.y-0.5 - 2*Math.cos(info.angle)));
        p.setAngle(info.angle);
        obj.setPhysical(p);

        CAction.Builder[] as = new CAction.Builder[1];
        List<Integer> tileList = new ArrayList<>();
        tileList.add(1);
        tileList.add(2);
        tileList.add(3);

        as[0] =nx.actionBuilder("kenney_pixelshmup/tiles_packed.png", 16, tileList);
        CVisible.Builder v = nx.visibleBuilder(as);
        obj.setVisible(v);

        nx.sendCObj(obj);
        nx.receiveAck();

        nx.forward(newId, 0, 20);
        nx.timer(newId, (float)0.7);
    }

    @Override
    public void keyHandler(CmdInfo info) throws IOException {
        KeyInfo c = info.getKey();

        if (c.getIsPressed() == false) {
            switch (c.getName()) {
            case "Arrow Left":
                keyDownLeft = false;
                if (keyDownRight) {
                    goRight();
                } else {
                    stop();
                } 
            break;					
            case "Arrow Right":
                keyDownRight = false;       
                if (keyDownLeft) {
                    goLeft();
                } else {
                    stop();
                }             
            break;
            }
        } else {
            switch (c.getName()) {
            case "Arrow Left":
                keyDownLeft = true;         
                goLeft();
                break;					
            case "Arrow Right":
                keyDownRight = true;            
                goRight();
                break;	
            case "Arrow Up":	
                missile();
                break;
            }
        }
    }

    @Override
    public void directionalHandler(CmdInfo info) throws IOException {
        DirectionalInfo directional = info.getDirectional();

        switch(directional.getDirection()) {
            case MOVE_LEFT:
              goLeft();
              break;
            case MOVE_RIGHT:
              goRight();
              break;
            default:
              stop();
              break;
        }
    }

    @Override
    public void buttonHandler(CmdInfo info) throws IOException {
        missile();
    }

}
