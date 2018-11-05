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


  public GameLevel(int width, int height, long seed)
  {
    m_random = new Random(seed);
    m_seed = seed;
    m_rooms = new Room[height][width];

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


  public static GameLevel generateLevel(int difficulty, int numRooms, int width, int height)
  {
    GameLevel level = new GameLevel(width, height, 50000);

    //ROOM Template considerations
    //Maybe use map properties to define:
    //what trap types are compatible
    //and possible trap locations for location traps vs. room traps

    //Pick start room
    //Room r = level.getRandomRoom();

    //Reduce room count by 1

    //for remaining rooms loop and place room randomly, only keep room if borders exactly 1 room


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
  }
}