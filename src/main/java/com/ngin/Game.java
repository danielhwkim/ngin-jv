package com.ngin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.*;

import commander.Command.BodyShape;
import commander.Command.BodyType;
import commander.Command.JoystickDirectionals;
import commander.Command.NClip;
import commander.Command.NClipType;
import commander.Command.NObject;
import commander.Command.NStageInfo;
import commander.Command.NBody;
import commander.Command.NVisual;

public class Game {
    public Double conv(Double v, Double tileSize) {
        return Math.round(v*2.0/tileSize)/2.0;
    }

    public JSONObject convInfo(JSONObject info, double tileSize) {
        info.put("x", conv(info.getDouble("x"), tileSize));
        info.put("y", conv(info.getDouble("y"), tileSize));
        info.put("width", conv(info.getDouble("width"), tileSize));
        info.put("height", conv(info.getDouble("height"), tileSize));
        return info;                        
    }

    public void run() {
        try
        {
            Nx nx = new Nx();
            File file = new File("./data/level03.json");
            Scanner scan = new Scanner(file);
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            JSONObject obj = new JSONObject(str);
            JSONArray layers = obj.getJSONArray("layers");
            JSONObject tileslayer = layers.getJSONObject(0);
            JSONObject objlayer = layers.getJSONObject(1);
            JSONArray data = tileslayer.getJSONArray("data");
            Double tileWidth = obj.getDouble("tilewidth");
            Double tileHeight = obj.getDouble("tileheight");
            nx.precision = 3;

            int width = obj.getInt("width");
            int height = obj.getInt("height");            

            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setBackground("Yellow");
            builder.setGravityX(0);
            builder.setGravityY(60);
            builder.setDebug(false);
            builder.setJoystickDirectionals(JoystickDirectionals.horizontal);
            nx.sendStageWait(builder);

            
            List<Integer> tileList = new ArrayList<>();
            for(int i=0; i<data.length(); i++) {
                tileList.add(data.getInt(i));
            }
            nx.sendObjWait(nx.tilesBuilder("Terrain/Terrain (16x16).png", tileWidth.floatValue(), tileHeight.floatValue(), width, height, tileList));
            JSONArray objs = objlayer.getJSONArray("objects");

            for (int i=0; i<objs.length(); i++) {
                JSONObject o = objs.getJSONObject(i);
                int id = o.getInt("id") + 100;
                o.put("id", id);
                convInfo(o, tileWidth);
                float x = (float) o.getDouble("x");
                float y = (float) o.getDouble("y");
                String name = o.getString("name"); 
                float w = (float) o.getDouble("width");
                float h = (float) o.getDouble("height");

                switch(name){
                    case "Apple":
                    case "Bananas":
                    case "Cherries":
                    case "Kiwi":
                    case "Orange":
                    case "Pineapple":
                    case "Strawberry":
                    {
                        NObject.Builder ob = nx.objBuilder(id, "fruit");
                        NBody.Builder bb = nx.bodyBuilder(BodyShape.circle, x, y);
                        bb.setType(BodyType.staticBody);
                        bb.setIsSensor(true);
                        NClip.Builder[] cbs = new NClip.Builder[2];
                        List<Integer> list = new ArrayList<>();              
                        cbs[0] = nx.clipBuilder("Items/Fruits/" + name + ".png", 32, 32, list, NClipType.loop, 0.05f);
                        cbs[1] = nx.clipBuilder("Items/Fruits/Collected.png", 32, 32, list, NClipType.once, 0.05f);
                        NVisual.Builder vb = nx.visualBuilder(cbs);
                        vb.setScaleX(2f);
                        vb.setScaleY(2f);
                        ob.setBody(bb);
                        ob.setVisual(vb);
                        nx.sendObj(ob);                        
                    }
                    break;
                    case "hero":
                    {
                        id = 100;
                        o.put("id", id);
                        w = 2;
                        h = 2;
                        String character = "Mask Dude";

                        NObject.Builder ob = nx.objBuilder(id, name);
                        NBody.Builder bb = nx.bodyBuilder(BodyShape.polygon, x, y);
                        bb.setWidth(w);
                        bb.setHeight(h);

                        float hx = w/4;
                        float hy = h/2;
                        float d = hx/4;
                        bb.addFloats(-hx);
                        bb.addFloats(-hy + d);
                        bb.addFloats(-hx + d);
                        bb.addFloats(-hy);

                        bb.addFloats(hx - d);
                        bb.addFloats(-hy);
                        bb.addFloats(hx);
                        bb.addFloats(-hy +d);

                        bb.addFloats(hx);
                        bb.addFloats(hy - d -d);
                        bb.addFloats(hx - d);
                        bb.addFloats(hy - d);

                        bb.addFloats(-hx +d);
                        bb.addFloats(hy - d);
                        bb.addFloats(-hx);
                        bb.addFloats(hy -d -d);

                        bb.setType(BodyType.dynamicBody);

                        NClip.Builder[] cbs = new NClip.Builder[7];
                        List<Integer> list = new ArrayList<>();              
                        cbs[0] = nx.clipBuilder("Main Characters/" + character +"/Idle (32x32).png", 32, 32, list, NClipType.loop, 0.05f);
                        cbs[1] = nx.clipBuilder("Main Characters/" + character +"/Run (32x32).png", 32, 32, list, NClipType.loop, 0.05f);
                        cbs[2] = nx.clipBuilder("Main Characters/" + character +"/Jump (32x32).png", 32, 32, list, NClipType.loop, 0.05f);
                        cbs[3] = nx.clipBuilder("Main Characters/" + character +"/Hit (32x32).png", 32, 32, list, NClipType.once, 0.05f);
                        cbs[4] = nx.clipBuilder("Main Characters/" + character +"/Fall (32x32).png", 32, 32, list, NClipType.loop, 0.05f);
                        cbs[5] = nx.clipBuilder("Main Characters/" + character +"/Wall Jump (32x32).png", 32, 32, list, NClipType.once, 0.05f);
                        cbs[6] = nx.clipBuilder("Main Characters/" + character +"/Double Jump (32x32).png", 32, 32, list, NClipType.once, 0.05f);                                                                                                                   
                        NVisual.Builder vb = nx.visualBuilder(cbs);
                        vb.setWidth(w);
                        vb.setHeight(h);
                        vb.setY(-0.13f);
                        ob.setBody(bb);
                        ob.setVisual(vb);
                        nx.sendObj(ob);
                    }
                    break;
                    case "floor":
                    {
                        NObject.Builder ob = nx.objBuilder(id, name);
                        NBody.Builder bb = nx.bodyBuilder(BodyShape.rectangle, x, y);
                        bb.setType(BodyType.staticBody);
                        bb.setWidth(w);
                        bb.setHeight(h);
                        ob.setBody(bb);
                        nx.sendObj(ob);    
                    }
                    break;
                    case "Trampoline":
                    {
                        NObject.Builder ob = nx.objBuilder(id, name);
                        NBody.Builder bb = nx.bodyBuilder(BodyShape.rectangle, x, y);
                        bb.setType(BodyType.staticBody);
                        bb.setIsSensor(true);
                        NClip.Builder[] cbs = new NClip.Builder[2];
                        List<Integer> list = new ArrayList<>();              
                        cbs[0] = nx.clipBuilder("Traps/Trampoline/Idle.png", 28, 28, list, NClipType.loop, 0.05f);
                        cbs[1] = nx.clipBuilder("Traps/Trampoline/Jump (28x28).png", 28, 28, list, NClipType.once, 0.05f);
                        NVisual.Builder vb = nx.visualBuilder(cbs);
                        vb.setWidth(1.7f);
                        vb.setHeight(1.7f);
                        vb.setY(-0.4f);
                        ob.setBody(bb);
                        ob.setVisual(vb);
                        nx.sendObj(ob);     
                    }
                    break;
                }
            }

            nx.eventLoop(new GameEventHandler(nx));
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }    
}
