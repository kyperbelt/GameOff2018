package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.kyperbox.objects.GameObject;

public class ProgressTexture extends GameObject {

	float progress;
	ShaderProgram progressShader;

	public ProgressTexture() {
			progress = 1f;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = MathUtils.clamp(progress, 0, 1f);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		progressShader = getState().getShader("progress");
		//setSize(128, 128);
		setSprite("circle_0");
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		if (progressShader != null && progressShader.isCompiled()) {
			ShaderProgram ps = batch.getShader(); //prev shader
			
			batch.setShader(progressShader);
			progressShader.setUniformf("u_progress", progress);
			progressShader.setUniformf("u_height",getHeight());
		
			super.draw(batch, parentAlpha);
			batch.setShader(ps);
		}else {
			//super.draw(batch, parentAlpha);
		}
		
		//super.draw(batch, parentAlpha);
	}

}
