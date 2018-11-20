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

public class Spiker extends Basic {

  AttackControl attack;
  Array<CollisionData> cols;
  boolean m_spikesOut = true;
  float m_time = 0;
  float m_pauseNone = 1.0f;
  int m_state = 0;
  int m_id = 1;
  String m_mode = "A";
  Random m_random = new Random();

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

  public Spiker(HealthGroup... damageGroup) {
    this.damageGroup = damageGroup;
    getMove().setPhysical(false);
    attack = new AttackControl(1f,1f,hazardListener);
    getZOrder().setZOrder(ZOrder.PLAYER);
    setYOffset(40);
    m_id = m_random.nextInt(9999999);
  }
  
  public Spiker() {
    this(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    createAnimations();

    // hazard does not have health so we remove the health control so that systems
    // that interact with the health control do not affect it
    removeController(getHealth());
    addController(attack);
    setSprite("spiker_red_0");
    m_mode = "A";
    m_spikesOut = false;
    m_time = 0;
    m_state = 0;

    
    Wall o = new Wall();
    o.setSize(44,45);
    o.setPosition(getX() + 68, getY()+30);
    this.getGameLayer().addGameObject(o, null);
  
  ;
}
  
  
  @Override
  public void update(float delta) {
    super.update(delta);

    m_time += delta;

    if (m_state == 0)
    {
      if (m_time > m_pauseNone)
      {
        m_time = 0;
        m_state = 10;
        getAnimation().set("spike" + m_mode,PlayMode.NORMAL);
      }
    } else if (m_state == 10)
    {
      if (m_time > 0.25f)
      {
        m_spikesOut = true;
      }

      if (m_time > 0.5f)
      {
        m_time = 0;
        m_state = 20;
        getAnimation().set("spike" + m_mode,PlayMode.REVERSED);
      }
    } else if (m_state == 20)
    {
      if (m_time > 0.1f)
      {
        m_spikesOut = false;
      }

      if (m_time > 0.5f)
      {
        m_time = 0;
        m_state = 0;
        if (m_mode == "A") 
        {
          m_mode = "B";
        } else
        {
          m_mode = "A";
        }
        m_pauseNone = 1.0f + m_random.nextFloat()*3f;
      }
    }

    if (m_spikesOut)
    {
      cols = getCollision().getCollisions();
      if(cols.size > 0) {
          attack.attack();
      }
    }

    cols = null;//null out because col data is pooled
  }

  @Override
  public void onRemove() {
    super.onRemove();
    removeController(attack);
  }

  private void createAnimations() {

    String spikeAnimationNameA = "spikerA" + m_id;
    String spikeAnimationNameB = "spikerB" + m_id;
    Animation<KyperSprite> spikeA = getState().getAnimation(spikeAnimationNameA);
    if (spikeA == null) {
      getState().storeAnimation(spikeAnimationNameA, getState().createGameAnimation("spiker_redA", .1f));
    }

    Animation<KyperSprite> spikeB = getState().getAnimation(spikeAnimationNameB);
    if (spikeB== null) {
      getState().storeAnimation(spikeAnimationNameB, getState().createGameAnimation("spiker_redB", .1f));
    }

    getAnimation().addAnimation("spikeA", spikeAnimationNameA);
    getAnimation().addAnimation("spikeB", spikeAnimationNameB);

  }


}
