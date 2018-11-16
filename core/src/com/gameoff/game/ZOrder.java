package com.gameoff.game;

/**
 * basic ZOrder constants to keep everything organized without magic numbers
 * <p>
 * FOREGROUND=10
 * <p>
 * PLAYER=30
 * <p>
 * BACKGROUND=100
 * 
 * @author john
 *
 */
public class ZOrder {

	// the numbers are spread out a bit so we can still order objects like
	// FOREGROUND +1 or FOREGROUND-1 ect.
	public static final int FOREGROUND = 10;// foreground layer
	public static final int PLAYER = 30; // player layer

  public static final int KEYHOLE = 48;
	public static final int DOORS = 50;
	public static final int FLOOR_TEXT = 61;
	public static final int BACKGROUND = 100; // background layer

}
