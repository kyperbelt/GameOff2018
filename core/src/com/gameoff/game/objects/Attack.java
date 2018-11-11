package com.gameoff.game.objects;

import com.gameoff.game.control.HealthControl.HealthGroup;

public class Attack extends Basic {

	// who does this damage
	HealthGroup[] damageGroup;

	public void setDamageGroup(HealthGroup... damageGroup) {
		this.damageGroup = damageGroup;
	}

	/**
	 * check if this attack damages the specified healthgroup if there is no damage
	 * group then this does damage to everything
	 * 
	 * @param group
	 * @return
	 */
	public boolean damages(HealthGroup group) {
		if(damageGroup == null || damageGroup.length == 0)
			return true;
		for (int i = 0; i < damageGroup.length; i++) {
			if (damageGroup[i] == group)
				return true;
		}
		return false;
	}

}
