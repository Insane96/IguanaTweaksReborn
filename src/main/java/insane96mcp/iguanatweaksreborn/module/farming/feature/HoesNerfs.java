package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.utils.HoeCooldown;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Slower Hoes and more fragile")
@LoadFeature(module = Modules.Ids.FARMING)
public class HoesNerfs extends Feature {

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldownsConfig;
	private static final List<String> hoesCooldownsDefault = Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15");

	public static ArrayList<HoeCooldown> hoesCooldowns;

	@Config
	@Label(name = "Disable Low Tier Hoes", description = "When true, Wooden and Stone Hoes will not be usable to till dirt and will be heavily damaged when trying to. The disabled hoes can be changed via the iguanatweaksreborn:disabled_hoes item tag")
	public static Boolean disableLowTierHoes = true;
	@Config
	@Label(name = "Hoes Damage On Use Multiplier", description = "When an hoe is used to till dirt it will lose this durability instead of 1. Set to 1 to disable.")
	public static Integer hoesDamageOnUseMultiplier = 3;

	public HoesNerfs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		hoesCooldownsConfig = this.getBuilder()
				.comment("A list of hoes and ticks that a hoe will go on cooldown. The format is modid:itemid,ticks. 20 ticks = 1 second. You can even use tags as #modid:tag,ticks.")
				.defineList("Hoes Cooldowns", hoesCooldownsDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		hoesCooldowns = parseHoesCooldowns(hoesCooldownsConfig.get());
	}

	public static ArrayList<HoeCooldown> parseHoesCooldowns(List<? extends String> list) {
		ArrayList<HoeCooldown> hoesCooldowns = new ArrayList<>();
		for (String line : list) {
			HoeCooldown hoeCooldown = HoeCooldown.parseLine(line);
			if (hoeCooldown != null)
				hoesCooldowns.add(hoeCooldown);
		}
		return hoesCooldowns;
	}

	@SubscribeEvent
	public void onHoeUse(BlockEvent.BlockToolModificationEvent event) {
		if (!this.isEnabled()
				|| event.getPlayer() == null
				|| (event.getPlayer() != null && event.getPlayer().level.isClientSide)
				|| event.isSimulated()
				|| event.getToolAction() != ToolActions.HOE_TILL
				|| event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true) == null)
			return;

		boolean isHoeDisabled = disabledHoes(event);
		if (!isHoeDisabled)
			harderTilling(event);
	}

	public boolean disabledHoes(BlockEvent.BlockToolModificationEvent event) {
		ItemStack hoe = event.getHeldItemStack();

		if (!disableLowTierHoes
				|| !isHoeDisabled(event.getHeldItemStack().getItem()))
			return false;

		//noinspection ConstantConditions getPlayer can't be null as it's called from onHoeUse that checks if player's null
		hoe.hurtAndBreak(1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		event.getPlayer().displayClientMessage(Component.translatable("This hoe is too weak to be used"), true);
		event.setCanceled(true);
		return true;
	}

	public void harderTilling(BlockEvent.BlockToolModificationEvent event) {
		ItemStack hoe = event.getHeldItemStack();
		//noinspection ConstantConditions getPlayer can't be null as it's called from onHoeUse that checks if player's null
		if (event.getPlayer().getCooldowns().isOnCooldown(hoe.getItem()))
			return;
		int cooldown = 0;
		for (HoeCooldown hoeCooldown : hoesCooldowns) {
			if (hoeCooldown.matchesItem(hoe.getItem(), null)) {
				cooldown = hoeCooldown.cooldown;
				break;
			}
		}
		if (hoesDamageOnUseMultiplier > 1)
			hoe.hurtAndBreak(hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		if (cooldown != 0)
			event.getPlayer().getCooldowns().addCooldown(hoe.getItem(), cooldown);
	}

	private static final ResourceLocation DISABLED_HOES = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes");

	private static boolean isHoeDisabled(Item item) {
		TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, DISABLED_HOES);
		//noinspection ConstantConditions
		ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
		return tag.contains(item);
	}
}