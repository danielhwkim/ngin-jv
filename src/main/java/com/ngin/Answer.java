package com.ngin;

import java.util.Arrays;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

public class Answer {
    public void run() {
        try
        {
            Nc nc = new Nc();
            for (int j=0; j<12; j++) {
                for (int i=0; i<12; i++) {
                    if (i<j) {
                        nc.relay(Arrays.asList("input"), Arrays.asList(i, j), Floats.asList());
                    }
                }
            }
            nc.relay(Arrays.asList("submit"), Ints.asList(), Floats.asList());
        }
        catch( Exception e )    
        {
            e.printStackTrace();
        }        
    }
}
