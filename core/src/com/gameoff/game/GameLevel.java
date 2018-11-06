package com.gameoff.game;
/*
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Wall;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.IGameObjectGetter;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
*/

import java.util.Random;

/**
 * The main class with logic for dynamically creating
 * maps.
 */
public class GameLevel {

  public Room[][] m_rooms;
  private Random m_random;
  private long m_seed;
  private int m_width;
  private int m_height;
  private static GameLevel m_currentLevel = null;
  private Room m_currentRoom;


  public GameLevel(int width, int height, long seed)
  {
    m_random = new Random(); //ignoring seed right now, random seed!
    m_seed = seed;
    m_rooms = new Room[height][width];
    m_width = width;
    m_height = height;

    for (int w = 0; w < width; w++) 
    {
      for (int h = 0; h < height; h++) 
      {
        m_rooms[h][w] = new Room(w,h);
      }
    }
  }

  public int nextInt(int bounds)
  {
    return m_random.nextInt(bounds);
  }

  public int getNeighborCount(Room r)
  {
    int x = r.m_X;
    int y = r.m_Y;
    int cnt = 0;

    x--;
    if (x >= 0)
    {
      if (m_rooms[y][x].isEmpty() == false)
        cnt++;
    }
    x += 2;
    if (x < m_width)
    {
      if (m_rooms[y][x].isEmpty() == false)
        cnt++;
    }
    x = r.m_X;
    y--;
    if (y >= 0)
    {
      if (m_rooms[y][x].isEmpty() == false)
        cnt++;
    }
    y += 2;
    if (y < m_width)
    {
      if (m_rooms[y][x].isEmpty() == false)
        cnt++;
    }
    return cnt;
  }

  public Room getRoom(int x, int y)
  {
    return m_rooms[x][y];
  }

  public Room getCurrentRoom()
  {
    return m_currentRoom;
  }

  public void moveRoom(int deltaX, int deltaY)
  {
    m_currentRoom = m_rooms[m_currentRoom.m_Y + deltaY][m_currentRoom.m_X+deltaX];
  }

  private Room getRandomRoom()
  {
    int x = nextInt(m_width);
    int y = nextInt(m_height);
    return m_rooms[y][x];
  }

  private Room getEmptyRoomWithNeighbors(int numNeighbors)
  {
    while (true)
    {
      Room r = getRandomRoom();
      if (r.isEmpty())
      {
        if (getNeighborCount(r) == 1)
          return r;
      }
    }
  }

  //only returns valid rooms (not -1 room codes)
  private Room getNeighborRoom(Room r, int deltaX, int deltaY)
  {
    int x = r.m_X += deltaX;
    int y = r.m_Y += deltaY;
    if ((x >= m_width) || (x < 0) || (y < 0) || (y >= m_height)) return null;
    Room n = m_rooms[y][x];
    if (n.m_roomCode >= 0) return n;
    return null;
  }

  private void updateAllDoors(Room r, int doorCode)
  {
    if (getNeighborRoom(r,0,-1) != null) r.setDoor(0, doorCode);
    if (getNeighborRoom(r,1,0) != null) r.setDoor(1, doorCode);
    if (getNeighborRoom(r,0,1) != null) r.setDoor(2, doorCode);
    if (getNeighborRoom(r,-1,0) != null) r.setDoor(3, doorCode);
  }

  private void updateDoor(Room r, int dir, int doorCode)
  {
    r.setDoor(dir, doorCode);
  }

  public static GameLevel generateLevel(int difficulty, int numRooms, int width, int height)
  {
    GameLevel level = new GameLevel(width, height, 50000);

    //ROOM Template considerations
    //Maybe use map properties to define:
    //what trap types are compatible
    //and possible trap locations for location traps vs. room traps

    //Pick start room
    Room r = level.getRandomRoom();
    r.m_roomCode = 0; //0 is always start
    numRooms--;
    level.m_currentRoom = r;

    for (int rc = 0; rc < numRooms; rc++)
    {
      r = level.getEmptyRoomWithNeighbors(1);
      r.m_roomCode = 1; //todo - should be random here - this is the room template
    }

    //now have rooms in array

    // now iterate over all rooms and update doors
    for (int h = 0; h < height; h++) 
    {
      for (int w = 0; w < width; w++) 
      {
        Room room = level.m_rooms[h][w];
        if (room.m_roomCode >= 0)
        {
          level.updateAllDoors(room,1); //1 is open doors
        }
      }
    }

    // now place mini boss

    // now place boss


    //place locked doors and keys

    //identify rooms with special items

    //identify rooms with traps

    // NOW with all this, do we dynamically build a big TMX
    //OR there will be another chunk of code in LevelManager as it loads a room,
    // it does the rest.


    return level;
  }

  public static void setCurrentLevel(GameLevel l)
  {
    m_currentLevel = l;
  }

  public static GameLevel getCurrentLevel()
  {
    return m_currentLevel;
  }

}