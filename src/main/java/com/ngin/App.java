package com.ngin;
public class App 
{



    public static void main( String[] args )
    {

        Stack.haha();

        Stack s = new Stack(5);
        s.add(5);
        //s.index = 4;
        System.out.println(s.remove());

        new Match3().run();



    }
}
