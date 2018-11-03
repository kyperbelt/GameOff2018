package com.gameoff.game.systems;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.objects.GameLayer.LayerCamera;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;

//TODO: will change this to a general layer system so that it can include targets and other points of interest for dynamic camera
public class PlayerCameraSystem extends ControlSpecificSystem {

	Viewport view;
	LayerCamera camera;
	Rectangle worldBounds;

	Vector2 campos;
	Vector2 futurePos;

	public PlayerCameraSystem(float x, float y, float width, float height) {
		super(PlayerControl.class);
		worldBounds = new Rectangle(x, y, width, height);
		campos = new Vector2();
		futurePos = new Vector2();
	}

	public PlayerCameraSystem(int width, int height) {
		this(0, 0, width, height);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		camera = getLayer().getCamera();
		camera.setCentered();
		view = getLayer().getState().getGame().getView();
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		// texel scale
//		float heightscale = view.getWorldHeight() / view.getScreenHeight();
//		float widthscale = view.getWorldWidth() / view.getScreenWidth();

		futurePos.set(camera.getPosition());

		for (int i = 0; i < objects.size; i++) {
			if(i == 0)
				futurePos.set(objects.get(i).getPosition());
			else {
				futurePos.lerp(objects.get(i).getPosition(), .5f);
			}
		}

		campos.lerp(futurePos, delta + delta);

		if (campos.x + camera.getXOffset() < worldBounds.x) {
			campos.x = worldBounds.x - camera.getXOffset();
		} else if (campos.x - camera.getXOffset() > worldBounds.x + worldBounds.width) {
			campos.x = worldBounds.x + worldBounds.width + camera.getXOffset();
		}

		if (campos.y + camera.getYOffset() < worldBounds.y) {
			campos.y = worldBounds.y - camera.getYOffset();
		} else if (campos.y - camera.getYOffset() > worldBounds.y + worldBounds.height) {
			// campos.y = world_bounds.y + world_bounds.height + cam.getYOffset();
		}
		camera.setPosition(campos.x, campos.y);

	}

	@Override
	public void added(GameObject object) {

	}

	@Override
	public void removed(GameObject object) {

	}

}
