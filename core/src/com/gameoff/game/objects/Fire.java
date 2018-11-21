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

public class Fire extends Basic {

  AttackControl attack;
  Array<CollisionData> cols;
  int m_id = 1;
  Random m_random = new Random();
  float m_lifeTime = 9999999;
  float m_maxLife = 0;
  boolean m_spread = false;
  float m_spawnTime = 4.0f;

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
    m_id = m_random.nextInt(9999999);
  }
  
  public Fire() {
    this(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
    //setSpread(true);
    //setLife(13f);
  }

  public void setSpread(boolean spread)
  {
    m_spread = spread;
    m_spawnTime = 3f + m_random.nextFloat()*5f;
  }

  public void setLife(float tm)
  {
    m_lifeTime = tm;
    m_maxLife = tm;
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    createAnimations();

    // Fire does not have health so we remove the health control so that systems
    // that interact with the health control do not affect it
    removeController(getHealth());
    addController(attack);
    setSprite("fire_0");
    getAnimation().set("fire", PlayMode.LOOP);
    getAnimation().setPlaySpeed(1.0f + m_random.nextFloat());
    setBounds(5,15,getWidth()-10,10);
  }
  
  public void spawnFire(int dir)
  {
    //0 to 3 for now
    Fire f = new Fire();
    f.setLife(m_maxLife);
    f.setSpread(true);
    getGameLayer().addGameObject(f,null);
    f.setSize(64,88);
    if (dir == 0)
    {
      f.setPosition(getX() - 10 + m_random.nextFloat()*20f, getY() - 30 - m_random.nextFloat()*25f);
    } else if (dir == 2)
    {
      f.setPosition(getX() - 10 + m_random.nextFloat()*20f, getY() + 30 + m_random.nextFloat()*25f);
    } else if (dir == 1)
    {
      f.setPosition(getX() + 32 + m_random.nextFloat()*32f, getY() - 10 + m_random.nextFloat()*20f);
    } else
    {
      f.setPosition(getX() - 32 - m_random.nextFloat()*32f, getY() - 10 + m_random.nextFloat()*20f);
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
        if (m_spawnTime < 0)
        {
          m_spawnTime = 5f + m_random.nextFloat()*5f;
          spawnFire(m_random.nextInt(4));
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
    String fireAnimationName = "fire" + m_id;
    Animation<KyperSprite> spikeA = getState().getAnimation(fireAnimationName);
    if (spikeA == null) {
      getState().storeAnimation(fireAnimationName, getState().createGameAnimation("fire", .1f));
    }
    getAnimation().addAnimation("fire", fireAnimationName);
  }


}
