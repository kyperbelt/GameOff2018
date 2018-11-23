package com.gameoff.game.objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.gameoff.game.Context;
import com.gameoff.game.control.AiControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.gameoff.game.control.StateControl.StateChangeListener;
import com.gameoff.game.objects.DirectionEntity;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.objects.BasicGameObject;

public class CherubEnemy extends DirectionEntity {

  StateControl state;
  String animation;
  UserData context;
  AiControl ai;
  int m_id = 0;
  Random m_random = new Random();

  float damagedDuration = .2f;
  float damagedElapsed = 0;
  ShaderProgram damageShader;
  private int m_masterID = 0;
  
  Action shake = BakedEffects.shake(.5f, 10,false,false);

  BasicGameObject wings;
  BasicGameObject shadow;
  AnimationController wingsAnim;

  DamageListener damageListener=new DamageListener(){

  @Override public void damaged(float amount){state.setState(EntityState.Damaged);damagedElapsed=0;}};

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

  public CherubEnemy() {
    state = new StateControl(EntityState.Moving);
    context = new UserData(getClass().getSimpleName() + "_Context");
    context.put(Context.SELF, this);
    ai = new AiControl(context, getExampleAi());
    getMove().setMoveSpeed(150);
    getHealth().setHealthGroup(HealthGroup.Angel);
    getHealth().setDamageListener(damageListener);
    state.setStateChangeListener(stateListener);
    m_id = m_masterID;
    m_masterID++;
    wings =  new BasicGameObject();
    shadow = new BasicGameObject();
    shadow.setSprite("player_shadow");
    shadow.setSize(40,24);
    setPreDrawChildren(true);

  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    addController(state);
    addController(ai);
    wingsAnim = new AnimationController();
    wings.addController(wingsAnim);

    createAnimations();
    getMove().setFlying(true);
    addChild(shadow);

    damageShader = getState().getShader("damageShader");

    if (getWidth() == 0) {
      setSize(117, 96);
      setCollisionBounds(20, 20, getWidth()-40, getHeight()-30);
    }

    wings.setSprite("cherubwings_0");
    addChild(wings);
    wings.setSize(getWidth(),getHeight());
    wings.setPosition(0,0);

    getDirectionControl().setDirectionListener(new DirectionChangeListener() {
      @Override
      public void directionChanged(Direction lastDirection, Direction newDirection) {
        if (state.getState() == EntityState.Moving || state.getState() == EntityState.Idling) {
          switch (newDirection) {
          case Left:
            getAnimation().set("move", PlayMode.LOOP_PINGPONG);
            wingsAnim.set("move", PlayMode.LOOP_PINGPONG);
            setFlip(true,false);
            wings.setFlip(true,false);
            shadow.setPosition(27,-20);
            break;
          case Right:
            getAnimation().set("move", PlayMode.LOOP_PINGPONG);
            wingsAnim.set("move", PlayMode.LOOP_PINGPONG);
            setFlip(false,false);
            wings.setFlip(false,false);
            shadow.setPosition(43,-20);
            break;
          }
        }
      }
    });

    getDirectionControl().setDirection(Direction.Right);
    shadow.setPosition(43,-15);
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
    removeController(state);
    removeController(ai);
  }

  private BehaviorNode getExampleAi() {
    return BehaviorTree.generateRoot(Gdx.files.internal("behavior/simpleEnemy.btree"));
  }

  private void createAnimations() {

    String moveAnimation = "cherubbody" + m_id;

    Animation<KyperSprite> moveRight = getState().getAnimation(moveAnimation);
    if (moveRight == null) {
      getState().storeAnimation(moveAnimation, getState().createGameAnimation("cherub", .15f));
    }
    getAnimation().addAnimation("move", moveAnimation);


    String wingAnimation = "cherubwings" + m_id;

    Animation<KyperSprite> wingA = getState().getAnimation(wingAnimation);
    if (wingA == null) {
      getState().storeAnimation(wingAnimation, getState().createGameAnimation("cherubwings", .1f));
    }
    wingsAnim.addAnimation("move", wingAnimation);

  }
}
