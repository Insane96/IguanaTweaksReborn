package insane96mcp.survivalreimagined.module.world;

import com.google.common.collect.Lists;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.ServerConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Label(name = "Seasons", description = "Change a few things relative to Serene Seasons")
@LoadFeature(module = Modules.Ids.WORLD)
public class Seasons extends Feature {

	@Config
	@Label(name = "Serene Seasons changes", description = "Normal glass no longer counts as greenhouse glass, saplings no longer grow in Winter and starting season is mid summer.")
	public static Boolean sereneSeasonsChanges = true;

	@Config
	@Label(name = "No Saplings in Winter", description = "Saplings no longer drop in Winter.")
	public static Boolean noSaplingsInWinter = true;

	@Config
	@Label(name = "Grass Decay and Growth", description = "Grass and tall grass decays in Winter and regrows back in Spring. Saplings are also transformed into Dead Bushes.")
	public static Boolean grassDecayAndGrowth = true;

	@Config
	@Label(name = "Serene Seasons changes", description = """
			Makes the following changes to Serene Seasons config:
			Makes the following changes to Serene Seasons config:
			* seasonal_crops is set to false, as it's controlled by PlantsGrowth
			* Sets the starting season to mid summer
			""")
	public static Boolean changeSereneSeasonsConfig = true;

	@Config
	@Label(name = "Season based fishing time")
	public static Boolean seasonBasedFishingTime = true;

	public Seasons(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "serene_seasons_changes", Component.literal("Survival Reimagined Serene Seasons Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && sereneSeasonsChanges));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_saplings_in_winter", Component.literal("Survival Reimagined No Saplings in Winter"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && noSaplingsInWinter));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		if (changeSereneSeasonsConfig) {
			FertilityConfig.seasonalCrops.set(false);
		}
	}

	@SubscribeEvent
	public void onServerStart(ServerStartedEvent event) {
		if (changeSereneSeasonsConfig) {
			ServerConfig.startingSubSeason.set(4);
			ServerConfig.progressSeasonWhileOffline.set(false);
		}
	}

	@SubscribeEvent
	public void onPreLevelTick(TickEvent.LevelTickEvent event) {
		if (!event.level.isClientSide && event.level.getGameTime() == 0 && changeSereneSeasonsConfig) {
			SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(event.level);
			seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * Season.SubSeason.EARLY_SUMMER.ordinal();
			//seasonData.seasonCycleTicks = event.level.random.nextInt(12) * SeasonTime.ZERO.getSubSeasonDuration();
			seasonData.setDirty();
			SeasonHandler.sendSeasonUpdate(event.level);
		}
	}

	static final Map<Season.SubSeason, Float> CHANCE_TO_GROW_OR_DECAY = Map.ofEntries(
			Map.entry(Season.SubSeason.EARLY_SUMMER, 0.05f),
			Map.entry(Season.SubSeason.MID_SUMMER, 0.025f),
			Map.entry(Season.SubSeason.LATE_SUMMER, 0.01f),
			Map.entry(Season.SubSeason.EARLY_AUTUMN, 0f),
			Map.entry(Season.SubSeason.MID_AUTUMN, 0f),
			Map.entry(Season.SubSeason.LATE_AUTUMN, -0.05f),
			Map.entry(Season.SubSeason.EARLY_WINTER, -0.10f),
			Map.entry(Season.SubSeason.MID_WINTER, -0.125f),
			Map.entry(Season.SubSeason.LATE_WINTER, -0.075f),
			Map.entry(Season.SubSeason.EARLY_SPRING, 0.05f),
			Map.entry(Season.SubSeason.MID_SPRING, 0.10f),
			Map.entry(Season.SubSeason.LATE_SPRING, 0.125f)
	);

	@SubscribeEvent
	public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!this.isEnabled()
				|| !grassDecayAndGrowth
				|| event.phase != TickEvent.Phase.END
				|| event.side != LogicalSide.SERVER)
            return;

        Season.SubSeason subSeason = SeasonHelper.getSeasonState(event.level).getSubSeason();
		float chance = CHANCE_TO_GROW_OR_DECAY.get(subSeason);
		if (chance == 0f)
			return;

        ServerLevel level = (ServerLevel)event.level;
		level.getProfiler().push("tallGrassRandomTick");
        ChunkMap chunkMap = level.getChunkSource().chunkMap;
        DistanceManager distanceManager = chunkMap.getDistanceManager();
        int naturalSpawnChunkCount = distanceManager.getNaturalSpawnChunkCount();
        List<ChunkAndHolder> list = Lists.newArrayListWithCapacity(naturalSpawnChunkCount);
        chunkMap.getChunks().forEach(chunkHolder -> {
            LevelChunk levelChunk = chunkHolder.getTickingChunk();
            if (levelChunk != null)
				list.add(new ChunkAndHolder(levelChunk, chunkHolder));
        });

        Collections.shuffle(list);
		for (ChunkAndHolder chunkAndHolder : list) {
			ChunkPos chunkPos = chunkAndHolder.chunk.getPos();
			if (level.shouldTickBlocksAt(chunkPos.toLong()) && (chunkMap.anyPlayerCloseEnoughForSpawning(chunkPos) || distanceManager.shouldForceTicks(chunkPos.toLong()))) {
				tickPlantsLifeDeath(chunkMap, chunkAndHolder.chunk, subSeason, chance / 10f);
			}
		}
		level.getProfiler().pop();
    }

	private static void tickPlantsLifeDeath(ChunkMap chunkMap, LevelChunk levelChunk, Season.SubSeason subSeason, float chance) {
		ServerLevel level = chunkMap.level;
		ChunkPos chunkpos = levelChunk.getPos();
		int x = chunkpos.getMinBlockX();
		int z = chunkpos.getMinBlockZ();

		LevelChunkSection[] levelChunkSections = levelChunk.getSections();
		for (int s = 0; s < levelChunkSections.length; s++) {
			LevelChunkSection levelchunksection = levelChunkSections[s];
			if (levelchunksection.isRandomlyTicking()) {
				int sectionY = levelChunk.getSectionYFromSectionIndex(s);
				int y = SectionPos.sectionToBlockCoord(sectionY);

				for (int t = 0; t < level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING); t++) {
					BlockPos pos = level.getBlockRandomPos(x, y, z, 15);
					BlockState state = levelchunksection.getBlockState(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
					BlockPos abovePos = pos.above();
					if (state.is(BlockTags.DIRT) && level.getBrightness(LightLayer.SKY, abovePos) >= 10) {
						BlockState stateUp = level.getBlockState(abovePos);
						if (chance < 0f && level.getRandom().nextFloat() < -chance) {
							if (stateUp.is(Blocks.FERN) || stateUp.is(Blocks.GRASS) || stateUp.is(Blocks.TALL_GRASS))
								level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 3);
							else if (stateUp.is(BlockTags.SAPLINGS))
								level.setBlock(abovePos, Blocks.DEAD_BUSH.defaultBlockState(), 3);
						}
						else if (state.is(Blocks.GRASS_BLOCK) && level.getRandom().nextFloat() < chance && stateUp.isAir()) {
							Optional<Holder.Reference<PlacedFeature>> oPlacedFeature = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);
							oPlacedFeature.ifPresent(placedFeatureReference ->
									placedFeatureReference.value().place(level, level.getChunkSource().getGenerator(), level.random, abovePos));
						}
					}
				}
			}
		}
	}

	record ChunkAndHolder(LevelChunk chunk, ChunkHolder holder) {}

	public static boolean shouldSlowdownFishing(Level level) {
		if (!Feature.isEnabled(Seasons.class)
				|| !seasonBasedFishingTime)
			return false;

		Season season = SeasonHelper.getSeasonState(level).getSeason();
		//Chance to slowdown fishing
		float rng = switch (season) {
			case SPRING -> 0.1F;
			case SUMMER -> 0.0F;
			case AUTUMN -> 0.2F;
			case WINTER -> 0.5F;
		};
		return level.getRandom().nextFloat() < rng;
	}
}