package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class PlantsGrowthReloadListener extends SimpleJsonResourceReloadListener {
	public static List<PlantGrowthModifier> STATS = new ArrayList<>();
	public static final PlantsGrowthReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();
	public PlantsGrowthReloadListener() {
		super(GSON, "item_stats");
	}

	static {
		INSTANCE = new PlantsGrowthReloadListener();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		/*STATS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				PlantGrowthModifier itemStatistics = GSON.fromJson(entry.getValue(), PlantGrowthModifier.class);
				PlantGrowthModifier.applyStats(false);
				STATS.add(itemStatistics);
			}
			catch (JsonSyntaxException e) {
				ITRLogHelper.error("Parsing error loading Item Statistics %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				ITRLogHelper.error("Failed loading Item Statistics %s: %s", entry.getKey(), e.getMessage());
			}
		}

		ITRLogHelper.info("Loaded %s Item Statistics", STATS.size());*/
	}

	@SubscribeEvent
	public static void onDataPackSync(OnDatapackSyncEvent event) {
		/*if (event.getPlayer() == null) {
			event.getPlayerList().getPlayers().forEach(player -> ItemStatisticsSync.sync(STATS, player));
		}
		else {
			ItemStatisticsSync.sync(STATS, event.getPlayer());
		}*/
	}


	@SubscribeEvent
	public static void onTagsUpdatedEvent(TagsUpdatedEvent event) {
		/*if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			for (PlantGrowthModifier itemStatistics : PlantsGrowthReloadListener.STATS) {
				itemStatistics.applyStats(true);
			}
		}*/
	}

	@Override
	public String getName() {
		return "Plants Growth Reload Listener";
	}
}
