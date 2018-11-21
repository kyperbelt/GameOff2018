package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.kyperbox.controllers.AnimationController;
import com.gameoff.game.ZOrder;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kyperbox.umisc.KyperSprite;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Animation;
import java.util.Random;

public class Lava extends Basic {

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
  int m_spawnStyle = 1;  //0 is slower and random, 1 is spreads out fast, 2 - single direction
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

  public Lava(HealthGroup... damageGroup) {
    this.damageGroup = damageGroup;
    getMove().setPhysical(false);
    attack = new AttackControl(1f,1f,hazardListener);
    getZOrder().setZOrder(ZOrder.PLAYER+5);
    setYOffset(0);
    masterID++;
    m_id = masterID;
  }
  
  public Lava() {
    this(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
    setSize(64,64);
    setBounds(0,0,64,64);
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

    addController(attack);
    setSprite("lavatile_0");
    getAnimation().set("lava", PlayMode.LOOP_PINGPONG);
    getAnimation().setPlaySpeed(1.0f + m_random.nextFloat());
    setBounds(5,5,54,54);
    removeController(getMove());
  }

  public void spawnFire(int dir)
  {
    if ((m_maxLife - m_decay) < 1) return;

    //0 to 3 for now
    Lava f = new Lava();

    if (dir == 0)
    {
      f.setPosition(getX(), getY() + 64);
    } else if (dir == 2)
    {
      f.setPosition(getX(), getY() - 64);
    } else if (dir == 1)
    {
      f.setPosition(getX() + 64, getY());
    } else
    {
      f.setPosition(getX() - 64, getY());
    }

    getGameLayer().addGameObject(f,null);
    f.setLife(m_maxLife-m_decay);
    f.setSpawnSpeed(m_spawnSpeed);
    f.setSpread(true);
    f.setDecay(m_decay);
    f.setMaxSpawn(m_originalMaxSpawn);
    f.setDirectionBitFlags(m_directionsBitFlags);
    f.setSpawnStyle(m_spawnStyle);

    f.setBounds(5,5,54,54);
    f.getCollision().getCollisions(f,0.005f);
    f.getCollision().getCollisions(f,0.005f);
    removeFireIfNotValid(f);
    f.setVisible(true);
  }

  private void removeFireIfNotValid(Lava f)
  {
    if ((f.getX() < 200) || (f.getX() > 2610) || (f.getY() < 200) || (f.getY() > 1650))
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
        if (target instanceof Lava)
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
              {
                spawnFire(dd);
              }
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
  }

  private void createAnimations() {
    String fireAnimationName = "lava" + m_id;
    Animation<KyperSprite> spikeA = getState().getAnimation(fireAnimationName);
    if (spikeA == null) {
      getState().storeAnimation(fireAnimationName, getState().createGameAnimation("lavatile", .1f));
    }
    getAnimation().addAnimation("lava", fireAnimationName);
  }


}
