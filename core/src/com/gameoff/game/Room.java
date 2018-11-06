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

  public Room(int x, int y)
  {
    m_X = x;
    m_Y = y;
  }

  public boolean isEmpty()
  {
    if (m_roomCode < 0) return true;
    return false;
  }

  public int getDoor(int dir)
  {
    return m_doors[dir];
  }

  public void setDoor(int dir, int doorCode)
  {
    m_doors[dir] = doorCode;
  }
}
