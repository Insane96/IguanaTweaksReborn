package insane96mcp.survivalreimagined.module.sleeprespawn.block;

import insane96mcp.survivalreimagined.module.experience.feature.GlobalExperience;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Death;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GraveBlockEntity extends BlockEntity {
    public static final String ITEMS_TAG = "items";
    public static final String XP_STORED_TAG = "xp_stored";
    public static final String OWNER_TAG = "owner";
    public static final String DEATH_NUMBER_TAG = "death_number";
    private List<ItemStack> items = new ArrayList<>();
    private int xpStored = 0;
    private UUID owner;
    private int deathNumber;
    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(Death.GRAVE_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        this.setChanged();
    }

    public int getXpStored() {
        return this.xpStored;
    }

    public void setXpStored(int xpStored) {
        this.xpStored = xpStored;
        this.setChanged();
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public int getDeathNumber() {
        return this.deathNumber;
    }

    public void setDeathNumber(int deathNumber) {
        this.deathNumber = deathNumber;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        ListTag itemsList = compoundTag.getList(ITEMS_TAG, 10);

        for(int i = 0; i < itemsList.size(); ++i) {
            CompoundTag itemStackTag = itemsList.getCompound(i);
            this.items.add(ItemStack.of(itemStackTag));
        }
        this.xpStored = compoundTag.getInt(XP_STORED_TAG);
        if (compoundTag.contains(OWNER_TAG)) {
            this.owner = compoundTag.getUUID(OWNER_TAG);
            this.deathNumber = compoundTag.getInt(DEATH_NUMBER_TAG);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ListTag itemsList = new ListTag();
        for (ItemStack itemStack : this.items) {
            CompoundTag itemStackTag = new CompoundTag();
            itemStack.save(itemStackTag);
            itemsList.add(itemStackTag);
        }
        compoundTag.put(ITEMS_TAG, itemsList);
        compoundTag.putInt(XP_STORED_TAG, this.xpStored);
        if (this.owner != null) {
            compoundTag.putUUID(OWNER_TAG, this.owner);
            compoundTag.putInt(DEATH_NUMBER_TAG, this.deathNumber);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T t) {
        if (level.getGameTime() % 20 != 9)
            return;

        GraveBlockEntity graveBlockEntity = (GraveBlockEntity) t;
        Optional<ServerPlayer> oPlayer = getPlayerOwner((ServerLevel) level, graveBlockEntity.owner);
        oPlayer.ifPresent(player -> {
            if (player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS)) != graveBlockEntity.deathNumber) {
                if (graveBlockEntity.getXpStored() > 0) {
                    ExperienceOrb xpOrb = new ExperienceOrb(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, graveBlockEntity.getXpStored());
                    xpOrb.getPersistentData().putBoolean(GlobalExperience.XP_PROCESSED, true);
                    level.addFreshEntity(xpOrb);
                    graveBlockEntity.setXpStored(0);
                }
                level.destroyBlock(pos, false);
            }
        });
    }

    public static Optional<ServerPlayer> getPlayerOwner(ServerLevel level, UUID playerUUID) {
        for(ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            if(uuid.equals(playerUUID))
                return Optional.of(player);
        }

        return Optional.empty();
    }
}
