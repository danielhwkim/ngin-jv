package com.ngin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.lang.Math;

import commander.Command.*;

public class GameEventHandler extends EventHandler {
    Ngin nx;       
    boolean keyDownLeft = false;
    boolean keyDownRight = false;
    Set<Integer> heroContacts = new HashSet<>();
    
    int heroJumpCount = 0;
    int dynamicId = 1000;
    boolean facingLeft = false;  
    boolean ready = false;
    int heroId = 100;

    GameEventHandler(Ngin ngin) {
        nx = ngin;
    }

    @Override
    public void contactHandler(ContactInfo contact) throws IOException, InterruptedException {
        if (contact.info1.equals("hero")) {
            if (contact.info2.equals("floor") || contact.info2.equals("bar")) {
                if (!contact.isEnded) {
                    if (contact.y < 0) {
                        if (heroContacts.isEmpty()) {
                            if (keyDownLeft || keyDownRight) {
                                nx.setClipType(contact.id1, NClipType.run, facingLeft);
                            } else {
                                nx.setClipType(contact.id1, NClipType.idle, facingLeft);
                            }
                            heroJumpCount = 0;
                        }
                        heroContacts.add(contact.id2);
                    } 
                } else {
                    if (heroContacts.contains(contact.id2)) {
                        heroContacts.remove(contact.id2);
                    }
                }
            } else if (contact.info2.equals("fruit")) {
                if (!contact.isEnded) {
                    nx.setClipType(contact.id2, NClipType.hit, facingLeft);
                }
            } else if (contact.info2.equals("box")) {
                if (!contact.isEnded) {
                    if (Math.abs(contact.y) > Math.abs(contact.x)) {
                        //
                    }/*
                    if (math.abs(contact.y) > math.abs(contact.x)):
                    obj = self.objs[contact.id2]
                    obj.count += 1
                    self.nx.set_clip_type(contact.id2, NClipType.hit, self.facingLeft)
                  if (contact.y < 0):
                    self.hero_jump_count = 0
                    self.nx.lineary(contact.id1, -20)*/
                }                
            } else if (contact.info2.equals("Trampoline")) {
                if (!contact.isEnded) {
                    nx.setClipType(contact.id2, NClipType.hit, facingLeft);
                    nx.lineary(contact.id1, -30);
                    heroJumpCount = 0;
                }                
            }
        }
    }    
    
    @Override
    public void eventHandler(EventInfo event) throws IOException {
        if (!event.completed) return;

        switch (event.info) {
        case "Trampoline":
            nx.setClipType(event.id, NClipType.idle);
            break;
        case "fruit":
            nx.remove(event.id);
            break;
        case "hero":
            nx.setClipType(event.id, NClipType.jump, facingLeft);
            break;
        }
    }

    @Override
    public void keyHandler(KeyInfo c) throws IOException, InterruptedException {
        if (!c.isPressed) {
            switch (c.name) {
            case "Arrow Left":
                keyDownLeft = false;
                if (keyDownRight) {
                    facingLeft = false;
                    nx.constx(heroId, 7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.run, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                } else {
                    nx.constx(heroId, 0);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.idle, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                } 
            break;					
            case "Arrow Right":
                keyDownRight = false;       
                if (keyDownLeft) {
                    facingLeft = true;
                    nx.constx(heroId, -7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.run, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                } else {
                    nx.constx(heroId, 0);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.idle, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                }             
            break;
            }
        } else {
            switch (c.name) {
            case "Arrow Left":
                if (!keyDownLeft) {
                    keyDownLeft = true;
                    facingLeft = true;
                    nx.constx(heroId, -7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.run, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                } else {
                    nx.setClipType(heroId, NClipType.noChange, facingLeft);
                }
                break;					
            case "Arrow Right":
                if (!keyDownRight) {
                    keyDownRight = true;
                    facingLeft = false;
                    nx.constx(heroId, 7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipType(heroId, NClipType.run, facingLeft);
                    } else {
                        nx.setClipType(heroId, NClipType.noChange, facingLeft);
                    }
                } else {
                    nx.setClipType(heroId, NClipType.noChange, facingLeft);
                }
                break;	
            case "Arrow Up":	
                if (!heroContacts.isEmpty()) {
                    nx.lineary(heroId, -20);
                    nx.setClipType(heroId, NClipType.jump, facingLeft);
                    heroJumpCount = 0;
                } else if (heroJumpCount < 1) {
                    nx.lineary(heroId, -20);
                    nx.setClipType(heroId, NClipType.doubleJump, facingLeft);
                    heroJumpCount = 1;
                }
                break;
            }
        }
    }

    @Override
    public void directionalHandler(DirectionalInfo info) throws IOException {
        switch(info.directional) {
            case JoystickMoveDirectional.MOVE_LEFT_VALUE:
              break;
            case JoystickMoveDirectional.MOVE_RIGHT_VALUE:
              break;
            default:
              break;
        }
    }

    @Override
    public void buttonHandler(ButtonInfo info) throws IOException, InterruptedException {
    }

}
