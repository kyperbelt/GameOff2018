package com.gameoff.game.objects.composition;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;

public class DropTable<T> {
	
	ArrayMap<T, Float> weightMap;
	float dropPool = 0f;
	
	public DropTable() {
		weightMap = new ArrayMap<T, Float>();
	}
	
	public void addDrop(T drop,float weight) {
		float w = Math.max(0f, weight);
		weightMap.put(drop, w);
		dropPool+=w;
	}
	
	public T getDrop() {
		float random = MathUtils.random(0,dropPool);
		T drop = null;
		
		float start = 0f;
		for (int i = 0; i < weightMap.size; i++) {
			float weight = weightMap.getValueAt(i);
			if(random >= start && random <= start+weight) {
				drop = weightMap.getKeyAt(i);
				break;
			}
			start+=weight;
		}
		
		return drop;
	}
	
	public void clear() {
		weightMap.clear();
	}

}
