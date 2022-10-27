package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.farming.utils.HoeCooldown;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Slower Hoes and more fragile")
public class HoesNerfs extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldownsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableLowTierHoesConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> hoesDamageOnUseMultiplierConfig;

	private static final List<String> hoesCooldownsDefault = Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15");

	public ArrayList<HoeCooldown> hoesCooldowns;
	public boolean disableLowTierHoes = true;
	public int hoesDamageOnUseMultiplier = 3;

	public HoesNerfs(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		hoesCooldownsConfig = ITCommonConfig.builder
				.comment("A list of hoes and ticks that a hoe will go on cooldown. The format is modid:itemid,ticks. 20 ticks = 1 second. You can even use tags as #modid:tag,ticks.")
				.defineList("Hoes Cooldowns", hoesCooldownsDefault, o -> o instanceof String);
		disableLowTierHoesConfig = ITCommonConfig.builder
				.comment("When true, Wooden and Stone Hoes will not be usable to till dirt and will be heavily damaged when trying to. The list of \"unusable\" hoes can be changed with datapacks by changing the iguanatweaksreborn:disabled_hoes tag")
				.define("Disable Low Tier Hoes", disableLowTierHoes);
		hoesDamageOnUseMultiplierConfig = ITCommonConfig.builder
				.comment("When an hoe is used to till dirt it will lose this durability instead of 1. Set to 1 to disable")
				.defineInRange("Hoes Damage On Use Multiplier", hoesDamageOnUseMultiplier, 1, 1024);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hoesCooldowns = parseHoesCooldowns(this.hoesCooldownsConfig.get());
		this.disableLowTierHoes = this.disableLowTierHoesConfig.get();
		this.hoesDamageOnUseMultiplier = this.hoesDamageOnUseMultiplierConfig.get();
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
		if (!this.isEnabled())
			return;
		if (event.getPlayer() == null || (event.getPlayer() != null && event.getPlayer().level.isClientSide))
			return;
		if (event.isSimulated())
			return;
		if (event.getToolAction() != ToolActions.HOE_TILL)
			return;
		if (event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true) == null)
			return;

		boolean isHoeDisabled = disabledHoes(event);
		if (!isHoeDisabled)
			harderTilling(event);
	}

	public boolean disabledHoes(BlockEvent.BlockToolModificationEvent event) {
		ItemStack hoe = event.getHeldItemStack();

		if (!this.disableLowTierHoes)
			return false;
		if (!isHoeDisabled(event.getHeldItemStack().getItem()))
			return false;

		hoe.hurtAndBreak(1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		event.getPlayer().displayClientMessage(Component.translatable("This hoe is too weak to be used"), true);
		event.setCanceled(true);
		return true;
	}

	public void harderTilling(BlockEvent.BlockToolModificationEvent event) {
		ItemStack hoe = event.getHeldItemStack();
		if (event.getPlayer().getCooldowns().isOnCooldown(hoe.getItem()))
			return;
		int cooldown = 0;
		for (HoeCooldown hoeCooldown : this.hoesCooldowns) {
			if (hoeCooldown.matchesItem(hoe.getItem(), null)) {
				cooldown = hoeCooldown.cooldown;
				break;
			}
		}
		if (this.hoesDamageOnUseMultiplier > 1)
			hoe.hurtAndBreak(this.hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		if (cooldown != 0)
			event.getPlayer().getCooldowns().addCooldown(hoe.getItem(), cooldown);
	}

	private static final ResourceLocation DISABLED_HOES = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes");

	private static boolean isHoeDisabled(Item item) {
		TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, DISABLED_HOES);
		ITag<Item> itemTag = ForgeRegistries.ITEMS.tags().getTag(tagKey);
		return itemTag.contains(item);
	}
}