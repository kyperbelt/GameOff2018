package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.kyperbox.objects.*;

public class BackgroundImage extends GameObject {


  public BackgroundImage() {}

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    if (properties != null)
    {
      setSprite(properties.get("image","back1",String.class));
    }
  }
}
