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
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;
import java.util.Random;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.controllers.AnimationController;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;

public class ScorpionEnemy extends DirectionEntity {

  StateControl state;
  String animation;
  UserData context;
  AiControl ai;
  int m_id = 0;
  Random m_random = new Random();
  BasicGameObject shadow;
  AnimationController shadowAnim;

  float damagedDuration = .2f;
  float damagedElapsed = 0;
  ShaderProgram damageShader;
  
  Action shake = BakedEffects.shake(.5f, 10,false,false);

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

  public ScorpionEnemy() {
    state = new StateControl(EntityState.Moving);
    context = new UserData(getClass().getSimpleName() + "_Context");
    context.put(Context.SELF, this);
    ai = new AiControl(context, getExampleAi());
    getMove().setMoveSpeed(80);
    getHealth().setHealthGroup(HealthGroup.Demon);
    getHealth().setDamageListener(damageListener);
    state.setStateChangeListener(stateListener);
    m_id = m_random.nextInt(99999999);
    setName("scorpion" + m_id);
    shadow = new BasicGameObject();
    shadow.setName("scorpshadow");
    setPreDrawChildren(true);
    //shadow.setSprite("shadowscorp_0");
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    addController(state);
    addController(ai);
    getMove().setFlying(true);

    shadowAnim = new AnimationController();
    shadow.addController(shadowAnim);
    //ZOrderControl z = new ZOrderControl();
    //shadow.addController(z);
    //z.setZOrder(ZOrder.SHADOW);

    createAnimations();

    damageShader = getState().getShader("damageShader");

    if (getWidth() == 0) {
      setSize(42, 62);
      setCollisionBounds(10, 10, getWidth()-20, getHeight()-25);
    }

    shadow.setSprite("shadowscorp_0");
    addChild(shadow);
    shadow.setSize(42,62);
    shadow.setPosition(0,-20);

    getAnimation().set("move");
    shadowAnim.set("shadow");

    getDirectionControl().setDirectionListener(new DirectionChangeListener() {
      @Override
      public void directionChanged(Direction lastDirection, Direction newDirection) {
        if (state.getState() == EntityState.Moving || state.getState() == EntityState.Idling) {
          switch (newDirection) {
          case Left:
            //getAnimation().set("move");
            setFlip(false,false);
            setRotation(-90);
            shadow.setFlip(false,false);
            //shadow.setRotation(-90);
            break;
          case Right:
            //getAnimation().set("move");
            setFlip(false,false);
            setRotation(90);
            shadow.setFlip(false,false);
            //shadow.setRotation(90);
            break;
          case Up:
            //getAnimation().set("move");
            setFlip(false,true);
            setRotation(0);
            shadow.setFlip(false,true);
            //shadow.setRotation(0);
            break;
          case Down:
            //getAnimation().set("move");
            setFlip(false,false);
            setRotation(0);
            shadow.setFlip(false,false);
            //shadow.setRotation(0);
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
    removeController(state);
    removeController(ai);
  }

  private BehaviorNode getExampleAi() {
    return BehaviorTree.generateRoot(Gdx.files.internal("behavior/simpleEnemy.btree"));
  }

  private void createAnimations() {

    String moveAnimation = "scorpion" + m_id;
    String shadowAnimationName = "shadowscorp" + m_id;

    Animation<KyperSprite> moveRight = getState().getAnimation(moveAnimation);
    if (moveRight == null) {
      getState().storeAnimation(moveAnimation, getState().createGameAnimation("scorpion", .1f));
    }

    getAnimation().addAnimation("move", moveAnimation);

    Animation<KyperSprite> shadowAnimation = getState().getAnimation(shadowAnimationName);
    if (shadowAnimation == null) {
      getState().storeAnimation(shadowAnimationName, getState().createGameAnimation("shadowscorp", .1f));
    }
    shadowAnim.addAnimation("shadow", shadowAnimationName);

  }
}
