package com.gameoff.game.objects.composition;

/**
 * able to drop loot
 * @author john
 *
 */
public interface Lootable {
	
	/**
	 * id of loot to drop
	 * @return
	 */
	public int loot();
	
	/*
	 * amount of times to drop loot
	 */
	public int lootAmount(); 
	
}
