package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.*;
import com.gameoff.game.GameLevel;
import com.gameoff.game.Room;

public class HudMap extends GameObject {

  GameLevel m_level = null;
  int m_roomSize = 15;

  public HudMap(GameLevel level) {
    m_level = level;
  }

  public void updateLevel(GameLevel level)
  {
    m_level = level;
    int ms = m_roomSize;
    Room currRoom = m_level.getCurrentRoom();
    clearChildren();
    Room[][] rooms = m_level.getRooms();
    for (int x = 0; x < m_level.getWidth(); x++)
    {
      for (int y=0; y < m_level.getHeight(); y++)
      {
        Room r = rooms[y][x];
        int rc = r.getCode();
        BasicGameObject g = new BasicGameObject();
        g.setPosition(x*ms,m_level.getHeight()*ms - y*ms);
        g.setSprite("map_empty");
        if (rc >= 0)
        { 
          g.setSprite("map_room");
        }

        g.setSize(ms,ms);
        addChild(g);

        if (currRoom == r)
        {
          BasicGameObject g2 = new BasicGameObject();
          g2.setPosition(x*ms,m_level.getHeight()*ms - y*ms);
          g2.setSprite("map_room_current_icon");
          g2.setSize(ms,ms);
          addChild(g2);
        }

        if (rc == 0)
        {
          //start room
          BasicGameObject g2 = new BasicGameObject();
          g2.setPosition(x*ms,m_level.getHeight()*ms - y*ms);
          g2.setSprite("map_start_icon");
          g2.setSize(ms,ms);
          addChild(g2);
        }

        //add doors
        for(int i = 0; i < 4; i++)
        {
          if (r.getDoor(i) > 0)
          {
            BasicGameObject g2 = new BasicGameObject();
            g2.setPosition(x*ms,m_level.getHeight()*ms - y*ms);
            g2.setSprite("map_door"+i);
            g2.setSize(ms,ms);
            addChild(g2);
          }
        }
      }
    }
  }

  public void setRecommendedPosition(int w, int h)
  {
    int rx = w - (m_level.getWidth() * m_roomSize + 3);
    int ry = h - (m_level.getHeight() * m_roomSize + m_roomSize + 1); 
    setPosition(rx,ry);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
  }
}
