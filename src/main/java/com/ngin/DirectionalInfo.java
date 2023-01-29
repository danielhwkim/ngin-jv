package com.ngin;

import commander.Command.NEvent;

public class DirectionalInfo {
    int input;
    int directional;
    float intensity;
    float angle;

    DirectionalInfo(NEvent c) {
        input = c.getInts(1);
        directional = c.getInts(2);
        intensity = c.getFloats(0);
        angle = c.getFloats(1);
    }

    @Override
    public String toString() {
        return String.format("DirectionalInfo input:%d directional:%d intensity:%f angle:%f", input, directional, intensity, angle);
    }
}
