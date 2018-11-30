package com.gameoff.game;

import com.kyperbox.input.GameInput;
import com.kyperbox.input.InputDefaults;

public class Inputs {

	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	
	public static final String ATTACK = "attack";
	public static final String TRANSFORM = "transform";
	public static final String DASH = "dash";
	public static final String INTERACT = "interact";
	public static final String SPECIAL = "special";
	
	
	public static final String MENU = "menu";
	
	public static void registerInputs(GameInput input) {
		InputDefaults.removeDefaults(input);
		
		input.registerInput(UP);
		input.registerInput(DOWN);
		input.registerInput(LEFT);
		input.registerInput(RIGHT);
		
		input.registerInput(ATTACK);
		input.registerInput(TRANSFORM);
		input.registerInput(DASH);
		input.registerInput(INTERACT);
		input.registerInput(SPECIAL);
		
		input.registerInput(MENU);
	}
	

}
