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
  float m_spawnTime = 2.0f;
  float m_spawnSpeed = 2.0f;
  int m_spawnStyle = 1;  //0 is slower and random, 1 is spreads out fast

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
    setSize(64,88);
    setBounds(5,15,getWidth()-10,10);
    setSpawnSpeed(1f);
    setSpread(true);
    setLife(3f);
  }

  public void setSpread(boolean spread)
  {
    m_spread = spread;
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
    f.setSpawnSpeed(m_spawnSpeed);
    f.setSpread(true);

    float f1 = m_random.nextFloat()*10f;
    float f2 = m_random.nextFloat()*10f;
    

    if (dir == 0)
    {
      f.setPosition(getX() - 10 + f1, getY() - 50 - f2);
    } else if (dir == 2)
    {
      f.setPosition(getX() - 10 + f1, getY() + 50 + f2);
    } else if (dir == 1)
    {
      f.setPosition(getX() + 64 + f1, getY() - 10 + f1);
    } else
    {
      f.setPosition(getX() - 64 - f1, getY() - 10 + f1);
    }
    getGameLayer().addGameObject(f,null);
    f.setBounds(5,0,f.getWidth()-10,64);
    f.getCollision().getCollisions(f,0.005f);
    f.getCollision().getCollisions(f,0.005f);
    removeFireIfNotValid(f);
    f.setBounds(5,15,f.getWidth()-10,10);
    
  }

  private void removeFireIfNotValid(Fire f)
  {
    Array<CollisionData> c = f.getCollision().getCollisions();
    for (int i = 0; i < c.size; i++) {
			CollisionData data = c.get(i);
			GameObject target = data.getTarget();
      if (target instanceof Player)
        return;

      if (target instanceof Fire)
      {
        f.remove();
        return;
      }

			if (target instanceof Basic) {
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
          m_spawnTime = m_spawnSpeed + m_random.nextFloat()*m_spawnSpeed;
          if (m_spawnStyle == 0)
            spawnFire(m_random.nextInt(4));
          else
          {
            for (int dd = 0; dd < 4; dd++)
              spawnFire(dd);
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
    String fireAnimationName = "fire" + m_id;
    Animation<KyperSprite> spikeA = getState().getAnimation(fireAnimationName);
    if (spikeA == null) {
      getState().storeAnimation(fireAnimationName, getState().createGameAnimation("fire", .1f));
    }
    getAnimation().addAnimation("fire", fireAnimationName);
  }


}
