package com.gameoff.game;

import com.gameoff.game.objects.Hazard;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Wall;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.IGameObjectGetter;

/**
 * just a utility class used to keep all of our objects organized.
 * These are objects that dont come with kyperbox framework and must be added to 
 * the framework so that they are able to be dynamically loaded.
 * @author john
 *
 */
public class ObjectFactory {
	
	public static void createObjectGetters(GameOffGame game) {
		
		game.registerGameObject("Player", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Player();
			}
		});
		
		game.registerGameObject("Wall", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Wall();
			}
		});
		
		game.registerGameObject("Hazard", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Hazard();
			}
		});
	}
	
	

}
