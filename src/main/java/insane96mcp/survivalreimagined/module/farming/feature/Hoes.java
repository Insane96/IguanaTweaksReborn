package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.data.HoeStat;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Label(name = "Hoes", description = "Slower Hoes and more fragile. Hoes Properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class Hoes extends SRFeature {

	public static final String TOO_WEAK = "survivalreimagined.weak_hoe";
	public static final String TILL_COOLDOWN = "survivalreimagined.till_cooldown";
	public static final String SCYTHE_RADIUS = "survivalreimagined.scythe_radius";
	public static final TagKey<Item> DISABLED_HOES = SRItemTagsProvider.create("disabled_hoes");

	public static final ArrayList<HoeStat> HOES_STATS_DEFAULT = new ArrayList<>(Arrays.asList(
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:wooden_hoe", 40, 4, 0),
			new HoeStat(IdTagMatcher.Type.ID, "survivalreimagined:flint_hoe", 30, 4, 1),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:stone_hoe", 18, 4, 1),
			new HoeStat(IdTagMatcher.Type.ID, "survivalreimagined:copper_hoe", 10, 4, 2),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:iron_hoe", 20, 4, 2),
			new HoeStat(IdTagMatcher.Type.ID, "survivalreimagined:durium_hoe", 18, 4, 2),
			new HoeStat(IdTagMatcher.Type.ID, "survivalreimagined:soul_steel_hoe", 15, 3, 2),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:golden_hoe", 5, 1, 0),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:diamond_hoe", 15, 3, 3),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:netherite_hoe", 10, 2, 3)
	));

	public static final ArrayList<HoeStat> hoesStats = new ArrayList<>();

	@Config(min = 0)
	@Label(name = "Efficiency cooldown reduction", description = "Each Efficiency level reduces the cooldown of hoes by this many ticks.")
	public static Integer efficiencyCooldownReduction = 1;

	public Hoes(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("hoes_stats.json", hoesStats, HOES_STATS_DEFAULT, HoeStat.LIST_TYPE));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	@SubscribeEvent
	public void onHoeUse(BlockEvent.BlockToolModificationEvent event) {
		if (!this.isEnabled()
				|| event.getPlayer() == null
				|| event.isSimulated()
				|| event.getToolAction() != ToolActions.HOE_TILL)
			return;

		boolean isHoeDisabled = disabledHoes(event);
		if (event.getPlayer() != null && event.getPlayer().level.isClientSide)
			return;
		BlockState finalState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
		if (finalState == null || !finalState.is(Blocks.FARMLAND))
			return;
		if (!isHoeDisabled)
			harderTilling(event);
	}

	public boolean disabledHoes(BlockEvent.BlockToolModificationEvent event) {
		if (!isHoeDisabled(event.getHeldItemStack().getItem()))
			return false;

		//noinspection ConstantConditions getPlayer can't be null as it's called from onHoeUse that checks if player's null
		event.getPlayer().displayClientMessage(Component.translatable(TOO_WEAK), true);
		event.setCanceled(true);
		return true;
	}

	public void harderTilling(BlockEvent.BlockToolModificationEvent event) {
		ItemStack hoe = event.getHeldItemStack();
		//noinspection ConstantConditions getPlayer can't be null as it's called from onHoeUse that checks if player's null
		if (event.getPlayer().getCooldowns().isOnCooldown(hoe.getItem()))
			return;
		for (HoeStat hoeStat : hoesStats) {
			if (hoeStat.matchesItem(hoe.getItem(), null)) {
				if (hoeStat.cooldown > 0) {
					int efficiency = hoe.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
					int cooldown = hoeStat.cooldown - (efficiency * efficiencyCooldownReduction);
					if (cooldown > 0)
						event.getPlayer().getCooldowns().addCooldown(hoe.getItem(), cooldown);
				}
				if (hoeStat.damageOnTill > 1) {
					hoe.hurtAndBreak(hoeStat.damageOnTill - 1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
				}
				break;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled()
				|| !event.getState().getMaterial().isReplaceable())
			return;
		for (HoeStat hoeStat : hoesStats) {
			if (hoeStat.matchesItem(event.getPlayer().getMainHandItem().getItem(), null) && hoeStat.scytheRadius > 0) {
				BlockPos.betweenClosedStream(event.getPos().offset(-hoeStat.scytheRadius, -(hoeStat.scytheRadius - 1), -hoeStat.scytheRadius), event.getPos().offset(hoeStat.scytheRadius, hoeStat.scytheRadius - 1, hoeStat.scytheRadius))
						.forEach(pos -> {
							BlockState state = event.getPlayer().level.getBlockState(pos);
							if (!state.getMaterial().isReplaceable()
									|| !state.getFluidState().isEmpty()
									|| pos.equals(event.getPos()))
								return;
							event.getPlayer().getLevel().destroyBlock(pos, false);
						});
				break;
			}
		}
	}

	private static boolean isHoeDisabled(Item item) {
		return Utils.isItemInTag(item, DISABLED_HOES);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled())
			return;

		if (Utils.isItemInTag(event.getItemStack().getItem(), DISABLED_HOES)) {
			event.getToolTip().add(Component.translatable(TOO_WEAK).withStyle(ChatFormatting.RED));
		}
		else {
			for (HoeStat hoeStat : hoesStats) {
				if (!hoeStat.matchesItem(event.getItemStack().getItem(), null)
						|| hoeStat.cooldown <= 0)
					continue;

				int efficiency = event.getItemStack().getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
				int cooldown = hoeStat.cooldown - (efficiency * efficiencyCooldownReduction);
				event.getToolTip().add(Component.literal(" ").append(Component.translatable(TILL_COOLDOWN, SurvivalReimagined.ONE_DECIMAL_FORMATTER.format(cooldown / 20f)).withStyle(ChatFormatting.DARK_GREEN)));
				if (hoeStat.scytheRadius > 0)
					event.getToolTip().add(Component.literal(" ").append(Component.translatable(SCYTHE_RADIUS, hoeStat.scytheRadius).withStyle(ChatFormatting.DARK_GREEN)));
				break;
			}
		}
	}
}