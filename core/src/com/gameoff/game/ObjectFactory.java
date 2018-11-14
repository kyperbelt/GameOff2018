package com.gameoff.game;

import com.gameoff.game.objects.*;
import com.gameoff.game.objects.enemies.SimpleEnemy;
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
				return new Player(0);
			}
		});
		
		game.registerGameObject("Wall", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Wall();
			}
		});

		game.registerGameObject("Pit", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Pit();
			}
		});
		
		game.registerGameObject("Hazard", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Hazard();
			}
		});
		
		game.registerGameObject("Collectible", new IGameObjectGetter() {
			
			@Override
			public GameObject getGameObject() {
				return new Collectible();
			}
		});
		
		game.registerGameObject("SimpleEnemy", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new SimpleEnemy();
			}
		});
		
		game.registerGameObject("Trigger", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Trigger();
			}
		});

	}
	
}
