package insane96mcp.iguanatweaksreborn.module.misc.beaconconduit;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Label(name = "Beacon & Conduit", description = "Beacon Range varying based of blocks of the pyramid and better conduit killing mobs. Blocks list and ranges are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MISC)
public class BeaconConduit extends JsonFeature {

    public static final SimpleBlockWithItem BEACON = SimpleBlockWithItem.register("beacon", () -> new ITRBeaconBlock(BlockBehaviour.Properties.copy(Blocks.BEACON)));
    public static final RegistryObject<BlockEntityType<ITRBeaconBlockEntity>> BEACON_BLOCK_ENTITY_TYPE = ITRRegistries.BLOCK_ENTITY_TYPES.register("beacon", () -> BlockEntityType.Builder.of(ITRBeaconBlockEntity::new, BEACON.block().get()).build(null));
    public static final RegistryObject<MenuType<ITRBeaconMenu>> BEACON_MENU_TYPE = ITRRegistries.MENU_TYPES.register("beacon", () -> new MenuType<>(ITRBeaconMenu::new, FeatureFlags.VANILLA_SET));


    public static final RegistryObject<MobEffect> BLOCK_REACH = ITRRegistries.MOB_EFFECTS.register("block_reach", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "bd0c6709-4b67-43d5-ae51-c6180d848978", 0.5f, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> ENTITY_REACH = ITRRegistries.MOB_EFFECTS.register("entity_reach", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "fb23063a-c676-4da0-8d75-574ab8f3ee30", 0.075f, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final ArrayList<IdTagValue> BLOCKS_LIST_DEFAULT = new ArrayList<>(List.of(
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_block", 1d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:emerald_block", 3.0d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:gold_block", 1.8d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_block", 2.5d),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_block", 4d),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:durium_block", 3.0d),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:soul_steel_block", 1.5d),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:quaron_block", 1.5d),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:keego_block", 3d)
    ));
    public static final ArrayList<IdTagValue> blocksList = new ArrayList<>();
    public static final ArrayList<IdTagValue> PAYMENT_TIMES_DEFAULT = new ArrayList<>(List.of(
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_ingot", 6000),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:gold_ingot", 18000),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond", 72000),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:emerald", 72000),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:netherite_ingot", 115200),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:durium_ingot", 12000),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:keego", 96000),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:quaron_ingot", 96000),
            new IdTagValue(IdTagMatcher.Type.ID, "iguanatweaksexpanded:soul_steel_ingot", 96000),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:nether_star", 576000)
    ));
    public static final ArrayList<IdTagValue> paymentTimes = new ArrayList<>();
    public static final ArrayList<BeaconEffect> EFFECTS_DEFAULT = new ArrayList<>(List.of(
            new BeaconEffect(MobEffects.MOVEMENT_SPEED, new int[] {1, 2, 4}),
            new BeaconEffect(MobEffects.DIG_SPEED, new int[] {1, 2, 4}),
            new BeaconEffect(MobEffects.DAMAGE_BOOST, new int[] {1, 3, 9}),
            new BeaconEffect(MobEffects.JUMP, new int[] {1, 2, 3}),
            new BeaconEffect(MobEffects.REGENERATION, new int[] {8}),
            new BeaconEffect("iguanatweaksreborn:regenerating_absorption", new int[] {2, 4}),
            new BeaconEffect(MobEffects.DAMAGE_RESISTANCE, new int[] {1, 3, 9}),
            new BeaconEffect(MobEffects.FIRE_RESISTANCE, new int[] {3}),
            new BeaconEffect(MobEffects.INVISIBILITY, new int[] {2}),
            new BeaconEffect(MobEffects.NIGHT_VISION, new int[] {3}),
            new BeaconEffect(MobEffects.SLOW_FALLING, new int[] {2}),
            new BeaconEffect(MobEffects.LEVITATION, new int[] {1, 2, 3}),
            new BeaconEffect("iguanatweaksreborn:block_reach", new int[] {1, 3, 9}),
            new BeaconEffect("iguanatweaksreborn:entity_reach", new int[] {1, 3, 9})
    ));
    public static final ArrayList<BeaconEffect> effects = new ArrayList<>();

    @Config
    @Label(name = "Conduit.Better Protection", description = "Greatly increases the range and damage of the conduit")
    public static Boolean betterConduitProtection = true;
    @Config(min = 0d, max = 64d)
    @Label(name = "Conduit.Protection Distance Multiplier", description = "Distance multiplier (formula is blocks_around / 7 * this_multiplier) from the conduit at which it will deal damage to enemies.")
    public static Double conduitProtectionDistanceMultiplier = 8d;
    @Config(min = 0d, max = 96d)
    @Label(name = "Conduit.Protection Max Damage Distance", description = "If a mob is within this radius from the conduit, it will be dealt the maximum amount of damage.")
    public static Double conduitProtectionMaxDamageDistance = 8d;

    public BeaconConduit(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);

        JSON_CONFIGS.add(new JsonConfig<>("beacon_blocks_ranges.json", blocksList, BLOCKS_LIST_DEFAULT, IdTagValue.LIST_TYPE));
        JSON_CONFIGS.add(new JsonConfig<>("beacon_payment_times.json", paymentTimes, PAYMENT_TIMES_DEFAULT, IdTagValue.LIST_TYPE));
        addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "beacon_effects"), new SyncType(json -> loadAndReadJson(json, effects, EFFECTS_DEFAULT, BeaconEffect.LIST_TYPE)));
        JSON_CONFIGS.add(new JsonConfig<>("beacon_effects.json", effects, EFFECTS_DEFAULT, BeaconEffect.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "beacon_effects")));
        IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "better_beacon", Component.literal("IguanaTweaks Reborn Better Beacon"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks));
    }

    @Override
    public String getModConfigFolder() {
        return IguanaTweaksReborn.CONFIG_FOLDER;
    }

    public static int getPaymentTime(ItemStack stack) {
        for (IdTagValue idTagValue : paymentTimes) {
            if (idTagValue.id.matchesItem(stack.getItem()))
                return (int) idTagValue.value;
        }
        return 0;
    }

    @Nullable
    static BeaconEffect cachedBeaconEffect;

    public static int getEffectTimeScale(@Nullable MobEffect mobEffect, int amplifier) {
        if (cachedBeaconEffect != null && Objects.equals(mobEffect, cachedBeaconEffect.getEffect()))
            return cachedBeaconEffect.getTimeCostForAmplifier(amplifier);
        for (BeaconEffect beaconEffect : effects) {
            if (beaconEffect.location.equals(ForgeRegistries.MOB_EFFECTS.getKey(mobEffect))) {
                cachedBeaconEffect = beaconEffect;
                return beaconEffect.getTimeCostForAmplifier(amplifier);
            }
        }
        return 1;
    }

    public static boolean isValidEffect(MobEffect mobEffect) {
        for (BeaconEffect beaconEffect : effects) {
            if (beaconEffect.location.equals(ForgeRegistries.MOB_EFFECTS.getKey(mobEffect)))
                return true;
        }
        return false;
    }

    /*
     * CONDUIT
     */

    static float MIN_DAMAGE = 2f;
    static float MAX_DAMAGE = 6f;

    public static boolean conduitUpdateDestroyEnemies(Level level, BlockPos blockPos, List<BlockPos> blocks) {
        if (!isEnabled(BeaconConduit.class)
                || !betterConduitProtection)
            return false;

        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, getDamageAABB(blockPos, blocks),
                (living) -> living instanceof Enemy && living.isInWaterOrRain());

        for (LivingEntity entity : list) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
            double distance = entity.position().distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            float damage;
            if (distance < conduitProtectionMaxDamageDistance)
                damage = MAX_DAMAGE;
            else
                damage = (float) (1 - (distance - conduitProtectionMaxDamageDistance) / (maxRangeRadius() - conduitProtectionMaxDamageDistance)) * (MAX_DAMAGE - MIN_DAMAGE) + MIN_DAMAGE;
            entity.hurt(entity.damageSources().magic(), damage);
        }
        return true;
    }

    private static AABB getDamageAABB(BlockPos blockPos, List<BlockPos> blocks) {
        double range = blocks.size() / 7d * conduitProtectionDistanceMultiplier;
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        return (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(range);
    }

    private static double maxRange() {
        return 42 / 7d * conduitProtectionDistanceMultiplier;
    }

    private static double maxRangeRadius() {
        return Math.sqrt(maxRange() * maxRange() + maxRange() * maxRange());
    }

    @JsonAdapter(BeaconEffect.Serializer.class)
    public static class BeaconEffect extends IdTagMatcher {
        int[] timeCost;

        public BeaconEffect(String location, int[] timeCost) {
            super(Type.ID, new ResourceLocation(location), null);
            this.timeCost = timeCost;
        }

        public BeaconEffect(MobEffect mobEffect, int[] timeCost) {
            super(Type.ID, ForgeRegistries.MOB_EFFECTS.getKey(mobEffect), null);
            this.timeCost = timeCost;
        }

        public MobEffect getEffect() {
            if (!ForgeRegistries.MOB_EFFECTS.containsKey(this.location))
                throw new NullPointerException("No mob effect found with id %s".formatted(this.location));
            return ForgeRegistries.MOB_EFFECTS.getValue(this.location);
        }

        public int getMaxAmplifier() {
            return this.timeCost.length - 1;
        }

        public int getTimeCostForAmplifier(int amplifier) {
            if (this.timeCost.length <= amplifier)
                return 1;
            return this.timeCost[amplifier];
        }

        public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<BeaconEffect>>(){}.getType();
        private static class Serializer implements JsonSerializer<BeaconEffect>, JsonDeserializer<BeaconEffect> {
            @Override
            public BeaconEffect deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jObject = json.getAsJsonObject();
                JsonArray jArray = jObject.getAsJsonArray("time_cost");
                if (jArray.size() > 8)
                    throw new JsonParseException("time_cost size cannot be greater than 8");
                List<Integer> timeCost = new ArrayList<>();
                jArray.forEach(jsonElement -> timeCost.add(jsonElement.getAsInt()));
                return new BeaconEffect(GsonHelper.getAsString(jObject, "id"), timeCost.stream().mapToInt(i->i).toArray());
            }

            @Override
            public JsonElement serialize(BeaconEffect src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jObject = new JsonObject();
                jObject.addProperty("id", src.location.toString());
                JsonArray jArray = new JsonArray();
                for (int i = 0; i < src.timeCost.length; i++) {
                    jArray.add(src.timeCost[i]);
                }
                jObject.add("time_cost", jArray);
                return jObject;
            }
        }
    }
}