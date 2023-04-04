package insane96mcp.survivalreimagined.data;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;
import java.util.function.Supplier;

public class SRRandomSpreadWithChance extends StructurePlacement {
    public static final Codec<SRRandomSpreadWithChance> CODEC =
            RecordCodecBuilder.create(instance ->
            placementCodec(instance).and(instance.group(
                    RandomSpreadParams.CODEC.get().fieldOf("random_spread").forGetter(SRRandomSpreadWithChance::randomSpreadParams),
                    ChanceParams.CODEC.get().fieldOf("chance_data").forGetter(SRRandomSpreadWithChance::chanceParams)
            )).apply(instance, SRRandomSpreadWithChance::new));

    RandomSpreadParams randomSpreadParams;

    ChanceParams chanceParams;

    public SRRandomSpreadWithChance(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone, RandomSpreadParams randomSpreadParams, ChanceParams chanceParams) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.randomSpreadParams = randomSpreadParams;
        this.chanceParams = chanceParams;
    }

    /*public SRRandomSpreadWithChance(int spacing, int separation, RandomSpreadType spreadType, int salt, int endRange) {
        this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, salt, Optional.empty(), spacing, separation, spreadType, 0, endRange, 0f);
    }*/

    public RandomSpreadParams randomSpreadParams() {
        return this.randomSpreadParams;
    }

    public ChanceParams chanceParams() {
        return this.chanceParams;
    }

    public ChunkPos getPotentialStructureChunk(long p_227009_, int p_227010_, int p_227011_) {
        int i = Math.floorDiv(p_227010_, this.randomSpreadParams.spacing);
        int j = Math.floorDiv(p_227011_, this.randomSpreadParams.spacing);
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureWithSalt(p_227009_, i, j, this.salt());
        int k = this.randomSpreadParams.spacing - this.randomSpreadParams.separation;
        int l = this.randomSpreadParams.spreadType.evaluate(worldgenrandom, k);
        int i1 = this.randomSpreadParams.spreadType.evaluate(worldgenrandom, k);
        return new ChunkPos(i * this.randomSpreadParams.spacing + l, j * this.randomSpreadParams.spacing + i1);
    }

    @Override
    public boolean isStructureChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int chunkX, int chunkZ) {
        if (!this.frequencyReductionMethod().shouldGenerate(chunkGeneratorStructureState.getLevelSeed(), this.salt(), chunkX, chunkZ, this.chanceParams.getChanceAt(chunkX, chunkZ)))
            return false;
        return super.isStructureChunk(chunkGeneratorStructureState, chunkX, chunkZ);
    }

    protected boolean isPlacementChunk(ChunkGeneratorStructureState p_256267_, int p_256050_, int p_255975_) {
        ChunkPos chunkpos = this.getPotentialStructureChunk(p_256267_.getLevelSeed(), p_256050_, p_255975_);
        return chunkpos.x == p_256050_ && chunkpos.z == p_255975_;
    }

    @Override
    public StructurePlacementType<?> type() {
        return StructurePlacementType.RANDOM_SPREAD;
    }

    public record RandomSpreadParams(int spacing, int separation, RandomSpreadType spreadType) {
            public static final Supplier<Codec<RandomSpreadParams>> CODEC = Suppliers.memoize(() ->
                    RecordCodecBuilder.<RandomSpreadParams>mapCodec((instance) ->
                                    instance.group(
                                            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(RandomSpreadParams::spacing),
                                            Codec.intRange(0, 4096).fieldOf("separation").forGetter(RandomSpreadParams::separation),
                                            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadParams::spreadType)
                                    ).apply(instance, RandomSpreadParams::new))
                            .flatXmap((p_275182_) -> p_275182_.spacing <= p_275182_.separation ? DataResult.error(() -> "Spacing has to be larger than separation") : DataResult.success(p_275182_), DataResult::success).codec());
    }

    /**
     * @param multiplierAtStart Chance to not generate when at start range
     */
    public record ChanceParams(int startRange, int endRange, float multiplierAtStart) {
            public static final Supplier<Codec<ChanceParams>> CODEC = Suppliers.memoize(() ->
                    RecordCodecBuilder.<ChanceParams>mapCodec((instance) ->
                            instance.group(
                                    Codec.INT.optionalFieldOf("start_range", 0).forGetter(ChanceParams::startRange),
                                    Codec.INT.fieldOf("end_range").forGetter(ChanceParams::endRange),
                                    Codec.FLOAT.optionalFieldOf("multiplier_at_start", 0f).forGetter(ChanceParams::multiplierAtStart)
                            ).apply(instance, ChanceParams::new)).codec());

            public float getChanceAt(int chunkX, int chunkY) {
                int x = chunkX * 16;
                int z = chunkY * 16;
                int distanceFromOrigin = (int) Math.sqrt((x * x) + (z * z));
                int distanceFromStart = (distanceFromOrigin - this.startRange);
                //Chance to not spawn
                return (this.endRange - distanceFromStart) / ((float) this.endRange - this.startRange) * (1f - multiplierAtStart);
            }

    }
}
