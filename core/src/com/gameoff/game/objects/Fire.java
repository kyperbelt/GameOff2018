package com.gameoff.game.objects;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gameoff.game.GameLevel;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.KyperSprite;

public class Fire extends Basic {
	
	// pool stuff
	private static Pool<Fire> firepool = new Pool<Fire>(100) {
		protected Fire newObject() {
			return new Fire();
		}; 
	};
	
	private static Array<Fire> used = new Array<Fire>();
	
	public static Fire get() {
		return get(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
	}
	
	public static Fire get(HealthGroup...groups) {
		Fire f = firepool.obtain();
		f.op = getOp();
		f.damageGroup = groups;
		used.add(f);
		return f;
	}
	
	/**
	 * should call this once in levelmanager dispose
	 */
	public static void freeAll() {
		firepool.freeAll(used);
		used.clear();
	}
	//pool stuff -----

	
	// --op

		static int OP = 0;
		static int LAST = 0;

		public static void updateOp() {
			ld = Gdx.graphics.getDeltaTime();
			OP = OP == 0 ? 1 : 0;
		}

		private static int getOp() {
			LAST = LAST == 0 ? 1 : 0;
			return LAST;
		}
		
		static float ld = 0;
		
		int op = 0;
		// ----
	
  AttackControl attack;
  Array<CollisionData> cols;
  static int masterID = 0;
  int m_id = 1;
  Random m_random = new Random();

  float m_lifeTime = 9999999;
  float m_maxLife = 999999;
  boolean m_spread = false;
  float m_spawnTime = 2.0f;
  float m_spawnSpeed = 2.0f;
  int m_spawnStyle = 1;  //0 is slower and random, 1 is spreads out fast, 2 - direction
  int m_directionsBitFlags = 0;
  boolean m_randomPositioning = true;
  float m_decay = 0.1f;
  int m_maxSpawn = 8;
  int m_originalMaxSpawn = 8;

  AttackListener hazardListener = new AttackListener() {
    @Override
    public void onAttack() {
      for (int i = 0; i < cols.size; i++) {
        GameObject target = cols.get(i).getTarget();
        HealthControl health = target.getController(HealthControl.class);
        if(health!=null) {
          for (int j = 0; j < damageGroup.length; j++) {
            if(damageGroup[j] == health.getHealthGroup()) {
              health.changeCurrentHealth(-attack.getDamage()*attack.getDamageMult());
            }
          }
        }
      }
    }
  };
  
  HealthGroup[] damageGroup;

  public Fire(HealthGroup... damageGroup) {
    this.damageGroup = damageGroup;
    getMove().setPhysical(false);
    attack = new AttackControl(1f,1f,hazardListener);
    getZOrder().setZOrder(ZOrder.PLAYER);
    setYOffset(0);
    masterID++;
    m_id = masterID;
  }
  
  public Fire() {
    this(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
    setSize(64,88);
    setBounds(5,15,getWidth()-10,10);
    setSpread(false);
  }

  public void setMaxSpawn(int max)
  {
    m_maxSpawn = max;
    m_originalMaxSpawn = max;
  }

  public void setSpread(boolean spread)
  {
    m_spread = spread;
  }

  public void setRandomPositioning(boolean rp)
  {
    m_randomPositioning = rp;
  }

  public void setLife(float tm)
  {
    m_lifeTime = tm;
    m_maxLife = tm;
  }

  public void setSpawnSpeed(float sp)
  {
    m_spawnSpeed = sp;
    m_spawnTime = m_spawnSpeed + m_random.nextFloat()*m_spawnSpeed;
  }

  public void setSpawnStyle(int style)
  {
    m_spawnStyle = style;
  }

  public void setDecay(float decay)
  {
    m_decay = decay;
  }

  //bit 0 = up, 1 = right, 2 = down, 3 = left
  //
  public void setDirectionBitFlags(int flags)
  {
    m_directionsBitFlags = flags;
  }

  public void setDirectionBitFlags(boolean up, boolean right, boolean down, boolean left)
  {
    m_directionsBitFlags = 0;
    if (up) m_directionsBitFlags += 1;
    if (right) m_directionsBitFlags += 2;
    if (down) m_directionsBitFlags += 4;
    if (left) m_directionsBitFlags += 8;
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    createAnimations();

    // Fire does not have health so we remove the health control so that systems
    // that interact with the health control do not affect it
    removeController(getHealth());
    removeController(getMove());
    
    addController(attack);
    setSprite("fire_0");
    getAnimation().set("fire", PlayMode.LOOP);
    getAnimation().setPlaySpeed(1.0f + m_random.nextFloat());
    setBounds(5,15,getWidth()-10,10);

    if (properties != null)
    {
      float sl = properties.get("spawnLife", 4f, Float.class);
      float sp = properties.get("spawnSpeed", 0.15f, Float.class);
      m_directionsBitFlags = properties.get("spawnDir", 0, Integer.class);
      if (m_directionsBitFlags > 0)
      {
        setLife(sl);
        setSpread(true);
        setSpawnStyle(2);
        setSpawnSpeed(sp);
        setMaxSpawn(1);
        setDecay(0);
        setRandomPositioning(false);
      }

    }
    
  }

  public void spawnFire(int dir)
  {
    //0 to 3 for now
    Fire f = Fire.get();

    f.setLife(m_maxLife-m_decay);
    f.setSpread(true);
    f.setDecay(m_decay);
    f.setMaxSpawn(m_originalMaxSpawn);
    f.setDirectionBitFlags(m_directionsBitFlags);
    f.setSpawnStyle(m_spawnStyle);
    f.setRandomPositioning(m_randomPositioning);
    f.setSpawnSpeed(m_spawnSpeed);

    float f1 = m_random.nextFloat()*20f;
    float f2 = m_random.nextFloat()*20f;

    if (m_randomPositioning == false)
      f1 = 10;
    

    if (dir == 2)
    {
      f.setPosition(getX() - 10 + f1, getY() - 50 - f2);
    } else if (dir == 0)
    {
      f.setPosition(getX() - 10 + f1, getY() + 50 + f2);
    } else if (dir == 1)
    {
      f.setPosition(getX() + 64 + f2, getY() - 10 + f1);
    } else
    {
      f.setPosition(getX() - 64 - f2, getY() - 10 + f1);
    }

    getGameLayer().addGameObject(f,null);
    f.setBounds(5,5,f.getWidth()-10,54);
    f.getCollision().getCollisions(f,0.005f);
    f.getCollision().getCollisions(f,0.005f);
    removeFireIfNotValid(f);
    f.setBounds(5,15,f.getWidth()-10,10);
    
  }

  private void removeFireIfNotValid(Fire f)
  {
    GameLevel level = GameLevel.getCurrentLevel();
    if ((f.getX() < 200) || (f.getX() > (level.getRoomWidth() - 270)) || (f.getY() < 210) || (f.getY() > (level.getRoomHeight()-224)))
    {
      f.remove();
      return;
    }

    Array<CollisionData> c = f.getCollision().getCollisions();
    for (int i = 0; i < c.size; i++) {
			CollisionData data = c.get(i);
			GameObject target = data.getTarget();
      if (!(target instanceof Player))
      {
        if (target instanceof Fire)
        {
          f.remove();
          return;
        } else if (target instanceof Basic) {
          Basic b = (Basic) target;
          if (b.getMove() != null)
          {
            if (b.getMove().isPhysical())
            {
              f.remove();
            }
          }
        }
      }
    }
  }
  
  @Override
  public void update(float delta) {
    super.update(delta);
    
    if(op != OP)
		return;
    delta+=ld;
    
    cols = getCollision().getCollisions();
    if(cols.size > 0) {
        attack.attack();
    }
    
    if (m_spread)
    {
      m_lifeTime -= delta;
      m_spawnTime -= delta;
      if (m_lifeTime < 0)
      {
        remove();
      } else
      {
        if (m_maxSpawn > 0)
        {     
          if (m_spawnTime < 0)
          {
            m_maxSpawn--;
            m_spawnTime = m_spawnSpeed + m_random.nextFloat()*m_spawnSpeed;
            if (m_spawnStyle == 0)
              spawnFire(m_random.nextInt(4));
            else if (m_spawnStyle == 1)
            {
              for (int dd = 0; dd < 4; dd++)
                spawnFire(dd);
            } else if (m_spawnStyle == 2)
            {
              if ((m_directionsBitFlags & 1) == 1) spawnFire(0);
              if ((m_directionsBitFlags & 2) == 2) spawnFire(1);
              if ((m_directionsBitFlags & 4) == 4) spawnFire(2);
              if ((m_directionsBitFlags & 8) == 8) spawnFire(3);
            }
          }
        }
      }
    }
    cols = null; //null out because col data is pooled
  }

  @Override
  public void onRemove() {
    super.onRemove();
    removeController(attack);
    used.removeValue(this, true);
    firepool.free(this);
  }

  private void createAnimations() {
    
    String fireAnimationName = "fire";
    Animation<KyperSprite> spikeA = getState().getAnimation(fireAnimationName);
    if (spikeA == null) {
      getState().storeAnimation(fireAnimationName, getState().createGameAnimation("fire", .1f));
    }
    getAnimation().addAnimation("fire", fireAnimationName);
  }


}
