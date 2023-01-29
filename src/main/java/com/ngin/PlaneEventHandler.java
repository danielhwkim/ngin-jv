package com.ngin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.lang.Math;

import commander.Command.*;

public class PlaneEventHandler extends EventHandler {
    Ngin nx;       
    boolean keyDownLeft = false;
    boolean keyDownRight = false;
    Set<Integer> actor_contacts = new HashSet<>();
    
    int actorJumpCount = 0;
    int dynamicId = 1000;
    boolean facingLeft = false;  
    boolean ready = false;      
    PlaneEventHandler(Ngin ngin) {
        nx = ngin;
    }

    @Override
    public void contactHandler(ContactInfo contact) throws IOException, InterruptedException {
        if (contact.info2.equals("missile")) {
            nx.remove(contact.id2);

            NObject.Builder obj = nx.objBuilder(getDynamicId(), "fire");
            obj.setTid(contact.id1);
            
            NClip.Builder[] as = new NClip.Builder[1];
            List<Integer> tileList = new ArrayList<>();
            tileList.add(1);
            as[0] =nx.clipBuilder("kenney_pixelshmup/tiles_packed.png", 16, 16, tileList);
            NVisual.Builder v = nx.visualBuilder(as);
            obj.setVisual(v);

            nx.sendObjWait(obj);
        }        
    }    
    
    @Override
    public void eventHandler(EventInfo event) throws IOException {
        if (event.info.equals("missile")) {
            nx.remove(event.id);
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


    public void missile() throws IOException, InterruptedException
    {

        NObjectInfo info = nx.getObjInfo(100);
        System.out.println("angle:" + info.angle);
        System.out.println(info);
        int newId = getDynamicId();

        NObject.Builder obj = nx.objBuilder(newId, "missile");

        NBody.Builder p = nx.bodyBuilder(BodyShape.rectangle,(float)(info.x + 2*Math.sin(info.angle)), (float)(info.y - 2*Math.cos(info.angle)));
        p.setAngle(info.angle);
        obj.setBody(p);

        NClip.Builder[] as = new NClip.Builder[1];
        List<Integer> tileList = new ArrayList<>();
        tileList.add(1);
        tileList.add(2);
        tileList.add(3);

        as[0] =nx.clipBuilder("kenney_pixelshmup/tiles_packed.png", 16, 16, tileList);
        NVisual.Builder v = nx.visualBuilder(as);
        obj.setVisual(v);

        nx.sendObjWait(obj);

        nx.forward(newId, 0, 20);
        nx.timer(newId, (float)0.7);
    }

    @Override
    public void keyHandler(KeyInfo c) throws IOException, InterruptedException {
        if (!c.isPressed) {
            switch (c.name) {
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
            switch (c.name) {
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
    public void directionalHandler(DirectionalInfo info) throws IOException {
        switch(info.directional) {
            case JoystickMoveDirectional.MOVE_LEFT_VALUE:
              goLeft();
              break;
            case JoystickMoveDirectional.MOVE_RIGHT_VALUE:
              goRight();
              break;
            default:
              stop();
              break;
        }
    }

    @Override
    public void buttonHandler(ButtonInfo info) throws IOException, InterruptedException {
        missile();
    }

}
