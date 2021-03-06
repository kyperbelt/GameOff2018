package com.gameoff.game;

import com.gameoff.game.objects.Collectible;
import com.gameoff.game.objects.Fire;
import com.gameoff.game.objects.enemies.*;
import com.gameoff.game.objects.Hazard;
import com.gameoff.game.objects.HealthBauble;
import com.gameoff.game.objects.LavaPit;
import com.gameoff.game.objects.Pit;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.ProgressTexture;
import com.gameoff.game.objects.SoulMeter;
import com.gameoff.game.objects.SpikeWall;
import com.gameoff.game.objects.Spiker;
import com.gameoff.game.objects.Trigger;
import com.gameoff.game.objects.Wall;
import com.gameoff.game.objects.destructible.Destructible;
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
		
		game.registerGameObject("Destructible", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Destructible();
			}
		});
		
		game.registerGameObject("ProgressTexture", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new ProgressTexture();
			}
		});

		game.registerGameObject("Spiker", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Spiker();
			}
		});

		game.registerGameObject("SpikeWall", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new SpikeWall();
			}
		});

		game.registerGameObject("Fire", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new Fire();
			}
		});

		game.registerGameObject("LavaBurst", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new LavaPit();
			}
		});
		
		game.registerGameObject("HealthBauble", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new HealthBauble();
			}
		});

		game.registerGameObject("Scorpion", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new ScorpionEnemy(0);
			}
		});

		game.registerGameObject("ScorpionBig", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new ScorpionEnemy(1);
			}
		});

		game.registerGameObject("Worm", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new WormEnemy(0);
			}
		});

		game.registerGameObject("WormBig", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new WormEnemy(1);
			}
		});

		game.registerGameObject("Cherub", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new CherubEnemy(0);
			}
		});

		game.registerGameObject("CherubRapid", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new CherubEnemy(1);
			}
		});

		game.registerGameObject("CherubFast", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new CherubEnemy(2);
			}
		});
		
		game.registerGameObject("SoulMeter", new IGameObjectGetter() {
			@Override
			public GameObject getGameObject() {
				return new SoulMeter();
			}
		});

	}
	
}
