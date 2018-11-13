package com.gameoff.game;
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
    if (y < m_height)
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

  public int getWidth()
  {
    return m_width;
  }

  public int getHeight()
  {
    return m_height;
  }

  public Room getCurrentRoom()
  {
    return m_currentRoom;
  }

  public Room[][] getRooms()
  {
    return m_rooms;
  }

  public Room moveRoom(int deltaX, int deltaY)
  {
    m_currentRoom = m_rooms[m_currentRoom.m_Y + deltaY][m_currentRoom.m_X+deltaX];
    return m_currentRoom;
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
        int nc = getNeighborCount(r);
        if ((nc > 0) && (nc <= numNeighbors))
          return r;
      }
    }
  }

  public Room getNeighborRoom(Room r, int dir)
  {
    if (dir == 0)
      return getNeighborRoom(r,0,-1);
    else if (dir == 1)
      return getNeighborRoom(r,1,0);
    else if (dir == 2)
      return getNeighborRoom(r,0,1);

    return getNeighborRoom(r,-1,0);
  }

  //only returns valid rooms (not -1 room codes)
  private Room getNeighborRoom(Room r, int deltaX, int deltaY)
  {
    int x = r.m_X + deltaX;
    int y = r.m_Y + deltaY;
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

  private void prune(int lx, int rx, int ty, int by)
  {

    int newWidth = rx - lx + 1;
    int newHeight = by-ty + 1;

    Room[][] newRooms = new Room[newHeight][newWidth];

    int newY = 0;
    for (int h = ty; h <= by; h++) 
    {
      int newX = 0;
      for (int w = lx; w <= rx; w++) 
      {
        newRooms[newY][newX] = m_rooms[h][w];
        newRooms[newY][newX].m_X = newX;
        newRooms[newY][newX].m_Y = newY;
        newX++;
      }
      newY++;
    }

    m_rooms = newRooms;
    m_width = newWidth;
    m_height = newHeight;

  }

  public void updateShowMapDetails()
  {
    //show details on all rooms that neightbor this one
    m_currentRoom.setVisited(true);
    m_currentRoom.setShowMapDetails();
    Room r = getNeighborRoom(m_currentRoom,-1,0);
    if (r != null) r.setShowMapDetails();
    r = getNeighborRoom(m_currentRoom,1,0);
    if (r != null) r.setShowMapDetails();
    r = getNeighborRoom(m_currentRoom,0,1);
    if (r != null) r.setShowMapDetails();
    r = getNeighborRoom(m_currentRoom,0,-1);
    if (r != null) r.setShowMapDetails();
  }

  public int getMapShowDir(Room r, int dir)
  {
    if (r.showMapDetails() == false) return 0;

    int dc = r.getDoor(dir);
    if (dc < 1) return dc;

    //if visited show all dirs
    if (r.getVisited()) return dc;

    //otherwise get room this door opens to and see if it should show details
    if (dir == 0)
    {
      Room n = getNeighborRoom(r,dir);
      if (n.showMapDetails()) return dc;
    }

    return 0;
    
  }

  public static GameLevel generateLevel(int difficulty, int numRooms, int width, int height)
  {
    GameLevel level = new GameLevel(width, height, 50000);
    numRooms += level.nextInt(5);
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
      int maxNeighbors = 1;
      if (level.nextInt(100) > 85)
      {
        maxNeighbors = 2;
      }
      r = level.getEmptyRoomWithNeighbors(maxNeighbors);
      r.m_roomCode = 1; //todo - should be random here - this is the room template
    }

    //now have rooms in array

    // now iterate over all rooms and update doors
    int lx = 100;
    int rx = -1;
    int ty = -1;
    int by = -1;
    int roomCount = 0;

    for (int h = 0; h < height; h++) 
    {
      for (int w = 0; w < width; w++) 
      {
        Room room = level.m_rooms[h][w];
        if (room.isEmpty() == false)
        {
          //update bounds
          if (w < lx) lx = w;
          if (rx < w) rx = w;
          if (ty < 0) ty = h;
          if (by < h) by = h;
          level.updateAllDoors(room,1); //1 is open doors, 2 is closed doors, 3 = locked
          roomCount++;
        }
      }
    }

    //now prune the array
    level.prune(lx,rx,ty,by);
    height = level.getHeight();
    width = level.getWidth();

    // now place boss
    int tryCnt = 0;
    Room bossRoom = null;

    while (true)
    {
      bossRoom = level.getRandomRoom();
      if (bossRoom.getCode() > 0)
      {
        //if only 1 neighbor, accept it
        int n = level.getNeighborCount(bossRoom);
        if (n == 1)
        {
          break;
        } else if (tryCnt > 100) {
          break;
        }

      }
      tryCnt++;
    }

    bossRoom.setIsBoss();
    //TODO:
    //We may decide only a few room templates make sense for the boss battle
    //if so, change the room code here to one that's valid for a boss.

    //set up map details to show
    level.updateShowMapDetails();

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