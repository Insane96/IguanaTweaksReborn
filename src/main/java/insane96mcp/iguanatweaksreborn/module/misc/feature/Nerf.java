package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Nerfs", description = "Various Nerfs")
public class Nerf extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> noSheepWoolConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> ironRequiresPlayerConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> noIceBoatConfig;

	public boolean noSheepWool = true;
	public boolean ironRequiresPlayer = true;
	public boolean noIceBoat = true;

	public Nerf(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		noSheepWoolConfig = ITCommonConfig.builder
				.comment("If true, sheep will no longer drop Wool on death.")
				.define("No Sheep Death Wool", this.noSheepWool);
		ironRequiresPlayerConfig = ITCommonConfig.builder
				.comment("If true, Iron golems will only drop Iron when killed by the player.")
				.define("Iron from Golems only when killed by Player", this.ironRequiresPlayer);
		noIceBoatConfig = ITCommonConfig.builder
				.comment("If true, boats will no longer go stupidly fast on ice.")
				.define("No Ice Boats", this.noIceBoat);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.noSheepWool = this.noSheepWoolConfig.get();
		this.ironRequiresPlayer = this.ironRequiresPlayerConfig.get();
		this.noIceBoat = this.noIceBoatConfig.get();
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event) {
		if (!this.isEnabled())
			return;

		if (this.noSheepWool && event.getEntityLiving() instanceof Sheep)
			event.getDrops().removeIf(itemEntity -> itemEntity.getItem().is(ItemTags.WOOL));

		if (this.ironRequiresPlayer && event.getEntityLiving() instanceof IronGolem && !(event.getSource().getDirectEntity() instanceof Player))
			event.getDrops().removeIf(itemEntity -> itemEntity.getItem().is(Items.IRON_INGOT));
	}

	public float getBoatFriction(float glide) {
		return this.noIceBoat ? 0.45f : glide;
	}
}