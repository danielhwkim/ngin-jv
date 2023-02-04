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

public class Plane {
    public void run() {
        try
        {
            Nx nx = new Nx();
            File file = new File("./data/planes0.tmj");
            Scanner scan = new Scanner(file);
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            JSONObject obj = new JSONObject(str);
            Double tileWidth = obj.getDouble("tilewidth");
            Double tileHeight = obj.getDouble("tileheight");            
            JSONObject layer = obj.getJSONArray("layers").getJSONObject(0);
            //System.out.println(layer);
            JSONArray data = layer.getJSONArray("data");
            int width = obj.getInt("width");
            int height = obj.getInt("height");            

            //System.out.println(String.format("tileSize:%d",tileSize));
            NStageInfo.Builder builder = nx.stageBuilder(width, height);
            builder.setJoystickDirectionals(JoystickDirectionals.horizontal);
            //builder.setDebug(false);
            nx.sendStageWait(builder);
            
            List<Integer> tileList = new ArrayList<>();
            for(int i=0; i<data.length(); i++) {
                tileList.add(data.getInt(i));
            }
            nx.sendObjWait(nx.tilesBuilder("kenney_pixelshmup/tiles_packed.png", tileWidth.floatValue(), tileHeight.floatValue(), width, height, tileList));

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
            nx.angular(200, 1);

            nx.mainLoop(new PlaneEventHandler(nx));
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
