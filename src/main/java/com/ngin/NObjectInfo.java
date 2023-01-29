package com.ngin;

import commander.Command.*;

public class NObjectInfo {
    float x;
    float y;
    float width;
    float height;
    float angle;
    float linearx;
    float lineary;
    float angular;

    NObjectInfo(NEvent event) {
        x = event.getFloats(0);
        y = event.getFloats(1);
        width = event.getFloats(2);
        height = event.getFloats(3);
        angle = event.getFloats(4);
        linearx = event.getFloats(5);
        lineary = event.getFloats(6);
        angular = event.getFloats(7);           
    }

    public String toString() {
        return String.format("x:%f y:%f width:%f height:%f angle:%f linear(%f, %f) angular:%f", x, y, width, height, angle, linearx, lineary, angular);  
    }    
}
