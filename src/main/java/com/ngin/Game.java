package com.ngin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.*;

import commander.Command.BodyShape;
import commander.Command.JoystickDirectionals;
import commander.Command.NClip;
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
            Ngin nx = new Ngin();
            File file = new File("./data/planes0.tmj");
            Scanner scan = new Scanner(file);
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            JSONObject obj = new JSONObject(str);

            JSONObject tileslayer = obj.getJSONArray("layers").getJSONObject(0);
            JSONObject objlayer = obj.getJSONArray("layers").getJSONObject(1);
            JSONArray data = tileslayer.getJSONArray("data");
            Double tileWidth = obj.getDouble("tilewidth");
            Double tileHeight = obj.getDouble("tileheight");
            nx.precision = 3;

            int width = obj.getInt("width");
            int height = obj.getInt("height");            

            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setBackground("Blue");
            builder.setGravityX(0);
            builder.setGravityY(60);
            builder.setJoystickDirectionals(JoystickDirectionals.horizontal);
            nx.sendStageWait(builder);

            
            List<Integer> tileList = new ArrayList<>();
            for(int i=0; i<data.length(); i++) {
                tileList.add(data.getInt(i));
            }
            nx.sendObjWait(nx.tilesBuilder("kenney_pixelshmup/tiles_packed.png", tileWidth.floatValue(), tileHeight.floatValue(), width, height, tileList));
            JSONArray objs = objlayer.getJSONArray("objects");

            for (int i=0; i<objs.length(); i++) {
                JSONObject o = objs.getJSONObject(i);
                int id = o.getInt("id") + 100;
                o.put("id", id);
                convInfo(o, tileWidth);
                double x = o.getDouble("x");
                double y = o.getDouble("y");
                String name = o.getString("name"); 
                double w = o.getDouble("width");
                double h = o.getDouble("height");                                               
            }
            /*
            NObject.Builder hero = nx.objBuilder(100, "hero");
            NBody.Builder heroBody = nx.bodyBuilder(BodyShape.circle, 11, 11);
            //heroBody.setAngle((float)1.5);
            heroBody.setWidth(2);
            heroBody.setHeight(2);
            NClip.Builder[] heroAs = new NClip.Builder[1];
            List<Integer> heroTileList = new ArrayList<>();
            heroTileList.add(1);
            heroAs[0] = nx.clipBuilder("kenney_pixelshmup/ships_packed.png", 32, 32, heroTileList);
            NVisual.Builder heroV = nx.visualBuilder(heroAs);
            heroV.setWidth(2);
            heroV.setHeight(2);
            hero.setBody(heroBody);
            hero.setVisual(heroV);
            nx.sendObjWait(hero);

            nx.follow(100);
            nx.forward(100, 0, 5);

            NObject.Builder enemy = nx.objBuilder(200, "enemy");
            NBody.Builder enemyBody = nx.bodyBuilder(BodyShape.circle, 11, 0);
            enemyBody.setWidth(2);
            enemyBody.setHeight(2);
            NClip.Builder[] enemyAs = new NClip.Builder[1];
            List<Integer> enemyTileList = new ArrayList<>();
            enemyTileList.add(10);                
            enemyAs[0] = nx.clipBuilder("kenney_pixelshmup/ships_packed.png", 32, 32, enemyTileList);
            NVisual.Builder enemyV = nx.visualBuilder(enemyAs);
            enemyV.setWidth(2);
            enemyV.setHeight(2);
            enemy.setBody(enemyBody);
            enemy.setVisual(enemyV);
            nx.sendObjWait(enemy);                  

            nx.forward(200, 0, 5);
            nx.angular(200, 1);*/

            nx.mainLoop(new GameEventHandler(nx));
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }    
}
