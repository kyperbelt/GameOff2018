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
import com.gameoff.game.objects.Fire;
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
  float m_doFireTime = 3;

  float damagedDuration = .2f;
  float damagedElapsed = 0;
  ShaderProgram damageShader;
  private int m_masterID = 0;
  boolean m_facingRight = true;
  float projectileSpeed = 700;
  
  Action shake = BakedEffects.shake(.5f, 10,false,false);

  BasicGameObject tail;
  BasicGameObject shadow;
  BasicGameObject stinger;
  BasicGameObject glow;
  AnimationController tailAnim;
  AnimationController stingerAnim;
  AnimationController glowAnim;

  AnimationListener attackAnimationListener = new AnimationListener() {
    @Override
    public void finished(String animation, int times) {
      if (times >= 1) {
        getAnimation().setListener(null);
        setWalkAnimation(getDirection());
        shootLaser();
        glow.setVisible(false);
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
      System.out.println("Attack called!!!");
      state.setState(EntityState.Attacking);
      getAnimation().set("attacka");
      getAnimation().setListener(attackAnimationListener);
      tailAnim.set("middle", PlayMode.LOOP);
      stingerAnim.set("middle", PlayMode.LOOP);
      tailAnim.setPlaySpeed(1);
      stingerAnim.setPlaySpeed(1);
      glow.setVisible(true);
      glowAnim.set("attacka");
      glowAnim.setPlaySpeed(1);
      getMove().setDirection(0,0);
    }
  };

  public SpiderBossEnemy() {
    state = new StateControl(EntityState.Moving);
    context = new UserData(getClass().getSimpleName() + "_Context");
    context.put(Context.SELF, this);
    ai = new AiControl(context, getExampleAi());
    getMove().setMoveSpeed(100);
    getHealth().setHealthGroup(HealthGroup.Boss);
    getHealth().setDamageListener(damageListener);
    getHealth().setMaxHealth(50f);
    getHealth().setCurrentHealth(50f); 
    state.setStateChangeListener(stateListener);
    m_id = m_masterID;
    m_masterID++;
    tail =  new BasicGameObject();
    stinger = new BasicGameObject();
    glow = new BasicGameObject();
    glow.setVisible(false);
    shadow = new BasicGameObject();
    shadow.setSprite("player_shadow");
    shadow.setSize(100,50);
    setPreDrawChildren(true);

    attack = new AttackControl(null);
    attack.setAttackListener(attackListener);
    attack.setCooldown(7);

    setPlayerFindRange(1200);

    tailAnim = new AnimationController();
    stingerAnim = new AnimationController();
    glowAnim = new AnimationController();

  }


  private void shootLaser()
  { 
    Projectile p = Projectile.get(HealthGroup.Player); // get a pooled projectile
    p.setVelocity(0, 0);
    p.setRotation(0);
    p.setSize(23,110);
    float w = p.getWidth();
    float h = p.getHeight();
    p.setBounds(w*0.35f, h*0.35f, w*0.3f, h*0.3f);
    getGameLayer().addGameObject(p, KyperBoxGame.NULL_PROPERTIES);
    p.getAnimation().setAnimation("bossweapon", PlayMode.LOOP_PINGPONG);
    p.getAnimation().setPlaySpeed(1);
    p.setSprite("bossweapon_0");
        
    MoveControl pmove = p.getMove();
    pmove.setMoveSpeed(projectileSpeed);
    m_vectorToPlayer.nor();
    float angle = m_vectorToPlayer.angle()-90;
    pmove.setDirection(m_vectorToPlayer.x, m_vectorToPlayer.y);
    p.setRotation(angle);

    float cx = p.getWidth()/2;
    float cy = p.getHeight()/2;

    float co = (float)Math.cos(Math.toRadians(angle));
    float so = (float)Math.sin(Math.toRadians(angle));

    float sx = cx*co - cy*so;
    float sy = cy*co + cx*co; 

    p.setPosition(getX() + 231 - cx + sx, getY() + 236  - cy + sy);
  }

  public void spawnFire()
  {
    for (int i=0; i < 4; i++)
    {
      Fire f = new Fire();
      getGameLayer().addGameObject(f,null);
      f.setLife(2);
      f.setSpread(true);
      f.setSpawnSpeed(1000);

      float ox = 65;
      float oy = 35;
      if (i == 1)
      {
        ox = 95;
        oy = 140;
      } else if (i == 2)
      {
        ox = 356;
        oy = 140;
      } else if (i == 3)
      {
        ox = 400;
        oy = 35;
      }
      f.setPosition(getX() + ox,getY() + oy);
    }
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    addController(state);
    addController(ai);
    addController(attack);

    tail.addController(tailAnim);
    stinger.addController(stingerAnim);
    glow.addController(glowAnim);

    createAnimations();
    getMove().setFlying(true);
    addChild(shadow);

    damageShader = getState().getShader("damageShader");

    if (getWidth() == 0) {
      setSize(538, 400);
    }

    setCollisionBounds(168,65,200,160);

    tail.setSprite("bosstailm_0");
    addChild(tail);
    tail.setSize(getWidth(),getHeight());
    tail.setPosition(0,0);
    tail.setIgnoreCollision(true);
    tailAnim.setPlaySpeed(1);
    tailAnim.set("middle",PlayMode.NORMAL);

    stinger.setSprite("bossstingerm_0");
    addChild(stinger);
    stinger.setSize(getWidth(),getHeight());
    stinger.setPosition(0,0);
    stinger.setIgnoreCollision(true);
    stingerAnim.setPlaySpeed(1);
    stingerAnim.set("middle",PlayMode.NORMAL);

    glow.setSprite("bosswglow_0");
    addChild(glow);
    glow.setSize(120,146);
    glow.setPosition(188,143);
    glow.setIgnoreCollision(true);
    glow.setVisible(false);

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
      {
        attack.attack();
      }
    }

    if (state.getState() == EntityState.Moving)
    {
      m_doFireTime -= delta;
      if (m_doFireTime < 0)
      {
        m_doFireTime = 3f;
        spawnFire();
      }
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
      stinger.draw(batch,parentAlpha);
      if (glow.isVisible()) glow.draw(batch, parentAlpha);
    } else {
      super.draw(batch, parentAlpha);
      stinger.draw(batch,parentAlpha);
      if (glow.isVisible()) glow.draw(batch, parentAlpha);
    }
  }

  public void setWalkAnimation(Direction newDirection)
  {
    getAnimation().set("move", PlayMode.LOOP);
    switch (newDirection) {
      case Left:
        tailAnim.set("movel", PlayMode.NORMAL);
        stingerAnim.set("movel", PlayMode.NORMAL);
        //setFlip(true,false);
        //tail.setFlip(true,false);
        //shadow.setPosition(27,-20);
        m_facingRight = false;
        break;
      case Right:
        tailAnim.set("mover", PlayMode.NORMAL);
        stingerAnim.set("mover", PlayMode.NORMAL);
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

    addAnimation(m_id, "move", "boss", getAnimation(), 0.1f);
    addAnimation(m_id, "attacka", "bossattacka", getAnimation(), 0.3f);

    addAnimation(m_id, "middle", "bosstailm", tailAnim, 5f);
    addAnimation(m_id, "movel", "bosstaill", tailAnim, 0.25f);
    addAnimation(m_id, "mover", "bosstailr", tailAnim, 0.25f);

    addAnimation(m_id, "middle", "bossstingerm", stingerAnim, 5f);
    addAnimation(m_id, "movel", "bossstingerl", stingerAnim, 0.25f);
    addAnimation(m_id, "mover", "bossstingerr", stingerAnim, 0.25f);

    addAnimation(m_id, "bossweapon", "bossweapon", getAnimation(), 0.04f);

    addAnimation(m_id, "attacka", "bosswglow", glowAnim, 0.05f);

  }
}
