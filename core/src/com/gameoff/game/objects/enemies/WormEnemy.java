package com.gameoff.game.objects.enemies;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.gameoff.game.Context;
import com.gameoff.game.Sounds;
import com.gameoff.game.control.AiControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.gameoff.game.control.StateControl.StateChangeListener;
import com.gameoff.game.objects.Collectible;
import com.gameoff.game.objects.DirectionEntity;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;

public class WormEnemy extends EnemyEntity{

  StateControl state;
  String animation;
  UserData context;
  AiControl ai;
  int m_id = 0;
  Random m_random = new Random();

  float sw = 91;
  float sh = 58;
  float hp = 2;
  float moveSpeed = 300;

  float damagedDuration = .2f;
  float damagedElapsed = 0;
  ShaderProgram damageShader;
  private int m_masterID = 0;
  
  Action shake = BakedEffects.shake(.5f, 10,false,false);

  DamageListener damageListener=new DamageListener(){

  @Override public void damaged(float amount){state.setState(EntityState.Damaged);damagedElapsed=0;hit();}};

  StateChangeListener stateListener = new StateChangeListener() {

    @Override
    public void stateChanged(EntityState last, EntityState newState) {
      if (newState == EntityState.Damaged) {
        clearActions();
        shake.restart();
        addAction(shake);
        getAnimation().setPlaySpeed(0f);
      }else if(last == EntityState.Damaged){
        //no longer damaged
        clearActions();
        getAnimation().setPlaySpeed(1f);
      }
    }
  };

  // 0 small, 1 = big
  public WormEnemy(int code) {
    state = new StateControl(EntityState.Moving);
    context = new UserData(getClass().getSimpleName() + "_Context");
    context.put(Context.SELF, this);
    ai = new AiControl(context, getExampleAi());
    getMove().setMoveSpeed(250);
    getHealth().setHealthGroup(HealthGroup.Demon);
    getHealth().setDamageListener(damageListener);
    state.setStateChangeListener(stateListener);
    m_id = m_masterID;
    m_masterID++;

    if (code == 1)
    {
      sw = 152;
      sh = 96;
      hp = 4;
      moveSpeed = moveSpeed - 50;
      maxItemDrop(5);
    }
    
    getDropTable().addDrop(Collectible.SOUL, 3f);
    getDropTable().addDrop(Collectible.HEART,1f);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    addController(state);
    addController(ai);
    createAnimations();

    damageShader = getState().getShader("damageShader");

    setSize(sw, sh);
    setCollisionBounds(30, 10, getWidth()-60, getHeight()-20);
    getHealth().setMaxHealth(hp);
    getMove().setMoveSpeed(moveSpeed);

    getDirectionControl().setDirectionListener(new DirectionChangeListener() {
      @Override
      public void directionChanged(Direction lastDirection, Direction newDirection) {
        if (state.getState() == EntityState.Moving || state.getState() == EntityState.Idling) {
          switch (newDirection) {
          case Left:
            getAnimation().set("move");
            setFlip(true,false);
            break;
          case Right:
            getAnimation().set("move");
            setFlip(false,false);
            break;
          }
        }
      }
    });

    getDirectionControl().setDirection(Direction.Right);
  }

  @Override
  public void update(float delta) {

    if (state.getState() == EntityState.Damaged) {
      if (damagedElapsed >= damagedDuration) {
        state.setState(EntityState.Moving);
      }
      damagedElapsed += delta;
    }
    super.update(delta);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (state.getState() == EntityState.Damaged && damageShader != null) {
      ShaderProgram lastShader = batch.getShader();
      batch.setShader(damageShader);
      super.draw(batch, parentAlpha);
      batch.setShader(lastShader);
    } else {
      super.draw(batch, parentAlpha);
    }
  }

  @Override
  public void onRemove() {
    super.onRemove();
    getState().playSound(Sounds.WormDie);
    removeController(state);
    removeController(ai);
  }

  private BehaviorNode getExampleAi() {
    return BehaviorTree.generateRoot(Gdx.files.internal("behavior/wormPingpong.btree"));
  }

  private void createAnimations() {

    String moveAnimation = "worm" + m_id;

    Animation<KyperSprite> moveRight = getState().getAnimation(moveAnimation);
    if (moveRight == null) {
      getState().storeAnimation(moveAnimation, getState().createGameAnimation("worm", .1f));
    }

    getAnimation().addAnimation("move", moveAnimation);

  }
}
