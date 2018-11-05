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


  public GameLevel(int width, int height, long seed)
  {
    m_random = new Random(seed);
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

  public Room getRandomRoom()
  {
    int x = nextInt(m_width);
    int y = nextInt(m_height);
    return m_rooms[y][x];
  }

  public Room getEmptyRoomWithNeighbors(int numNeighbors)
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

    for (int rc = 0; rc < numRooms; rc++)
    {
      r = level.getEmptyRoomWithNeighbors(1);
      r.m_roomCode = 1; //todo - should be random here
    }

    //now have rooms in array

    // now iterate over all rooms and update doors

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


  //Inner Class Room
  public class Room {
    int m_roomCode = -1;
    int m_X = 0;
    int m_Y = 0;
    int[] doors = new int[]{0,0,0,0};

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
  }
}