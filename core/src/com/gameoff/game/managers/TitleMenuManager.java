package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;
import com.kyperbox.umisc.BakedEffects;
import com.gameoff.game.GameLevel;

public class TitleMenuManager extends StateManager {

	ImageButton play;
	Image title;
	ClickListener listener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			Actor a = event.getListenerActor();
			if (a == play) {
				GameLevel l = GameLevel.generateLevel(1, 9, 6, 6);
				GameLevel.setCurrentLevel(l); //set singleton
				getState().getGame().setGameState("level");
			}
		};
	};

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

		play = (ImageButton) state.getUiLayer().getActor("playbutton");
		play.addListener(listener);
		play.setOrigin(Align.center);
		
		float pulseTime = 3f;
		float pulseScale = 1.5f;
		float pulseAngle = 10;//10 degrees of axis
		play.addAction(Actions.repeat(-1, BakedEffects.pulse(play, pulseTime, pulseScale, pulseAngle)));

		float bobAmount = 40;
		float bobTime = 1f;
		title = (Image) state.getUiLayer().getActor("title");
		title.addAction(Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
				Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime)))));
	}

	@Override
	public void update(GameState state, float delta) {

	}

}
