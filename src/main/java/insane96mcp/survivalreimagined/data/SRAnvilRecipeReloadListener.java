package insane96mcp.survivalreimagined.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class SRAnvilRecipeReloadListener extends SimpleJsonResourceReloadListener {
    public static final List<SRAnvilRecipe> RECIPES = new ArrayList<>();
    public static final SRAnvilRecipeReloadListener INSTANCE;
    private static final Gson GSON = new GsonBuilder().create();

    public SRAnvilRecipeReloadListener() {
        super(GSON, "anvil_recipes");
    }

    static {
        INSTANCE = new SRAnvilRecipeReloadListener();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        RECIPES.clear();
        for (var entry : map.entrySet()) {
            try {
                SRAnvilRecipe recipe = GSON.fromJson(entry.getValue(), SRAnvilRecipe.class);
                RECIPES.add(recipe);
            }
            catch (JsonSyntaxException e) {
                LogHelper.error("Parsing error loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
            }
            catch (Exception e) {
                LogHelper.error("Failed loading Anvil Recipe %s: %s", entry.getKey(), e.getMessage());
            }
        }

        LogHelper.info("Loaded %s Anvil Recipe(s)", RECIPES.size());
    }

    @SubscribeEvent
    public static void anvilUpdateEvent(final AnvilUpdateEvent event)
    {
        for (SRAnvilRecipe anvilRecipe : SRAnvilRecipeReloadListener.RECIPES) {
            if (anvilRecipe.matches(event.getLeft(), event.getRight())) {
                event.setCost(0);
                event.setOutput(anvilRecipe.assemble(event.getLeft(), event.getRight()));
                event.setMaterialCost(anvilRecipe.amount);
            }
        }
    }

    @SubscribeEvent
    public static void anvilUpdateEvent(final AnvilRepairEvent event)
    {
        for (SRAnvilRecipe anvilRecipe : SRAnvilRecipeReloadListener.RECIPES) {
            if (anvilRecipe.matches(event.getLeft(), event.getRight()) && anvilRecipe.getChanceToBreak() != null) {
                event.setBreakChance(anvilRecipe.getChanceToBreak().floatValue());
            }
        }
    }
}
