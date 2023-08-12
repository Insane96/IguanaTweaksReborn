package insane96mcp.survivalreimagined.module.experience.anvils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnvilRecipeReloadListener extends SimpleJsonResourceReloadListener {
	public static List<AnvilRecipe> RECIPES = new ArrayList<>();
	public static final AnvilRecipeReloadListener INSTANCE;
	private static final Gson GSON = new GsonBuilder().create();
	public AnvilRecipeReloadListener() {
		super(GSON, "anvil_recipes");
	}

	static {
		INSTANCE = new AnvilRecipeReloadListener();
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		RECIPES.clear();
		for (var entry : map.entrySet()) {
			try {
				ResourceLocation name = entry.getKey();
				LogHelper.debug("Loading Anvil Recipe %s", entry.getKey());
				String[] split = name.getPath().split("/");
				if (split[split.length - 1].startsWith("_"))
					continue;

				AnvilRecipe mob = GSON.fromJson(entry.getValue(), AnvilRecipe.class);
				RECIPES.add(mob);
				LogHelper.debug("Loaded Anvil Recipe %s", entry.getKey());
			}
			catch (JsonSyntaxException e) {
				LogHelper.error("Parsing error loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
			}
			catch (Exception e) {
				LogHelper.error("Failed loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
			}
		}

		LogHelper.debug("Loaded %s Anvil Recipe(s)", RECIPES.size());
	}

	@Override
	public String getName() {
		return "Anvil Recipe Reload Listener";
	}
}
