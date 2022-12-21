package com.ngin;

import commander.Command.*;

public class CObjectInfo {
    float x;
    float y;
    float width;
    float height;
    float angle;
    float linearx;
    float lineary;
    float angular;

    CObjectInfo(Cmd cmd) {
        x = cmd.getFloats(0);
        y = cmd.getFloats(1);
        width = cmd.getFloats(2);
        height = cmd.getFloats(3);
        angle = cmd.getFloats(4);
        linearx = cmd.getFloats(5);
        lineary = cmd.getFloats(6);
        angular = cmd.getFloats(7);           
    }
}
