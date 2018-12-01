package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.gameoff.game.GameLevel;
import com.gameoff.game.Inputs;
import com.kyperbox.GameState;
import com.kyperbox.SoundManager;
import com.kyperbox.input.GameInput;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.*;
import com.kyperbox.umisc.BakedEffects;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.umisc.KyperSprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class InstructionsManager extends StateManager {

  BasicGameObject character;
  BasicGameObject details;
  float m_time = 0f;
  int m_introState = 0;
  AnimationController anim;
  
  public void playGame() {
    GameLevel l = GameLevel.generateLevel(1, 8, 6, 6);
    GameLevel.setCurrentLevel(l); //set singleton
    getState().getGame().setGameState("room_"+l.getCurrentRoom().getCode());
  }

  /**
   * use this to add the layersystems. This gets called before init and before any
   * object in layers are loaded in
   */
  @Override
  public void addLayerSystems(GameState state) {
  }

  @Override
  public void dispose(GameState state) {

  }

  /**
   * all objects in the tmx have been loaded - use this to initiate them if you
   * need to do something else to them
   */
  @Override
  public void init(GameState state) {
    state.playMusic(SoundManager.MUSIC,"bgmusic", true);

    state.getSoundManager().changeVolume(SoundManager.MUSIC, .2f);
    details = (BasicGameObject) state.getForegroundLayer().getActor("player_details");
    character = (BasicGameObject) state.getForegroundLayer().getActor("character");
    
    anim = new AnimationController();
    character.addController(anim);
    String animationName = "introplayer";
    Animation<KyperSprite> a = getState().getAnimation(animationName);
    if (a == null) {
      getState().storeAnimation(animationName, getState().createGameAnimation("introplayer", .05f));
    }

    anim.addAnimation("dtoa", animationName);

    animationName = "introplayera";
    a = getState().getAnimation(animationName);
    if (a == null) {
      getState().storeAnimation(animationName, getState().createGameAnimation("introplayera", .05f));
    }
    anim.addAnimation("atod", animationName);

    anim.set("dtoa", PlayMode.NORMAL);
    anim.setPlaySpeed(0f);


    /*
    
    play = (ImageButton) state.getUiLayer().getActor("playbutton");
    play.addListener(listener);
    play.setOrigin(Align.center);
    
    float pulseTime = 3f;
    float pulseScale = 1.5f;
    float pulseAngle = 10;//10 degrees of axis
    play.addAction(Actions.repeat(-1, BakedEffects.pulse(play, pulseTime, pulseScale, pulseAngle)));
    
    float bobAmount = 40;
    float bobTime = 1f;
    title = state.getUiLayer().getGameObject("title");
    title.addAction(Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
        Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime)))));
    */
    //title.setVisible(false);
  }

  @Override
  public void update(GameState state, float delta) {
    
    GameInput input = state.getInput();
    if((input.inputJustPressed(Inputs.ATTACK)) || (input.inputJustPressed(Inputs.TRANSFORM))) {
      playGame();
    }

    m_time += delta;
    if (m_introState == 0)
    {
      if (m_time > 2.5f)
      {
        anim.set("dtoa", PlayMode.NORMAL);
        anim.setPlaySpeed(1f);
        m_introState = 1;
        m_time = 0f;
        details.setSprite("angeldetails");
      }     
    } else if (m_introState == 1)
    {
      if (m_time > 2.5f)
      {
        anim.set("atod", PlayMode.NORMAL);
        anim.setPlaySpeed(1f);
        m_introState = 0;
        m_time = 0f; 
        details.setSprite("demondetails"); 
      }    
    }

  }

}
