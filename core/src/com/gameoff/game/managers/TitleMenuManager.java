package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;

public class TitleMenuManager extends StateManager{

	ImageButton play;
	ClickListener listener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			Actor a = event.getListenerActor();
			if(a == play) {
				getState().getGame().setGameState("level");
			}
		};
	};
	
	/**
	 * use this to add the layersystems. This gets called before init and before any object in layers are loaded in
	 */
	@Override
	public void addLayerSystems(GameState state) {
	}

	@Override
	public void dispose(GameState state) {
		
	}

	/**
	 * all objects in the tmx have been loaded - use this to initiate them if you need to do something else to them
	 */
	@Override
	public void init(GameState state) {

		play = (ImageButton) state.getUiLayer().getActor("playbutton");
		play.addListener(listener);
	}

	@Override
	public void update(GameState state, float delta) {
		
	}

}
