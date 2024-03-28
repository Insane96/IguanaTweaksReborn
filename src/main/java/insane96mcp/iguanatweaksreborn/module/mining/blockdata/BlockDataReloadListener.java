package insane96mcp.iguanatweaksreborn.module.mining.blockdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.utils.ITRLogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class BlockDataReloadListener extends SimpleJsonResourceReloadListener {
	public static List<BlockData> STATS = new ArrayList<>();
	public static final BlockDataReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();
	public BlockDataReloadListener() {
		super(GSON, "block_data");
	}

	static {
		INSTANCE = new BlockDataReloadListener();
	}

	//TODO Save original data here
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		STATS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				BlockData blockData = GSON.fromJson(entry.getValue(), BlockData.class);
				blockData.apply();
				STATS.add(blockData);
			}
			catch (JsonSyntaxException e) {
				ITRLogHelper.error("Parsing error loading Block Data %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				ITRLogHelper.error("Failed loading Block Data %s: %s", entry.getKey(), e.getMessage());
			}
		}

		ITRLogHelper.info("Loaded %s Block Data", STATS.size());
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
