package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.control.*;
import com.gameoff.game.objects.DoorMat;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.gameoff.game.ZOrder;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kyperbox.umisc.KyperSprite;

public class Door extends Basic {
  int m_code = 0;
  String m_aName = "do";
  public BasicGameObject m_keyHole = null;
  public DoorMat m_doorMat = null;

  // 0 = no door, 1 = open, 2 = closed, 3 = closed and locked
  public Door(int doorCode, GameState state) {
    getMove().setPassable(false);
    m_code = doorCode;
    getZOrder().setZOrder(ZOrder.DOORS);
    m_aName = m_aName + doorCode;
    state.storeAnimation(m_aName, state.createGameAnimation("door_open", 0.2f));
  }

  private void doDoorState(boolean open)
  {
    if (open)
    {
      getMove().setPhysical(false);
      getAnimation().set("door_open", PlayMode.NORMAL);
    } else
    {
      getMove().setPhysical(true);
      getAnimation().set("door_open", PlayMode.REVERSED);
      getAnimation().setPlaySpeed(1f);
    }
    getAnimation().setPlaySpeed(1f);
  }

  public BasicGameObject getKeyHole()
  {
    return m_keyHole;
  }

  public DoorMat getDoorMat()
  {
    return m_doorMat;
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());

    AnimationController animation = getAnimation();
    animation.addAnimation("door_open", m_aName);

    if (m_code == 1) 
    {
      setSprite("door_open_3");
      getMove().setPhysical(false);
      doDoorState(true);
    }
    else
    {
      setSprite("door_open_0");
      getMove().setPhysical(true);
      doDoorState(false);

      if (m_code == 3)
      {
        m_keyHole = new BasicGameObject();
        m_keyHole.setSprite("key_hole");
        Sprite sprite = getState().getGameSprite("key_hole");
        m_keyHole.setSize(sprite.getWidth(), sprite.getHeight());
        m_keyHole.setPosition(getX(), getY());
        ZOrderControl zorder = new ZOrderControl();
        zorder.setZOrder(ZOrder.KEYHOLE);
        m_keyHole.addController(zorder);
        this.getGameLayer().addGameObject(m_keyHole, null);

        m_doorMat = new DoorMat(this);
        this.getGameLayer().addGameObject(m_doorMat, null);
      }
    }
    getAnimation().setPlaySpeed(2f);
  }

  public void unlock()
  {
    if (m_code == 3) 
    {
      m_code = 2;
      m_keyHole.clearActions();
      m_keyHole.addAction(Actions.sequence(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor())));
      m_doorMat.remove();
      m_doorMat = null;
    }
  }

  public void open()
  {
    if (m_code < 2) return;
    doDoorState(true);
    m_code = 1;
  }

  public void close()
  {
    if (m_code > 1) return;
    doDoorState(false);
    m_code = 2;
  }

  public int getCode()
  {
    return m_code;
  }

}
