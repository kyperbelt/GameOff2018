package com.kyper.lvd.desktop;


public class AssetPacker {
	
	
	public static void main(String[] arg) {
		AutoPacking.pack("game", "image", "game");
		AutoPacking.pack("overlay", "image", "overlay");
		AutoPacking.pack("death", "image", "death");
	}
}
