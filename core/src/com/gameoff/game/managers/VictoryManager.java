package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Inputs;
import com.kyperbox.GameState;
import com.kyperbox.input.GameInput;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameLayer;
import com.gameoff.game.Inputs;
import com.kyperbox.SoundManager;

public class VictoryManager extends StateManager {

  BasicGameObject title;
  float delay = 2.5f;

  @Override
  public void addLayerSystems(GameState state) {

  }

  @Override
  public void init(GameState state) {

    state.playMusic(SoundManager.MUSIC,"bgmusic", true);
    state.getSoundManager().changeVolume(SoundManager.MUSIC, .2f);
    title = (BasicGameObject)state.getForegroundLayer().getGameObject("victory");
    title.clearActions();
    title.setColor(1,1,1,0);
    title.addAction(Actions.fadeIn(1.5f));
  }

  @Override
  public void update(GameState state, float delta) {
    
    GameInput input = state.getInput();
    delay -= delta;
    if (delay > 0) return;

    if ((input.inputJustPressed(Inputs.ATTACK)) || (input.inputJustPressed(Inputs.TRANSFORM))) {
      getState().getGame().setGameState("title");
    }
  }

  @Override
  public void dispose(GameState state) {

  }

}
