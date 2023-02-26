package com.ngin;

import java.util.Arrays;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

public class AnswerPigs {
    public void run() {
        try
        {
            Nc nc = new Nc();

            for (int s=0; s<12; s++) {
                nc.pick();
                for (int i=0; i<12-s; i++) {
                    nc.moveRight();
                    if (nc.compare() > 0) {
                        nc.pick();
                    }
                }
                for (int i=0; i<12-s; i++) {
                    nc.moveLeft();
                }  
                nc.pick();
                nc.moveRight();              
            }
            if (nc.submit() == 1) {
                System.out.println("SUCCESS");
            } else {
                System.out.println("FAILURE");
            }
        }
        catch( Exception e )    
        {
            e.printStackTrace();
        }        
    }    
}
