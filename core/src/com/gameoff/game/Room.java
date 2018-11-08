package com.gameoff.game;
/*
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Wall;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.IGameObjectGetter;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
*/

public class Room {
  int m_roomCode = -1;
  int m_X = 0;
  int m_Y = 0;
  int[] m_doors = new int[]{0,0,0,0};
  boolean m_visited = false;
  boolean m_isBoss = false;
  boolean m_showMapDetails = false;

  public Room(int x, int y)
  {
    m_X = x;
    m_Y = y;
  }
  
  public int getX() {
	  return m_X;
  }
  
  public int getY() {
	  return m_Y;
  }

  public boolean isEmpty()
  {
    if (m_roomCode < 0) return true;
    return false;
  }

  public int getCode()
  {
    return m_roomCode;
  }

  public int getDoor(int dir)
  {
    return m_doors[dir];
  }

  public void setDoor(int dir, int doorCode)
  {
    m_doors[dir] = doorCode;
  }

  public boolean showMapDetails()
  {
    return m_showMapDetails;
  }

  public void setShowMapDetails()
  {
    m_showMapDetails = true;
  }

  public boolean getVisited()
  {
    return m_visited;
  }

  public void setVisited(boolean v)
  {
    m_visited = v;
  }

  public boolean getIsBoss()
  {
    return m_isBoss;
  }

  public void setIsBoss()
  {
    m_isBoss = true;
  }
}
