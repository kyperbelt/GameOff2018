package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kyperbox.objects.GameObject;

public class ProgressTexture extends GameObject {

	final String u_progress = "u_progress";
	float progress;
	ShaderProgram progressShader;
	Viewport view;

	public ProgressTexture() {
		progress = .5f;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = MathUtils.clamp(progress, 0, 1f);
		if (progressShader != null) {
			view = getGameLayer().getState().getGame().getView();
			if (!progressShader.isCompiled()) {
				System.out.println(progressShader.getLog());
			} else {
				progressShader.begin();
				setupShader();
				progressShader.end();
			}
		}
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		progressShader = getState().getShader("progress");
		setProgress(this.progress);
		// setSize(128, 128);
	}
	
	private void setupShader() {
		Sprite s = getRenderSprite();
		if (s != null) {
			float th = s.getTexture().getHeight();
			float ty = s.getRegionY();

			float h = s.getRegionHeight() / th;
			float y = (ty / th) + h * (1f - progress);// (getY()+(h*progress)) / view.getWorldHeight();

			
			progressShader.setUniformf(u_progress, (y));
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		if (progressShader != null && progressShader.isCompiled()) {
			ShaderProgram ps = batch.getShader(); // prev shader

			batch.setShader(progressShader);
			setupShader();
			
			super.draw(batch, parentAlpha);
			batch.setShader(ps);
		} else {
			// super.draw(batch, parentAlpha);
		}

		// super.draw(batch, parentAlpha);
	}

}
