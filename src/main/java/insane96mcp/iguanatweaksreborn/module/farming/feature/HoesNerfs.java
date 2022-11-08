package insane96mcp.iguanatweaksreborn.module.farming.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.utils.HoeStat;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Hoes Nerfs", description = "Slower Hoes and more fragile")
@LoadFeature(module = Modules.Ids.FARMING)
public class HoesNerfs extends ITFeature {

	private static final ResourceLocation DISABLED_HOES = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes");

	public static ArrayList<HoeStat> hoesStats = new ArrayList<>(Arrays.asList(
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:stone_hoe", 20, 3),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:iron_hoe", 15, 3),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:golden_hoe", 4),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:diamond_hoe", 10, 2),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:netherite_hoe", 6, 2),
			new HoeStat(IdTagMatcher.Type.ID, "minecraft:vulcanite_hoe", 15, 3)
	));

	public HoesNerfs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type hoesStatsListType = new TypeToken<ArrayList<HoeStat>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File hoesStatsFile = new File(jsonConfigFolder, "hoes_stats.json");
		if (!hoesStatsFile.exists()) {
			try {
				if (!hoesStatsFile.createNewFile()) {
					throw new Exception("File#createNewFile failed");
				}
				String json = gson.toJson(hoesStats, hoesStatsListType);
				Files.write(hoesStatsFile.toPath(), json.getBytes());
			}
			catch (Exception e) {
				LogHelper.error("Failed to create default Json %s: %s", FilenameUtils.removeExtension(hoesStatsFile.getName()), e.getMessage());
			}
		}

		hoesStats.clear();
		try {
			FileReader fileReader = new FileReader(hoesStatsFile);
			List<HoeStat> hoeStats = gson.fromJson(fileReader, hoesStatsListType);
			hoesStats.addAll(hoeStats);
		}
		catch (JsonSyntaxException e) {
			LogHelper.error("Parsing error loading Json %s: %s", FilenameUtils.removeExtension(hoesStatsFile.getName()), e.getMessage());
		}
		catch (Exception e) {
			LogHelper.error("Failed loading Json %s: %s", FilenameUtils.removeExtension(hoesStatsFile.getName()), e.getMessage());
		}
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

		if (!isHoeDisabled(event.getHeldItemStack().getItem()))
			return false;

		//noinspection ConstantConditions getPlayer can't be null as it's called from onHoeUse that checks if player's null
		hoe.hurtAndBreak(1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
		event.getPlayer().displayClientMessage(Component.translatable(Strings.Translatable.TOO_WEAK), true);
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
					event.getPlayer().getCooldowns().addCooldown(hoe.getItem(), hoeStat.cooldown);
				}
				if (hoeStat.damageOnTill > 1) {
					hoe.hurtAndBreak(hoeStat.damageOnTill - 1, event.getPlayer(), (player) -> player.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));
				}
				break;
			}
		}
	}

	private static boolean isHoeDisabled(Item item) {
		TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, DISABLED_HOES);
		//noinspection ConstantConditions
		return ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(item);
	}
}