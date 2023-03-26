package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.utils.IdTagValue;
import insane96mcp.survivalreimagined.module.movement.utils.MaterialSlowdown;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Label(name = "Terrain Slowdown", description = "Slowdown based off the terrain you're walking on. Custom Terrain Slowdown are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class TerrainSlowdown extends SRFeature {

	//TODO Prevent recalculating slowdown until player's on ground
	private static final UUID MATERIAL_SLOWDOWN_UUID = UUID.fromString("a849043f-b280-4789-bafd-5da8e8e1078e");

	public static final ArrayList<IdTagValue> CUSTOM_TERRAIN_SLOWDOWN_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:dirt_path", -0.10)
	));
	public static final ArrayList<IdTagValue> customTerrainSlowdown = new ArrayList<>();

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> materialOnSlowdownConfig;
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> materialInSlowdownConfig;

	//TODO Move to json
	private static final List<String> materialOnSlowdownDefault = List.of(
			"amethyst,0.1","bamboo_sapling,0","bamboo,0.2","barrier,0","buildable_glass,0","cactus,0","cake,0","clay,0.1","cloth_decoration,0","decoration,0","dirt,0","egg,0","explosive,0","glass,0","grass,0.05","heavy_metal,0","ice_solid,0.5","ice,0.35","leaves,0.2","metal,0","moss,0.1","nether_wood,0","piston,0","plant,0","powder_snow,0","replaceable_fireproof_plant,0","replaceable_plant,0","replaceable_water_plant,0","sand,0.1","sculk,0","shulker_shell,0","snow,0.1","sponge,0","stone,0","top_snow,0.1","vegetable,0","water_plant,0","web,0","wood,0","wool,0.15"
	);
	private static final List<String> materialInSlowdownDefault = List.of(
			"amethyst,0.15","bamboo_sapling,0.15","bamboo,0","barrier,0","buildable_glass,0","cactus,0","cake,0","clay,0","cloth_decoration,0","decoration,0","dirt,0","egg,0","explosive,0","glass,0","grass,0","heavy_metal,0","ice_solid,0","ice,0","leaves,0","metal,0","moss,0","nether_wood,0","piston,0","plant,0.15","powder_snow,0","replaceable_fireproof_plant,0.15","replaceable_plant,0.15","replaceable_water_plant,0.15","sand,0","sculk,0","shulker_shell,0","snow,0","sponge,0","stone,0","top_snow,0.1","vegetable,0","water_plant,0.15","web,0","wood,0","wool,0"
	);

	public static ArrayList<MaterialSlowdown> materialOnSlowdown;
	public static ArrayList<MaterialSlowdown> materialInSlowdown;

	public TerrainSlowdown(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		materialOnSlowdownConfig = this.getBuilder()
				.comment("Slowdown percentage when walking on certain materials. Material names are fixed and cannot be changed. Materials per block list here: https://docs.google.com/spreadsheets/d/1XZ2iTC4nqit_GxvKurRp8NpW3tNaeaZsvfv4EGPoQxw/edit?usp=sharing")
				.defineList("Material On Slowdown", materialOnSlowdownDefault, o -> o instanceof String);
		materialInSlowdownConfig = this.getBuilder()
				.comment("Slowdown percentage when walking in certain materials. Material names are fixed and cannot be changed. Materials per block list here: https://docs.google.com/spreadsheets/d/1XZ2iTC4nqit_GxvKurRp8NpW3tNaeaZsvfv4EGPoQxw/edit?usp=sharing")
				.defineList("Material In Slowdown", materialInSlowdownDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		materialOnSlowdown = MaterialSlowdown.parseStringList(materialOnSlowdownConfig.get());
		materialInSlowdown = MaterialSlowdown.parseStringList(materialInSlowdownConfig.get());
	}

	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		this.loadAndReadFile("custom_terrain_slowdown.json", customTerrainSlowdown, CUSTOM_TERRAIN_SLOWDOWN_DEFAULT, IdTagValue.LIST_TYPE);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase != TickEvent.Phase.START
				|| (event.player.tickCount + event.player.getId()) % 2 != 0
				|| !event.player.isOnGround())
			return;

		double onTerrainSlowdown = 0d;
		int blocks = 0;
		AABB bb = event.player.getBoundingBox();
		int mX = Mth.floor(bb.minX);
		int mY = Mth.floor(bb.minY);
		int mZ = Mth.floor(bb.minZ);
		for (int x2 = mX; x2 < bb.maxX; x2++) {
			for (int z2 = mZ; z2 < bb.maxZ; z2++) {
				BlockState state = event.player.level.getBlockState(new BlockPos(x2, event.player.position().y - 0.02d, z2));
				if (state.isAir())
					continue;
				double blockSlowdown = 0d;
				for (IdTagValue idTagValue : customTerrainSlowdown) {
					if (idTagValue.matchesBlock(state.getBlock())) {
						blockSlowdown = idTagValue.value;
						blocks++;
						break;
					}
				}
				if (blockSlowdown == 0d) {
					for (MaterialSlowdown materialOn : materialOnSlowdown) {
						if (materialOn.material.equals(state.getMaterial())) {
							blockSlowdown = materialOn.slowdown;
							blocks++;
							break;
						}
					}
				}
				onTerrainSlowdown += blockSlowdown;
			}
		}
		if (blocks != 0)
			onTerrainSlowdown /= blocks;

		double inTerrainSlowdown = 0d;
		blocks = 0;
		for (int x2 = mX; x2 < bb.maxX; x2++) {
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int z2 = mZ; z2 < bb.maxZ; z2++) {
					BlockState state = event.player.level.getBlockState(new BlockPos(x2, y2, z2));
					if (state.isAir())
						continue;
					double blockSlowdown = 0d;
					for (MaterialSlowdown materialIn : materialInSlowdown) {
						if (materialIn.material.equals(state.getMaterial())) {
							blockSlowdown = materialIn.slowdown;
							blocks++;
							break;
						}
					}
					inTerrainSlowdown += blockSlowdown;
				}
			}
		}
		if (blocks != 0)
			inTerrainSlowdown /= blocks;

		double slowdown = onTerrainSlowdown + inTerrainSlowdown;

		AttributeModifier modifier = event.player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(MATERIAL_SLOWDOWN_UUID);
		if (slowdown != 0d) {
			if (modifier == null) {
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, MATERIAL_SLOWDOWN_UUID, "material slowdown", -slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
			else if (modifier.getAmount() != -slowdown) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MATERIAL_SLOWDOWN_UUID);
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, MATERIAL_SLOWDOWN_UUID, "material slowdown", -slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
		}
		else {
			if (modifier != null) {
				event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MATERIAL_SLOWDOWN_UUID);
			}
		}
	}
}