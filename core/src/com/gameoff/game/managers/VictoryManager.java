package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Inputs;
import com.kyperbox.GameState;
import com.kyperbox.input.GameInput;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameLayer;

public class VictoryManager extends StateManager {

  BasicGameObject youdied;

  BasicGameObject menu, replay;
  BasicGameObject selector;

  int selected = 0;

  Array<BasicGameObject> menuItems;

  public VictoryManager() {

    menuItems = new Array<BasicGameObject>();

  }

  @Override
  public void addLayerSystems(GameState state) {

  }

  @Override
  public void init(GameState state) {
    menuItems.clear();
    GameLayer ui = state.getUiLayer();

    menu = (BasicGameObject) ui.getGameObject("menu");
    replay = (BasicGameObject) ui.getGameObject("replay");

    selector = (BasicGameObject) ui.getGameObject("selector");

    menuItems.add(replay);
    menuItems.add(menu);

    setSelectedIndex(selected);

    youdied = (BasicGameObject) state.getForegroundLayer().getGameObject("victory");
    state.getColor().a = 0f;
    state.clearActions();
    state.addAction(Actions.fadeIn(1f));

    float bobAmount = 40;
    float bobTime = 2f;
    youdied.addAction(Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
        Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime)))));
  }

  public void setSelectedIndex(int index) {
    selected = index;
    BasicGameObject so = menuItems.get(index);
    selector.clearActions();
    selector.addAction(Actions.moveTo(so.getX() - selector.getWidth() * 1.5f, so.getY()+so.getHeight()*.5f-selector.getHeight()*.5f, .2f));
  }

  public void nextMenuItem() {
    int index = selected + 1;
    if (index >= menuItems.size)
      index = 0;
    setSelectedIndex(index);
  }

  public void prevMenuItem() {
    int index = selected - 1;
    if (index < 0)
      index = menuItems.size - 1;
    setSelectedIndex(index);
  }
  
  boolean upPressed = false;
  boolean upJustPressed = false;
  boolean downPressed = false;
  boolean downJustPressed = false;

  @Override
  public void update(GameState state, float delta) {

    GameInput input = state.getInput();
    float threshold = .2f;
    float inputUp = input.inputValue(Inputs.UP);
    float inputDown = input.inputValue(Inputs.DOWN);
    
    if(inputUp > threshold) {
      upPressed = true;
    }else {
      upPressed = false;
      upJustPressed = false;
    }
    
    if(inputDown > threshold) {
      downPressed = true;
    }else {
      downPressed = false;
      downJustPressed = false;
    }
    
    if (!upJustPressed && upPressed) {
      System.out.println("pressed up");
      prevMenuItem();
      upJustPressed = true;
    }else if (!downJustPressed && downPressed) {
      System.out.println("pressed down");
      nextMenuItem();
      downJustPressed = true;
    }
    
    if(input.inputJustPressed(Inputs.ATTACK)) {
      BasicGameObject so = menuItems.get(selected);
      if(so == menu) {
        goToMenu();
      }
    }
  }
  
  public void goToMenu() {
    getState().getGame().setGameState("title");
  }

  @Override
  public void dispose(GameState state) {

  }

}
