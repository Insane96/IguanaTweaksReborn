package insane96mcp.survivalreimagined.module.misc;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;

@Label(name = "Nerfs", description = "Various Nerfs")
@LoadFeature(module = Modules.Ids.MISC)
public class Nerfs extends Feature {
	@Config
	@Label(name = "Iron from Golems only when killed by Player", description = "If true, Iron golems will only drop Iron when killed by the player.")
	public static Boolean ironRequiresPlayer = true;
	@Config
	@Label(name = "No Coordinates", description = "If true, renderDebugInfo is enabled by default. Requires a world restart")
	public static Boolean noCoordinates = true;
	@Config
	@Label(name = "Reduced Random Tick Speed", description = "If true, randomTickSpeed is set to 2 from 3")
	public static Boolean reducedRandomTickSpeed = true;
	@Config
	@Label(name = "Reduced mob cramming", description = "If true, maxEntityCramming game rule is set to 6 from 24")
	public static Boolean reducedMobCramming = true;
	@Config
	@Label(name = "Less burn time for Kelp block", description = "Kelp blocks smelt 16 items instead of 20")
	public static Boolean lessBurnTimeForKelpBlock = true;

	@Config
	@Label(name = "Remove Falling Block Dupe across dimensions", description = "Prevents duping falling blocks when they travel across dimensions")
	public static Boolean removeFallingBlockDupe = true;
	@Config
	@Label(name = "Remove piston physics exploit", description = "Fixes several piston physics exploits like TNT duping")
	public static Boolean removePistonPhysicsExploit = true;

	@Config(min = 0d, max = 1d)
	@Label(name = "Fishing has a chance to fish a guardian")
	public static Double fishingCreatureChance = 0.05d;

    public Nerfs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static boolean isFallingBlockDupeRemoved() {
		return isEnabled(Nerfs.class) && removeFallingBlockDupe;
	}

	public static boolean isPistonPhysicsExploitEnabled() {
		return isEnabled(Nerfs.class) && removePistonPhysicsExploit;
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event) {
		if (!this.isEnabled())
			return;

		if (ironRequiresPlayer && event.getEntity() instanceof IronGolem && !(event.getSource().getDirectEntity() instanceof Player))
			event.getDrops().removeIf(itemEntity -> itemEntity.getItem().is(Items.IRON_INGOT));
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		if (!this.isEnabled())
			return;

		if (noCoordinates && FMLLoader.isProduction())
			event.getServer().getGameRules().getRule(GameRules.RULE_REDUCEDDEBUGINFO).set(true, event.getServer());
		if (reducedRandomTickSpeed)
			event.getServer().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(2, event.getServer());
		if (reducedMobCramming)
			event.getServer().getGameRules().getRule(GameRules.RULE_MAX_ENTITY_CRAMMING).set(6, event.getServer());
	}

	@SubscribeEvent
	public void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		if (!this.isEnabled()
				|| !lessBurnTimeForKelpBlock
				|| !event.getItemStack().is(Items.DRIED_KELP_BLOCK))
			return;

		event.setBurnTime(3200);
	}

	@SubscribeEvent
	public void onRetrieveBobber(ItemFishedEvent event) {
		if (!this.isEnabled()
				|| fishingCreatureChance == 0d
				|| event.getHookEntity().level().random.nextFloat() > fishingCreatureChance)
			return;

		Guardian guardian = EntityType.GUARDIAN.create(event.getHookEntity().level());
		guardian.setPos(event.getHookEntity().position().add(0, guardian.getBbHeight(), 0));
		Player player = event.getHookEntity().getPlayerOwner();
		double d0 = player.getX() - event.getHookEntity().getX();
		double d1 = player.getY() - event.getHookEntity().getY();
		double d2 = player.getZ() - event.getHookEntity().getZ();
		guardian.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
		event.getHookEntity().level().addFreshEntity(guardian);
		event.setCanceled(true);
	}
}