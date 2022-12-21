package com.ngin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.*;

import commander.Command.BodyShape;
import commander.Command.CAction;
import commander.Command.CObject;
import commander.Command.CPhysical;
import commander.Command.CVisible;

/**
 * Hello world!
 *
 */
public class App 
{

    static Ngin nx;
    public static void main( String[] args )
    {
        try
        {
            nx = new Ngin();
            nx.setEventHandler(new MyEventHandler(nx));
            File file = new File("./data/planes0.tmj");
            Scanner scan = new Scanner(file);
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            JSONObject obj = new JSONObject(str);
            int tileSize = obj.getInt("tilewidth");
            
            JSONObject layer = obj.getJSONArray("layers").getJSONObject(0);
            //System.out.println(layer);
            JSONArray data = layer.getJSONArray("data");
            int width = obj.getInt("width");
            int height = obj.getInt("height");            

            //System.out.println(String.format("tileSize:%d",tileSize));

            nx.sendStage(nx.stageBuilder(width, height));
            nx.receiveAck();            
            List<Integer> tileList = new ArrayList<>();
            for(int i=0; i<data.length(); i++) {
                tileList.add(data.getInt(i));
            }
            nx.sendCObj(nx.tilesBuilder("kenney_pixelshmup/tiles_packed.png", tileSize, width, height, tileList));

            CObject.Builder hero = nx.objBuilder(100, "hero");
            CPhysical.Builder heroP = nx.physicalBuilder(BodyShape.circle, 11, 11);
            heroP.setAngle((float)1.5);
            heroP.setWidth(2);
            heroP.setHeight(2);
            CAction.Builder[] heroAs = new CAction.Builder[1];
            List<Integer> heroTileList = new ArrayList<>();
            heroTileList.add(1);
            heroAs[0] = nx.actionBuilder("kenney_pixelshmup/ships_packed.png", 32, heroTileList);
            CVisible.Builder heroV = nx.visibleBuilder(heroAs);
            heroV.setWidth(2);
            heroV.setHeight(2);
            hero.setPhysical(heroP);
            hero.setVisible(heroV);
            nx.sendCObj(hero);
            nx.receiveAck();

            nx.follow(100);
            nx.forward(100, 0, 5);

            CObject.Builder enemy = nx.objBuilder(200, "enemy");
            CPhysical.Builder enemyP = nx.physicalBuilder(BodyShape.circle, 11, 0);
            enemyP.setWidth(2);
            enemyP.setHeight(2);
            CAction.Builder[] enemyAs = new CAction.Builder[1];
            List<Integer> enemyTileList = new ArrayList<>();
            enemyTileList.add(10);
            //enemyTileList.add(11);
            //enemyTileList.add(12);                        
            enemyAs[0] = nx.actionBuilder("kenney_pixelshmup/ships_packed.png", 32, enemyTileList);
            CVisible.Builder enemyV = nx.visibleBuilder(enemyAs);
            enemyV.setWidth(2);
            enemyV.setHeight(2);
            enemy.setPhysical(enemyP);
            enemy.setVisible(enemyV);
            nx.sendCObj(enemy); 
            nx.receiveAck();                       

            nx.forward(200, 0, 5);
            nx.angular(200, 1);

            nx.mainLoop();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }        
    }
}
