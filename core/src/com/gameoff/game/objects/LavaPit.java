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

public class LavaPit extends Basic {

  AttackControl attack;
  Array<CollisionData> cols;
  static int masterID = 0;
  int m_id = 1;
  Random m_random = new Random();

  float m_stateTime = 3f;
  int m_state = 0;

  float m_lifeTime = 9999999;
  float m_maxLife = 0;
  boolean m_spread = false;
  float m_spawnTime = 2.0f;
  float m_spawnSpeed = 2.0f;
  int m_spawnStyle = 1;  //0 is slower and random, 1 is spreads out fast, 2 - single direction
  int m_directionsBitFlags = 0;
  boolean m_randomPositioning = true;
  float m_decay = 0.1f;
  int m_maxSpawn = 8;

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

  public LavaPit(HealthGroup... damageGroup) {
    this.damageGroup = damageGroup;
    getMove().setPhysical(false);
    attack = new AttackControl(1f,1f,hazardListener);
    getZOrder().setZOrder(ZOrder.PLAYER);
    setYOffset(0);
    masterID++;
    m_id = masterID;
  }
  
  public LavaPit() {
    this(HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
    setSize(64,64);
    setBounds(5,15,getWidth()-10,10);
    /*
    setSpawnSpeed(0.2f);
    setSpread(true);
    setLife(5.0f);
    setDecay(0.0f);
    setSpawnStyle(1);
    setRandomPositioning(false);
    setMaxSpawn(1);
    */
  }

  public void setMaxSpawn(int max)
  {
    m_maxSpawn = max;
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
    setSprite("lavaflow_0");
    setBounds(5,15,getWidth()-10,10);
    getAnimation().setPlaySpeed(0f);
    m_stateTime = 0;
    m_state = 0;
    setVisible(false);
  }
  
  @Override
  public void update(float delta) {
    super.update(delta);
  
    cols = null; //null out because col data is pooled

    m_stateTime += delta;
    if (m_state == 0)
    {
      //not triggered
      if (m_stateTime > 4)
      {
        m_state = 10;
        m_stateTime = 0;
        setVisible(true);
        getAnimation().setPlaySpeed(1f);
        getAnimation().set("lavapit", PlayMode.NORMAL);
      }
    } if (m_state == 10)
    {
      if (m_stateTime > 1.1f)
      {
        m_state = 20;
        m_stateTime = 0;
        //spawn some lava all around
        for (int xx = -1; xx < 2; xx++)
        {
          for (int yy = -1; yy < 2; yy++)
          {
            Lava l = new Lava();
            l.setLife(15);
            l.setSpawnSpeed(2.0f);
            l.setSpread(true);
            l.setMaxSpawn(1);
            l.setSpawnStyle(1);
            l.setPosition(getX() + getWidth()/2 -32 + xx*64, getY() - getHeight()/2 + 48 + yy*64);
            getGameLayer().addGameObject(l,null);
            l.setBounds(0,0,getWidth(),getHeight());
          }
        }
        setVisible(false);
      }
    }
  }

  @Override
  public void onRemove() {
    super.onRemove();
    removeController(attack);
  }

  private void createAnimations() {
    String fireAnimationName = "lavapit" + m_id;
    Animation<KyperSprite> spikeA = getState().getAnimation(fireAnimationName);
    if (spikeA == null) {
      getState().storeAnimation(fireAnimationName, getState().createGameAnimation("lavaflow", .15f));
    }
    getAnimation().addAnimation("lavapit", fireAnimationName);
  }


}
