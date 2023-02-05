package com.ngin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.lang.Math;

import commander.Command.*;

class HeroAction {
    static final int NOCHANGE = -1;
    static final int IDLE=0;
    static final int RUN=1;
    static final int JUMP=2;
    static final int HIT=3;
    static final int FALL=4;
    static final int WALLJUMP=5;
    static final int DOUBLEJUMP=6;
}

public class GameEventHandler extends EventHandler {
    Nx nx;       
    boolean keyDownLeft = false;
    boolean keyDownRight = false;
    Set<Integer> heroContacts = new HashSet<>();
    
    int heroJumpCount = 0;
    int dynamicId = 1000;
    boolean facingLeft = false;  
    boolean ready = false;
    int heroId = 100;

    GameEventHandler(Nx n) {
        nx = n;
    }

    @Override
    public void onContact(ContactInfo contact) throws IOException, InterruptedException {
        if (contact.info1.equals("hero")) {
            if (contact.info2.equals("floor") || contact.info2.equals("bar")) {
                if (!contact.isEnded) {
                    if (contact.y < 0) {
                        if (heroContacts.isEmpty()) {
                            if (keyDownLeft || keyDownRight) {
                                nx.setClipIndex(contact.id1, HeroAction.RUN, facingLeft);
                            } else {
                                nx.setClipIndex(contact.id1, HeroAction.IDLE, facingLeft);
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
                    nx.setClipIndex(contact.id2, 1, facingLeft);
                }
            } else if (contact.info2.equals("box")) {
                if (!contact.isEnded) {
                    if (Math.abs(contact.y) > Math.abs(contact.x)) {
                        //
                    }/*
                    if (math.abs(contact.y) > math.abs(contact.x)):
                    obj = self.objs[contact.id2]
                    obj.count += 1
                    self.nx.set_clip_type(contact.id2, HeroAction.HIT, self.facingLeft)
                  if (contact.y < 0):
                    self.hero_jump_count = 0
                    self.nx.lineary(contact.id1, -20)*/
                }                
            } else if (contact.info2.equals("Trampoline")) {
                if (!contact.isEnded) {
                    nx.setClipIndex(contact.id2, 1, facingLeft);
                    nx.lineary(contact.id1, -30);
                    heroJumpCount = 0;
                }                
            }
        }
    }    
    
    @Override
    public void onEvent(EventInfo event) throws IOException {
        if (!event.completed) return;

        switch (event.info) {
        case "Trampoline":
            nx.setClipIndex(event.id, HeroAction.IDLE);
            break;
        case "fruit":
            nx.remove(event.id);
            break;
        case "hero":
            nx.setClipIndex(event.id, HeroAction.JUMP, facingLeft);
            break;
        }
    }

    @Override
    public void onKey(KeyInfo c) throws IOException, InterruptedException {
        if (!c.isPressed) {
            switch (c.name) {
            case "Arrow Left":
                keyDownLeft = false;
                if (keyDownRight) {
                    facingLeft = false;
                    nx.constx(heroId, 7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipIndex(heroId, HeroAction.RUN, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                    }
                } else {
                    nx.constx(heroId, 0);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipIndex(heroId, HeroAction.IDLE, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                    }
                } 
            break;					
            case "Arrow Right":
                keyDownRight = false;       
                if (keyDownLeft) {
                    facingLeft = true;
                    nx.constx(heroId, -7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipIndex(heroId, HeroAction.RUN, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                    }
                } else {
                    nx.constx(heroId, 0);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipIndex(heroId, HeroAction.IDLE, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
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
                        nx.setClipIndex(heroId, HeroAction.RUN, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                    }
                } else {
                    nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                }
                break;					
            case "Arrow Right":
                if (!keyDownRight) {
                    keyDownRight = true;
                    facingLeft = false;
                    nx.constx(heroId, 7);
                    if (!heroContacts.isEmpty()) {
                        nx.setClipIndex(heroId, HeroAction.RUN, facingLeft);
                    } else {
                        nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                    }
                } else {
                    nx.setClipIndex(heroId, HeroAction.NOCHANGE, facingLeft);
                }
                break;	
            case "Arrow Up":	
                if (!heroContacts.isEmpty()) {
                    nx.lineary(heroId, -20);
                    nx.setClipIndex(heroId, HeroAction.JUMP, facingLeft);
                    heroJumpCount = 0;
                } else if (heroJumpCount < 1) {
                    nx.lineary(heroId, -20);
                    nx.setClipIndex(heroId, HeroAction.DOUBLEJUMP, facingLeft);
                    heroJumpCount = 1;
                }
                break;
            }
        }
    }

    @Override
    public void onDirectional(DirectionalInfo info) throws IOException {
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
    public void onButton(ButtonInfo info) throws IOException, InterruptedException {
    }

}
