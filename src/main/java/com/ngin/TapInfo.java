package com.ngin;

import commander.Command.NEvent;
import commander.Command.TouchInputId;
import commander.Command.TouchMotion;

public class TapInfo {
    TouchMotion motion;
    TouchInputId inputId;
    float x;
    float y;

    public static TouchInputId code2TouchInputId(int code) {
        switch(code) {
            case TouchInputId.joystickInput_VALUE: return TouchInputId.joystickInput;
            case TouchInputId.button1Input_VALUE: return TouchInputId.button1Input;
            case TouchInputId.button2Input_VALUE: return TouchInputId.button2Input;
            case TouchInputId.tapInput_VALUE: return TouchInputId.tapInput;                                    
            default: return null;
        }
    }

    public static TouchMotion code2TouchMotion(int code) {
        switch(code) {
            case TouchMotion.NONE_VALUE: return TouchMotion.NONE;
            case TouchMotion.DOWN_VALUE: return TouchMotion.DOWN;
            case TouchMotion.MOVE_VALUE: return TouchMotion.MOVE;
            case TouchMotion.UP_VALUE: return TouchMotion.UP;
            case TouchMotion.DOWN_UP_VALUE: return TouchMotion.DOWN_UP;
            case TouchMotion.ALL_VALUE: return TouchMotion.ALL;
            default: return null;
        }
    }


    public static String touchInputId2String(TouchInputId code) {
        switch(code) {
            case joystickInput: return "TouchInputId.joystickInput";
            case button1Input: return "TouchInputId.button1Input";
            case button2Input: return "TouchInputId.button2Input";
            case tapInput: return "TouchInputId.tapInput";
            default: return null;                                 
        }

    }

    public static String touchMotion2String(TouchMotion code) {
        switch(code) {
            case NONE: return "TouchMotion.NONE";
            case DOWN: return "TouchMotion.DOWN";
            case MOVE: return "TouchMotion.MOVE";
            case UP: return "TouchMotion.UP";
            case DOWN_UP: return "TouchMotion.DOWN_UP";
            case ALL: return "TouchMotion.ALL";
            default: return null;
        }
    }

    TapInfo(NEvent e) {
        inputId = code2TouchInputId(e.getInts(1));
        motion = code2TouchMotion(e.getInts(2));
        x = e.getFloats(0);
        y = -e.getFloats(1);
    }

    public String toString() {
        return String.format("TapInfo inputId:%s motion:%s (%f,%f)", touchInputId2String(inputId), touchMotion2String(motion), x, y);
    }    
}
