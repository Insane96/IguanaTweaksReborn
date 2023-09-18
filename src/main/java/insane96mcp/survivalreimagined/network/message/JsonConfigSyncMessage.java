package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.combat.stats.Stats;
import insane96mcp.survivalreimagined.module.farming.HarderCrops;
import insane96mcp.survivalreimagined.module.farming.plantsgrowth.PlantsGrowth;
import insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.survivalreimagined.module.items.StackSizes;
import insane96mcp.survivalreimagined.module.items.itemstats.ItemStats;
import insane96mcp.survivalreimagined.module.mining.blockhardness.BlockHardness;
import insane96mcp.survivalreimagined.module.misc.beaconconduit.BeaconConduit;
import insane96mcp.survivalreimagined.module.movement.TerrainSlowdown;
import insane96mcp.survivalreimagined.module.movement.weightedequipment.WeightedEquipment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class JsonConfigSyncMessage {

    ConfigType type;
    String json;
    int jsonSize;

    public JsonConfigSyncMessage(ConfigType type, String json) {
        this.type = type;
        this.json = json;
        this.jsonSize = json.length();
    }

    public static void encode(JsonConfigSyncMessage pkt, FriendlyByteBuf buf) {
        buf.writeEnum(pkt.type);
        buf.writeInt(pkt.jsonSize);
        buf.writeBytes(pkt.json.getBytes());
    }

    public static JsonConfigSyncMessage decode(FriendlyByteBuf buf) {
        ConfigType type = buf.readEnum(ConfigType.class);
        int size = buf.readInt();
        byte[] jsonByte = new byte[size];
        buf.readBytes(jsonByte);
        String json = new String(jsonByte);
        return new JsonConfigSyncMessage(type, json);
    }

    public static void handle(final JsonConfigSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> message.type.consumer.accept(message.json));
        ctx.get().setPacketHandled(true);
    }

    public static void sync(ConfigType type, String json, ServerPlayer player) {
        Object msg = new JsonConfigSyncMessage(type, json);
        CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public enum ConfigType {
        ARMOR_WEIGHT(WeightedEquipment::handleArmorWeightSync),
        CUSTOM_BLOCK_HARDNESS(BlockHardness::handleCustomBlockHardnessPacket),
        CUSTOM_FOOD_PROPERTIES(FoodDrinks::handleCustomFoodPropertiesPacket),
        CUSTOM_FOOD_STACK_SIZES(StackSizes::handleCustomStackSizesPacket),
        CUSTOM_IN_TERRAIN_SLOWDOWN(TerrainSlowdown::handleCustomInTerrainSlowdownSync),
        CUSTOM_TERRAIN_SLOWDOWN(TerrainSlowdown::handleCustomTerrainSlowdownSync),
        DEPTH_HARDNESS(BlockHardness::handleDepthHardnessPacket),
        DIMENSION_HARDNESS(BlockHardness::handleDimensionHardnessPacket),
        DURABILITY(ItemStats::handleDurabilityPacket),
        EFFICIENCIES(ItemStats::handleEfficienciesPacket),
        ENCHANTMENTS_WEIGHTS(WeightedEquipment::handleEnchantmentWeightsSync),
        HARDER_CROPS(HarderCrops::handleSyncPacket),
        ITEM_ATTRIBUTE_MODIFIERS(Stats::handleItemAttributeModifiersPacket),
        PLANTS_GROWTH(PlantsGrowth::handlePlantsGrowthPacket),
        BEACON_EFFECTS(BeaconConduit::handleEffectsPacket);

        final Consumer<String> consumer;

        ConfigType(Consumer<String> consumer) {
            this.consumer = consumer;
        }

        public void consume(String json) {
            this.consumer.accept(json);
        }
    }
}
