package insane96mcp.survivalreimagined.module.misc.beaconconduit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class SRBeaconBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public static final RegistryObject<MobEffect> BLOCK_REACH = SRRegistries.MOB_EFFECTS.register("block_reach", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "bd0c6709-4b67-43d5-ae51-c6180d848978", 0.5f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> ENTITY_REACH = SRRegistries.MOB_EFFECTS.register("entity_reach", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "fb23063a-c676-4da0-8d75-574ab8f3ee30", 0.5f, AttributeModifier.Operation.ADDITION));

    private static final List<MobEffectInstance> MOB_EFFECTS = List.of(
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 2),
            new MobEffectInstance(MobEffects.DIG_SPEED, 240, 2),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 240, 1),
            new MobEffectInstance(MobEffects.JUMP, 240, 1),
            new MobEffectInstance(MobEffects.REGENERATION, 240, 0),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 240, 2),
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 240, 0),
            new MobEffectInstance(MobEffects.INVISIBILITY, 240, 0),
            new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0),
            new MobEffectInstance(MobEffects.HEALTH_BOOST, 240, 4),
            new MobEffectInstance(MobEffects.SLOW_FALLING, 240, 0),
            new MobEffectInstance(BLOCK_REACH.get(), 240, 1),
            new MobEffectInstance(ENTITY_REACH.get(), 240, 1)
    );

    private static final int MAX_LEVELS = 4;
    public static final int DATA_LEVELS = 0;
    public static final int DATA_EFFECT = 1;
    public static final int DATA_AMPLIFIER = 2;
    public static final int DATA_TIME_LEFT = 3;
    public static final int DATA_COUNT = 4;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    /** A list of beam segments for this beacon. */
    List<SRBeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<SRBeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    /** The number of levels of this beacon's pyramid. */
    int levels;
    private int lastCheckY;
    /** The effect given by this beacon. */
    @Nullable
    MobEffect effect;
    /** The amplifier of the effect given by this beacon. */
    int amplifier;
    /** The time left for this beacon to apply the effect*/
    int timeLeft;
    /** The custom name for this beacon. */
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData() {
        public int get(int id) {
            switch (id) {
                case DATA_LEVELS -> {
                    return SRBeaconBlockEntity.this.levels;
                }
                case DATA_EFFECT -> {
                    return MobEffect.getIdFromNullable(SRBeaconBlockEntity.this.effect);
                }
                case DATA_AMPLIFIER -> {
                    return SRBeaconBlockEntity.this.amplifier;
                }
                case DATA_TIME_LEFT -> {
                    return SRBeaconBlockEntity.this.timeLeft;
                }
            }

            return 0;
        }

        public void set(int id, int value) {
            switch (id) {
                case DATA_LEVELS:
                    SRBeaconBlockEntity.this.levels = value;
                    break;
                case DATA_EFFECT:
                    if (!SRBeaconBlockEntity.this.level.isClientSide && !SRBeaconBlockEntity.this.beamSections.isEmpty()) {
                        SRBeaconBlockEntity.playSound(SRBeaconBlockEntity.this.level, SRBeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }

                    SRBeaconBlockEntity.this.effect = MobEffect.byId(value);
                    break;
                case DATA_AMPLIFIER:
                    SRBeaconBlockEntity.this.amplifier = value;
                case DATA_TIME_LEFT:
                    SRBeaconBlockEntity.this.timeLeft = value;
            }

        }

        public int getCount() {
            return DATA_COUNT;
        }
    };

    public SRBeaconBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BeaconConduit.BEACON_BLOCK_ENTITY_TYPE.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, SRBeaconBlockEntity pBlockEntity) {
        /*int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        BlockPos blockpos;
        if (pBlockEntity.lastCheckY < j) {
            blockpos = pPos;
            pBlockEntity.checkingBeamSections = Lists.newArrayList();
            pBlockEntity.lastCheckY = pPos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, pBlockEntity.lastCheckY + 1, k);
        }

        SRBeaconBlockEntity.BeaconBeamSection SRBeaconBlockEntity$beaconbeamsection = pBlockEntity.checkingBeamSections.isEmpty() ? null : pBlockEntity.checkingBeamSections.get(pBlockEntity.checkingBeamSections.size() - 1);
        int l = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for(int i1 = 0; i1 < BLOCKS_CHECK_PER_TICK && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = pLevel.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            float[] afloat = blockstate.getBeaconColorMultiplier(pLevel, blockpos, pPos);
            if (afloat != null) {
                if (pBlockEntity.checkingBeamSections.size() <= 1) {
                    SRBeaconBlockEntity$beaconbeamsection = new SRBeaconBlockEntity.BeaconBeamSection(afloat);
                    pBlockEntity.checkingBeamSections.add(SRBeaconBlockEntity$beaconbeamsection);
                } else if (SRBeaconBlockEntity$beaconbeamsection != null) {
                    if (Arrays.equals(afloat, SRBeaconBlockEntity$beaconbeamsection.color)) {
                        SRBeaconBlockEntity$beaconbeamsection.increaseHeight();
                    } else {
                        SRBeaconBlockEntity$beaconbeamsection = new SRBeaconBlockEntity.BeaconBeamSection(new float[]{(SRBeaconBlockEntity$beaconbeamsection.color[0] + afloat[0]) / 2.0F, (SRBeaconBlockEntity$beaconbeamsection.color[1] + afloat[1]) / 2.0F, (SRBeaconBlockEntity$beaconbeamsection.color[2] + afloat[2]) / 2.0F});
                        pBlockEntity.checkingBeamSections.add(SRBeaconBlockEntity$beaconbeamsection);
                    }
                }
            } else {
                if (SRBeaconBlockEntity$beaconbeamsection == null || blockstate.getLightBlock(pLevel, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                    pBlockEntity.checkingBeamSections.clear();
                    pBlockEntity.lastCheckY = l;
                    break;
                }

                SRBeaconBlockEntity$beaconbeamsection.increaseHeight();
            }

            blockpos = blockpos.above();
            ++pBlockEntity.lastCheckY;
        }

        int j1 = pBlockEntity.levels;
        if (pLevel.getGameTime() % 80L == 0L) {
            if (!pBlockEntity.beamSections.isEmpty()) {
                pBlockEntity.levels = updateBase(pLevel, i, j, k);
            }

            if (pBlockEntity.levels > 0 && !pBlockEntity.beamSections.isEmpty()) {
                applyEffects(pLevel, pPos, pBlockEntity.levels, pBlockEntity.primaryPower, pBlockEntity.secondaryPower);
                playSound(pLevel, pPos, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (pBlockEntity.lastCheckY >= l) {
            pBlockEntity.lastCheckY = pLevel.getMinBuildHeight() - 1;
            boolean flag = j1 > 0;
            pBlockEntity.beamSections = pBlockEntity.checkingBeamSections;
            if (!pLevel.isClientSide) {
                boolean flag1 = pBlockEntity.levels > 0;
                if (!flag && flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_ACTIVATE);

                    for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, (new AABB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).inflate(10.0D, 5.0D, 10.0D))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, pBlockEntity.levels);
                    }
                } else if (flag && !flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }*/

    }

    private static int updateBase(Level pLevel, int pX, int pY, int pZ) {
        int i = 0;

        for(int j = 1; j <= 4; i = j++) {
            int k = pY - j;
            if (k < pLevel.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;

            for(int l = pX - j; l <= pX + j && flag; ++l) {
                for(int i1 = pZ - j; i1 <= pZ + j; ++i1) {
                    if (!pLevel.getBlockState(new BlockPos(l, k, i1)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                break;
            }
        }

        return i;
    }

    /**
     * Marks this {@code BlockEntity} as no longer valid (removed from the level).
     */
    public void setRemoved() {
        playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private static void applyEffects(Level pLevel, BlockPos pPos, int pLevels, @javax.annotation.Nullable MobEffect pPrimary, @javax.annotation.Nullable MobEffect pSecondary) {
        if (!pLevel.isClientSide && pPrimary != null) {
            double d0 = (double)(pLevels * 10 + 10);
            int i = 0;
            if (pLevels >= 4 && pPrimary == pSecondary) {
                i = 1;
            }

            int j = (9 + pLevels * 2) * 20;
            AABB aabb = (new AABB(pPos)).inflate(d0).expandTowards(0.0D, (double)pLevel.getHeight(), 0.0D);
            List<Player> list = pLevel.getEntitiesOfClass(Player.class, aabb);

            for(Player player : list) {
                player.addEffect(new MobEffectInstance(pPrimary, j, i, true, true));
            }

            if (pLevels >= 4 && pPrimary != pSecondary && pSecondary != null) {
                for(Player player1 : list) {
                    player1.addEffect(new MobEffectInstance(pSecondary, j, 0, true, true));
                }
            }

        }
    }

    public static void playSound(Level pLevel, BlockPos pPos, SoundEvent pSound) {
        pLevel.playSound(null, pPos, pSound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public List<SRBeaconBlockEntity.BeaconBeamSection> getBeamSections() {
        return this.levels == 0 ? ImmutableList.of() : this.beamSections;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.effect = MobEffect.byId(pTag.getInt("Effect"));
        this.amplifier = pTag.getInt("Amplifier");
        this.timeLeft = pTag.getInt("TimeLeft");
        if (pTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }

        this.lockKey = LockCode.fromTag(pTag);
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("Effect", MobEffect.getIdFromNullable(this.effect));
        pTag.putInt("Amplifier", this.amplifier);
        pTag.putInt("TimeLeft", this.timeLeft);
        pTag.putInt("Levels", this.levels);
        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }

        this.lockKey.addToTag(pTag);
    }

    /**
     * Sets the custom name for this beacon.
     */
    public void setCustomName(@javax.annotation.Nullable Component pName) {
        this.name = pName;
    }

    @javax.annotation.Nullable
    public Component getCustomName() {
        return this.name;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return BaseContainerBlockEntity.canUnlock(pPlayer, this.lockKey, this.getDisplayName()) ? new SRBeaconMenu(pContainerId, pPlayerInventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos())) : null;
    }

    public Component getDisplayName() {
        return this.getName();
    }

    public Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        this.lastCheckY = pLevel.getMinBuildHeight() - 1;
    }

    public static class BeaconBeamSection {
        /** The colors of this section of a beacon beam, in RGB float format. */
        final float[] color;
        private int height;

        public BeaconBeamSection(float[] pColor) {
            this.color = pColor;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        /**
         * @return The colors of this section of a beacon beam, in RGB float format.
         */
        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }}
