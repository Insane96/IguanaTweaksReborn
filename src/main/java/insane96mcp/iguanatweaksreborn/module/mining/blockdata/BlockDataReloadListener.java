package insane96mcp.iguanatweaksreborn.module.mining.blockdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.BlockDataSync;
import insane96mcp.iguanatweaksreborn.utils.ITRLogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class BlockDataReloadListener extends SimpleJsonResourceReloadListener {
	public static List<BlockData> DATA = new ArrayList<>();
	public static List<BlockData> ORIGINAL_DATA = new ArrayList<>();
	public static final BlockDataReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();
	public BlockDataReloadListener() {
		super(GSON, "block_data");
	}

	static {
		INSTANCE = new BlockDataReloadListener();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		//Restore original data
		restoreOriginalDataAndClear();
		//Load new data
		DATA.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				BlockData blockData = GSON.fromJson(entry.getValue(), BlockData.class);
				//Serializer can return null in case the block doesn't exist (e.g. from other optional mods)
				if (blockData == null)
					return;
				blockData.apply(false);
				DATA.add(blockData);
			}
			catch (JsonSyntaxException e) {
				ITRLogHelper.error("Parsing error loading Block Data %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				ITRLogHelper.error("Failed loading Block Data %s: %s", entry.getKey(), e.getMessage());
			}
		}

		ITRLogHelper.info("Loaded %s Block Data", DATA.size());
	}

	public static void restoreOriginalDataAndClear() {
		for (BlockData data : ORIGINAL_DATA)
			data.apply(true);
		ITRLogHelper.info("Restored %s Block Data", ORIGINAL_DATA.size());
		ORIGINAL_DATA.clear();
	}

	@SubscribeEvent
	public static void onDataPackSync(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null) {
			event.getPlayerList().getPlayers().forEach(player -> BlockDataSync.sync(DATA, player));
		}
		else {
			BlockDataSync.sync(DATA, event.getPlayer());
		}
	}

	/*@SubscribeEvent
	public static void onDataPackSync(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null) {
			event.getPlayerList().getPlayers().forEach(player -> ItemStatisticsSync.sync(STATS, player));
		}
		else {
			ItemStatisticsSync.sync(STATS, event.getPlayer());
		}
	}


	@SubscribeEvent
	public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			for (ItemStatistics itemStatistics : BlockDataReloadListener.STATS) {
				itemStatistics.applyStats(true);
			}
		}
	}*/

	@Override
	public String getName() {
		return "Block Data Reload Listener";
	}
}
