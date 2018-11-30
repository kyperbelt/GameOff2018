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
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.BakedEffects;

public class TitleMenuManager extends StateManager {

	ImageButton play;
	GameObject title;
	ClickListener listener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			Actor a = event.getListenerActor();
			if (a == play) {
				playGame();
			}
		};
	};
	
	public void playGame() {
		getState().getGame().setGameState("instructions");
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
	
		//title.setVisible(false);
	}

	@Override
	public void update(GameState state, float delta) {
		
		GameInput input = state.getInput();
		if(input.inputJustPressed(Inputs.ATTACK)) {
			playGame();
		}
	}

}
