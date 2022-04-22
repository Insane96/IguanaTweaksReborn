package insane96mcp.iguanatweaksreborn.module.movement.feature;

import insane96mcp.iguanatweaksreborn.module.movement.utils.MaterialSlowdown;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Label(name = "Terrain Slowdown", description = "Slowdown based off the terrain you're walking on.")
public class TerrainSlowdown extends Feature {

	private final UUID MATERIAL_SLOWDOWN_UUID = UUID.fromString("a849043f-b280-4789-bafd-5da8e8e1078e");

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> materialOnSlowdownConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> materialInSlowdownConfig;
	//private final ForgeConfigSpec.ConfigValue<List<? extends String>> customTerrainSlowdownConfig;

	private final List<String> materialOnSlowdownDefault = Arrays.asList(
			"amethyst,0.1","bamboo_sapling,0","bamboo,0.1","barrier,0","buildable_glass,0","cactus,0","cake,0","clay,0.15","cloth_decoration,0","decoration,0","dirt,0.1","egg,0","explosive,0","glass,0","grass,0.1","heavy_metal,0","ice_solid,0.5","ice,0.35","leaves,0.15","metal,0","moss,0.15","nether_wood,0","piston,0","plant,0","powder_snow,0","replaceable_fireproof_plant,0","replaceable_plant,0","replaceable_water_plant,0","sand,0.15","sculk,0","shulker_shell,0","snow,0.2","sponge,0","stone,0","top_snow,0","vegetable,0","water_plant,0","web,0","wood,0","wool,0.2"
	);
	private final List<String> materialInSlowdownDefault = Arrays.asList(
			"amethyst,0.1","bamboo_sapling,0.15","bamboo,0","barrier,0","buildable_glass,0","cactus,0","cake,0","clay,0","cloth_decoration,0","decoration,0","dirt,0","egg,0","explosive,0","glass,0","grass,0","heavy_metal,0","ice_solid,0","ice,0","leaves,0","metal,0","moss,0","nether_wood,0","piston,0","plant,0.15","powder_snow,0","replaceable_fireproof_plant,0.1","replaceable_plant,0.1","replaceable_water_plant,0.1","sand,0","sculk,0","shulker_shell,0","snow,0","sponge,0","stone,0","top_snow,0.1","vegetable,0","water_plant,0.1","web,0","wood,0","wool,0"
	);

	public ArrayList<MaterialSlowdown> materialOnSlowdown;
	public ArrayList<MaterialSlowdown> materialInSlowdown;

	public TerrainSlowdown(Module module) {
		super(Config.builder, module);
		this.pushConfig(Config.builder);
		materialOnSlowdownConfig = Config.builder
				.comment("Slowdown percentage when walking on certain materials. Material names are fixed and cannot be changed. Materials per block list here: https://docs.google.com/spreadsheets/d/1XZ2iTC4nqit_GxvKurRp8NpW3tNaeaZsvfv4EGPoQxw/edit?usp=sharing")
				.defineList("Material On Slowdown", this.materialOnSlowdownDefault, o -> o instanceof String);
		materialInSlowdownConfig = Config.builder
				.comment("Slowdown percentage when walking in certain materials. Material names are fixed and cannot be changed. Materials per block list here: https://docs.google.com/spreadsheets/d/1XZ2iTC4nqit_GxvKurRp8NpW3tNaeaZsvfv4EGPoQxw/edit?usp=sharing")
				.defineList("Material In Slowdown", this.materialInSlowdownDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.materialOnSlowdown = MaterialSlowdown.parseStringList(this.materialOnSlowdownConfig.get());
		this.materialInSlowdown = MaterialSlowdown.parseStringList(this.materialInSlowdownConfig.get());
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled())
			return;

		if (event.phase != TickEvent.Phase.START)
			return;

		//if (event.player.tickCount % 5 != 0)
			//return;

		double slowdown = 0d;
		BlockState stateWalked = event.player.level.getBlockState(event.player.blockPosition().below());
		for (MaterialSlowdown materialOn : materialOnSlowdown) {
			if (materialOn.material.equals(stateWalked.getMaterial())) {
				slowdown += materialOn.slowdown;
				break;
			}
		}
		BlockState stateIn = event.player.level.getBlockState(event.player.blockPosition());
		for (MaterialSlowdown materialIn : materialInSlowdown) {
			if (materialIn.material.equals(stateIn.getMaterial())) {
				slowdown += materialIn.slowdown;
				break;
			}
		}

		AttributeModifier modifier = event.player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(MATERIAL_SLOWDOWN_UUID);
		if (slowdown != 0d) {
			if (modifier == null) {
				MCUtils.applyModifier(event.player, Attributes.MOVEMENT_SPEED, MATERIAL_SLOWDOWN_UUID, "material slowdown", -slowdown, AttributeModifier.Operation.MULTIPLY_BASE, false);
			}
			else if (modifier.getAmount() != slowdown) {
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