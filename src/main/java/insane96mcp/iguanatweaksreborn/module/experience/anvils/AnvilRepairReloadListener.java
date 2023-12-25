package insane96mcp.iguanatweaksreborn.module.experience.anvils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.SyncAnvilRepair;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
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
public class AnvilRepairReloadListener extends SimpleJsonResourceReloadListener {
	public static List<AnvilRepair> REPAIRS = new ArrayList<>();
	public static final AnvilRepairReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();
	public AnvilRepairReloadListener() {
		super(GSON, "anvil_repairs");
	}

	static {
		INSTANCE = new AnvilRepairReloadListener();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		REPAIRS.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				AnvilRepair mob = GSON.fromJson(entry.getValue(), AnvilRepair.class);
				REPAIRS.add(mob);
			}
			catch (JsonSyntaxException e) {
				LogHelper.error("Parsing error loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				LogHelper.error("Failed loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
			}
		}

		LogHelper.debug("Loaded %s Anvil Recipe(s)", REPAIRS.size());
	}

	@SubscribeEvent
	public static void onDataPackSync(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null) {
			event.getPlayerList().getPlayers().forEach(player -> SyncAnvilRepair.sync(REPAIRS, player));
		}
		else {
			SyncAnvilRepair.sync(REPAIRS, event.getPlayer());
		}
	}

	@Override
	public String getName() {
		return "Anvil Recipe Reload Listener";
	}
}
