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
  public int roomWidthPixels, roomHeightPixels;
  public int numberEnemies = 0;

  //means a key will be place in this room
  public boolean m_hasKey = false;

  //means upon first entry a trap will be randomly
  //selected and placed in this room
  public boolean m_hasTrap = false;

  //means 1 or more special items will be placed in this
  //these special items will be higher value
  public boolean m_hasSpecial = false;

  //will add some persistance classes
  //to store what gets generated on room entry
  //and persist state upon re-entry

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

  public void setHasKey()
  {
    m_hasKey = true;
  }

  public boolean getHasKey()
  {
    return m_hasKey;
  }

  public void setHasSpecial()
  {
    m_hasSpecial = true;
  }

  public boolean getHasSpecial()
  {
    return m_hasSpecial;
  }

}
