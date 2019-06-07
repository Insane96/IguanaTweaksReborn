package net.insane96mcp.iguanatweaks.mapgen;

import net.minecraft.world.gen.structure.MapGenVillage;

public class MapGenNoVillage extends MapGenVillage {
	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		return false;
	}
}
