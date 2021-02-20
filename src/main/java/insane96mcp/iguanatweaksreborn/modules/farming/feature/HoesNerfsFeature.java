package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.HoeCooldown;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Hoes nerfs")
public class HoesNerfsFeature extends ITFeature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldownsConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableLowTierHoesConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> hoesDamageOnUseMultiplierConfig;

	private static final List<String> hoesCooldownsDefault = Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15");

	public ArrayList<HoeCooldown> hoesCooldowns;
	public boolean disableLowTierHoes;
	public int hoesDamageOnUseMultiplier;

	public HoesNerfsFeature(ITModule module) {
		super(module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hoesCooldownsConfig = Config.builder
				.comment("A list of hoes and ticks that a hoe will go on cooldown. The format is modid:itemid,ticks. 20 ticks = 1 second. You can even use tags as #modid:tag,ticks.")
				.defineList("Hoes Cooldowns", hoesCooldownsDefault, o -> o instanceof String);
		disableLowTierHoesConfig = Config.builder
				.comment("When true, Wooden Hoes will not be usable and will be heavily damaged when trying to. The list of \"unusable\" hoes can be changed with datapacks by changing the iguanatweaksreborn:disabled_hoes tag")
				.define("Disable Low Tier Hoes", true);
		hoesDamageOnUseMultiplierConfig = Config.builder
				.comment("When an hoe is used it will lose this durability instead of 1. Set to 1 to disable")
				.defineInRange("Hoes Damage On Use Multiplier", 3, 1, 1024);
		Config.builder.pop();
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
	//TODO Replace with PlayerInteractEvent.RightClickBlock
	public void disabledHoes(BlockEvent.BlockToolInteractEvent event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().world.isRemote)
			return;
		if (!this.disableLowTierHoes)
			return;
		if (!isHoeDisabled(event.getHeldItemStack().getItem()))
			return;
		ItemStack hoe = event.getHeldItemStack();
		hoe.damageItem(hoe.getMaxDamage() - 1, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getPlayer().getActiveHand()));
		event.getPlayer().sendStatusMessage(new StringTextComponent("This hoe is too weak to be used"), true);
		event.setCanceled(true);
	}

	@SubscribeEvent
	//TODO Replace with PlayerInteractEvent.RightClickBlock
	public void harderTilling(BlockEvent.BlockToolInteractEvent event) {
		if (!this.isEnabled())
			return;
		if (event.getPlayer().world.isRemote)
			return;
		if (isHoeDisabled(event.getHeldItemStack().getItem()) && disableLowTierHoes)
			return;
		if (/*event.getFace() == Direction.DOWN || */!event.getWorld().isAirBlock(event.getPos().up()))
			return;
		BlockState blockstate = HoeItem.HOE_LOOKUP.get(event.getWorld().getBlockState(event.getPos()).getBlock());
		if (blockstate == null || blockstate.getBlock() != Blocks.FARMLAND)
			return;
		ItemStack stack = event.getHeldItemStack();
		if (!(stack.getItem() instanceof HoeItem))
			return;
		if (event.getPlayer().getCooldownTracker().hasCooldown(stack.getItem()))
			return;
		int cooldown = 0;
		for (HoeCooldown hoeCooldown : this.hoesCooldowns) {
			if (MCUtils.isInTagOrItem(hoeCooldown, stack.getItem(), null)) {
				cooldown = hoeCooldown.cooldown;
			}
		}

		if (this.hoesDamageOnUseMultiplier > 1)
			stack.damageItem(this.hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getPlayer().getActiveHand()));

		event.getPlayer().getCooldownTracker().setCooldown(stack.getItem(), cooldown);
	}

	private static boolean isHoeDisabled(Item item) {
		return item.getTags().contains(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes"));
	}
}
