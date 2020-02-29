package insane96mcp.iguanatweaksreborn.utils;

import java.util.Random;

public class RandomHelper {
	public static int getInt(Random rand, int min, int max) {
		return rand.nextInt(max - min) + min;
	}

	public static float getFloat(Random rand, float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}

	public static double getFloat(Random rand, double min, double max) {
		return rand.nextFloat() * (max - min) + min;
	}
}