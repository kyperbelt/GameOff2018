package com.gameoff.game.objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.gameoff.game.Context;
import com.gameoff.game.control.AiControl;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.gameoff.game.control.StateControl.StateChangeListener;
import com.gameoff.game.objects.DirectionEntity;
import com.gameoff.game.objects.Projectile;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.AnimationController.AnimationListener;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.KyperBoxGame;
import com.badlogic.gdx.math.Vector2;

public class SpiderBossEnemy extends DirectionEntity {

  StateControl state;
  AttackControl attack;
  String animation;
  UserData context;
  AiControl ai;
  int m_id = 0;
  Random m_random = new Random();

  float damagedDuration = .2f;
  float damagedElapsed = 0;
  ShaderProgram damageShader;
  private int m_masterID = 0;
  boolean m_facingRight = true;
  float projectileSpeed = 600;
  
  Action shake = BakedEffects.shake(.5f, 10,false,false);

  BasicGameObject tail;
  BasicGameObject shadow;
  BasicGameObject stinger;
  AnimationController tailAnim;
  AnimationController stingerAnim;

  AnimationListener attackAnimationListener = new AnimationListener() {
    @Override
    public void finished(String animation, int times) {
      if (times >= 1) {
        getAnimation().setListener(null);
        setWalkAnimation(getDirection());
      state.setState(EntityState.Idling);
      }
    }
  };

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

  //Array<CollisionData> cols;
  
  AttackListener attackListener = new AttackListener() {
    @Override
    public void onAttack() {
      //getAnimation().set("attack");
      //getAnimation().setListener(attackAnimationListener);
      state.setState(EntityState.Attacking);
      
      Projectile p = Projectile.get(HealthGroup.Player); // get a pooled projectile
      p.setVelocity(0, 0);
      p.setPosition(getX() + getWidth() * 0.2f, getY() + getHeight() * .15f + getDepth());
      p.getAnimation().setAnimation("cherublaser", PlayMode.LOOP);
      p.getAnimation().setPlaySpeed(1);
      p.setSize(39,59);
      float w = p.getWidth();
      float h = p.getHeight();

      p.setBounds(w*0.3f, h*0.15f, w*0.3f, h*0.6f);
 
      getGameLayer().addGameObject(p, KyperBoxGame.NULL_PROPERTIES);
          
      MoveControl pmove = p.getMove();
      pmove.setMoveSpeed(projectileSpeed);
      m_vectorToPlayer.nor();
      pmove.setDirection(m_vectorToPlayer.x, m_vectorToPlayer.y);
      p.setRotation(m_vectorToPlayer.angle()-90);
      
      if (m_facingRight)
      {
        p.setPosition(getX() + getWidth() * 0.35f, getY() + getHeight() * .1f + getDepth());
      }

    }
  };

  public SpiderBossEnemy() {
    state = new StateControl(EntityState.Moving);
    context = new UserData(getClass().getSimpleName() + "_Context");
    context.put(Context.SELF, this);
    ai = new AiControl(context, getExampleAi());
    getMove().setMoveSpeed(100);
    getHealth().setHealthGroup(HealthGroup.Demon);
    getHealth().setDamageListener(damageListener);
    getHealth().setMaxHealth(50f);
    getHealth().setCurrentHealth(50f); 
    state.setStateChangeListener(stateListener);
    m_id = m_masterID;
    m_masterID++;
    tail =  new BasicGameObject();
    stinger = new BasicGameObject();
    shadow = new BasicGameObject();
    shadow.setSprite("player_shadow");
    shadow.setSize(100,50);
    setPreDrawChildren(true);

    attack = new AttackControl(null);
    attack.setAttackListener(attackListener);
    attack.setCooldown(5);

    setPlayerFindRange(1200);

  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    addController(state);
    addController(ai);
    addController(attack);

    tailAnim = new AnimationController();
    tail.addController(tailAnim);
    stingerAnim = new AnimationController();
    stinger.addController(stingerAnim);

    createAnimations();
    getMove().setFlying(true);
    addChild(shadow);

    damageShader = getState().getShader("damageShader");

    if (getWidth() == 0) {
      setSize(404, 300);
    }

    setCollisionBounds(90,60,220,130);

    tail.setSprite("bosstail_0");
    addChild(tail);
    tail.setSize(getWidth(),getHeight());
    tail.setPosition(0,0);
    tail.setIgnoreCollision(true);

    stinger.setSprite("bossstinger_0");
    addChild(stinger);
    stinger.setSize(getWidth(),getHeight());
    stinger.setPosition(0,0);
    stinger.setIgnoreCollision(true);

    getDirectionControl().setDirectionListener(new DirectionChangeListener() {
      @Override
      public void directionChanged(Direction lastDirection, Direction newDirection) {
        if (state.getState() == EntityState.Moving || state.getState() == EntityState.Idling) {
          setWalkAnimation(newDirection);
        }
      }
    });

    getDirectionControl().setDirection(Direction.Right);
    shadow.setPosition(50,0);
    shadow.setVisible(false); //shadow only visible when jumping
  }

  @Override
  public void update(float delta) {

    if (state.getState() == EntityState.Damaged) {
      if (damagedElapsed >= damagedDuration) {
        state.setState(EntityState.Moving);
      }
      damagedElapsed += delta;
    } else if ((state.getState() == EntityState.Idling) || (state.getState() == EntityState.Moving))
    {
      if (setClosestPlayerData())
        attack.attack();
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

  public void setWalkAnimation(Direction newDirection)
  {
    switch (newDirection) {
      case Left:
      case Up:
      case Down:
        getAnimation().set("move", PlayMode.LOOP);
        tailAnim.set("move", PlayMode.LOOP_PINGPONG);
        stingerAnim.set("move", PlayMode.LOOP_PINGPONG);
        //setFlip(true,false);
        //tail.setFlip(true,false);
        //shadow.setPosition(27,-20);
        m_facingRight = false;
        break;
      case Right:
        getAnimation().set("move", PlayMode.LOOP);
        tailAnim.set("move", PlayMode.LOOP_PINGPONG);
        stingerAnim.set("move", PlayMode.LOOP_PINGPONG);
        //setFlip(false,false);
        //tail.setFlip(false,false);
        //shadow.setPosition(43,-20);
        m_facingRight = true;
        break;
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

    String moveAnimation = "boss" + m_id;

    Animation<KyperSprite> moveRight = getState().getAnimation(moveAnimation);
    if (moveRight == null) {
      getState().storeAnimation(moveAnimation, getState().createGameAnimation("boss", .1f));
    }
    getAnimation().addAnimation("move", moveAnimation);

   /*
   String attackAnimation1 = "cherubattack" + m_id;

   moveRight = getState().getAnimation(attackAnimation1);

    if (moveRight == null) {
      getState().storeAnimation(attackAnimation1, getState().createGameAnimation("cherubattack", 0.1f));
    }

    getAnimation().addAnimation("attack", attackAnimation1);
    */

    String wingAnimation = "bosstail" + m_id;

    Animation<KyperSprite> wingA = getState().getAnimation(wingAnimation);
    if (wingA == null) {
      getState().storeAnimation(wingAnimation, getState().createGameAnimation("bosstail", .25f));
    }
    tailAnim.addAnimation("move", wingAnimation);

    String stingerAnimation = "bossstinger" + m_id;
    Animation<KyperSprite> stingA = getState().getAnimation(stingerAnimation);
    if (stingA == null) {
      getState().storeAnimation(stingerAnimation, getState().createGameAnimation("bossstinger", .25f));
    }
    stingerAnim.addAnimation("move", stingerAnimation);

    Animation<KyperSprite> laser = getState().getAnimation("cherublaser");
    if (laser == null) {
      getState().storeAnimation("cherublaser", getState().createGameAnimation("cherublaser", .05f));
    }
  }
}
