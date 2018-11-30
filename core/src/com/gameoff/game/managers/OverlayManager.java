package com.gameoff.game.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.gameoff.game.objects.HealthBauble;
import com.gameoff.game.objects.SoulMeter;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.umisc.KyperSprite;

public class OverlayManager extends StateManager {

	private float fadeinTime = .8f;

	private Image keyIcon;
	private Label keyLabel;

	private BasicGameObject soulIcon;
	private Label soulLabel;

	private HealthBauble health;

	private SoulMeter soulMeter;
	private boolean smv = false;
	private int soulMax = 10;

	public void setNewSoulMax(int newMax) {
		soulMax = newMax;
		soulMeter.setProgress(1f);
		showSoulMeter(true);
	}

	public boolean isSoulMeterVisible() {
		return smv;
	}

	public void updateSoulMeter(int newSoulCount) {
		soulMeter.setProgress((float) ((float) newSoulCount / soulMax), true);
	}

	public void showSoulMeter(boolean fade) {
		smv = true;
		if (fade) {
			soulMeter.setVisible(true);
			soulMeter.setAlpha(0f);
			soulMeter.clearActions();
			soulMeter.addAction(Actions.fadeIn(.5f));
			
		}else soulMeter.setVisible(true);
	}

	public void hideSoulMeter(boolean fade) {
		smv = false;

		if (fade) {
			soulMeter.clearActions();
			soulMeter.setAlpha(1f);
			soulMeter.addAction(Actions.sequence(Actions.fadeOut(.5f), Actions.visible(false)));
		} else {
			soulMeter.setVisible(false);
		}

	}

	@Override
	public void addLayerSystems(GameState state) {
		state.shouldHaltUpdate(false);// does not hault the update of the under layers
	}

	@Override
	public void init(GameState state) {
		keyIcon = (Image) state.getUiLayer().getActor("key");
		keyLabel = (Label) state.getUiLayer().getActor("keyLabel");
		health = (HealthBauble) state.getUiLayer().getActor("health");
		health.setAlpha(.7f);

		soulIcon = (BasicGameObject) state.getUiLayer().getGameObject("soul");

		Animation<KyperSprite> a = state.getAnimation("soul");
		if (a == null)
			state.storeAnimation("soul", state.createGameAnimation("soul", .17f));

		AnimationController ac = new AnimationController();
		ac.setAnimation("soul", PlayMode.LOOP);

		soulIcon.addController(ac);

		soulLabel = (Label) state.getUiLayer().getActor("soulLabel");
		soulLabel.setAlignment(Align.center);

		soulMeter = (SoulMeter) state.getUiLayer().getGameObject("soulMeter");
		hideSoulMeter(false);

	}

	public void fadeIn() {
		GameState state = getState();
		state.getColor().a = 0f;
		state.clearActions();

		state.addAction(Actions.fadeIn(fadeinTime));
	}

	public void fadeOut() {
		GameState state = getState();
		state.clearActions();

		state.addAction(Actions.fadeOut(fadeinTime));
	}

	@Override
	public void update(GameState state, float delta) {

	}

	@Override
	public void dispose(GameState state) {

	}

	public void setSoulLabelText(String text) {
		if (soulLabel != null)
			soulLabel.setText(text);
	}

	public void updateSouls(int souls, boolean pulse) {
		setSoulLabelText("" + souls);
		if (pulse) {
			soulIcon.clearActions();
			soulIcon.setScale(1f);
			soulIcon.addAction(Actions.sequence(Actions.scaleTo(1.5f, 1.5f, .25f), Actions.scaleTo(1f, 1f, .25f)));
		}
		if (soulMeter.isVisible()) {
			updateSoulMeter(souls);
		}
	}

	public void setKeyLabelText(String text) {
		if (keyLabel != null)
			keyLabel.setText(text);
	}

	public void updateKeys(int keys, boolean pulse) {
		setKeyLabelText(":" + keys);
		if (pulse) {
			keyIcon.clearActions();
			keyIcon.setScale(1f);
			keyIcon.addAction(Actions.sequence(Actions.scaleTo(1.5f, 1.5f, .25f), Actions.scaleTo(1f, 1f, .25f)));
		}
	}

	public void updateHealth(float progress) {
		if (health != null)
			health.setProgress(progress);
	}

	public HealthBauble getHealth() {
		return health;
	}

}
