package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

//Rename to Villagers
@Label(name = "Villager Nerfs", description = "Small changes to villagers to make them less OP")
public class VillagerNerf extends Feature {

	private static final String CURE_DISCOUNT_REMOVED = IguanaTweaksReborn.RESOURCE_PREFIX + "cure_discount_removed";

	private final ForgeConfigSpec.ConfigValue<Boolean> lockTradesConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> alwaysConvertZombieConfig;
	private final ForgeConfigSpec.ConfigValue<Double> maxDiscountConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> preventCureDiscountConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> clampNegativeDemandConfig;

	public boolean lockTrades = true;
	public boolean alwaysConvertZombie = true;
	public double maxDiscount = 0.5d;
	public boolean preventCureDiscount = true;
	public boolean clampNegativeDemand = true;

	public VillagerNerf(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		lockTradesConfig = Config.builder
				.comment("If true, villagers will be given 1 trading experience as soon as they choose their job to lock the trades.")
				.define("Lock Trades", this.lockTrades);
		alwaysConvertZombieConfig = Config.builder
				.comment("If true, villagers will always be transformed into Zombies, no matter the difficulty.")
				.define("Always Convert Zombie", this.alwaysConvertZombie);
		maxDiscountConfig = Config.builder
				.comment("Define a max percentage discount that villagers can give.")
				.defineInRange("Max Discount Percentage", this.maxDiscount, 0d, 1d);
		preventCureDiscountConfig = Config.builder
				.comment("If true, villagers will no longer get the discount when cured from Zombies to prevent over discounting.")
				.define("Prevent Cure Discount", this.preventCureDiscount);
		clampNegativeDemandConfig = Config.builder
				.comment("When villagers restock, they update the 'demand'. Demand is a trade modifier that increases the price whenever a trade is done many times, BUT when a trade is not performed, at each restock the 'demand' goes negative, making possible for a trade to never increase it's price due to high negative demand. With this to true, negative demand will be capped at -max_uses of the trade (e.g. Carrot trade from a farmer will have it's minimum demand set to -16).")
				.define("Clamp Negative Demand", this.clampNegativeDemand);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.lockTrades = this.lockTradesConfig.get();
		this.alwaysConvertZombie = this.alwaysConvertZombieConfig.get();
		this.maxDiscount = this.maxDiscountConfig.get();
		this.preventCureDiscount = this.preventCureDiscountConfig.get();
		this.clampNegativeDemand = this.clampNegativeDemandConfig.get();
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		if (!this.isEnabled())
			return;

		if (!this.preventCureDiscount)
			return;

		if (!(event.getEntityLiving() instanceof Villager villager))
			return;

		if (villager.getPersistentData().getBoolean(CURE_DISCOUNT_REMOVED))
			return;

		Map<UUID, Object2IntMap<GossipType>> gossips = villager.getGossips().getGossipEntries();
		gossips.forEach(((uuid, gossipTypeObject2IntMap) -> {
			villager.getGossips().remove(uuid, GossipType.MAJOR_POSITIVE);
			villager.getGossips().remove(uuid, GossipType.MINOR_POSITIVE);
		}));
		villager.getPersistentData().putBoolean(CURE_DISCOUNT_REMOVED, true);
	}

	public int clampSpecialPrice(int specialPriceDiff, final ItemStack baseCostA) {
		if (!this.isEnabled()
				|| this.maxDiscount == 1d)
			return specialPriceDiff;

		if (specialPriceDiff < 0 && Mth.abs(specialPriceDiff) > baseCostA.getCount() * this.maxDiscount)
			return Mth.clamp(specialPriceDiff, (int) (baseCostA.getCount() * -this.maxDiscount), 0);

		return specialPriceDiff;
	}

	public int clampDemand(int demand, int maxUses) {
		if (!this.isEnabled()
				|| !this.clampNegativeDemand)
			return demand;

		return Math.max(demand, -maxUses);
	}

	public void lockTrades(Villager villager) {
		if (!this.isEnabled()
				|| !this.lockTrades)
			return;

		if (villager.getVillagerData().getProfession() != VillagerProfession.NONE && villager.getVillagerXp() == 0)
			villager.setVillagerXp(1);
	}

	public void onZombieKillEntity(Zombie zombie, ServerLevel level, LivingEntity killedEntity) {
		if (!this.isEnabled()
				|| !this.alwaysConvertZombie
				//If removed should mean that the Zombie Villager has already been converted
				|| killedEntity.isRemoved())
			return;

		if (killedEntity instanceof Villager villager && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(killedEntity, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
			ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
			zombievillager.finalizeSpawn(level, level.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
			zombievillager.setVillagerData(villager.getVillagerData());
			zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
			zombievillager.setTradeOffers(villager.getOffers().createTag());
			zombievillager.setVillagerXp(villager.getVillagerXp());
			net.minecraftforge.event.ForgeEventFactory.onLivingConvert(killedEntity, zombievillager);
			if (!zombie.isSilent()) {
				level.levelEvent(null, 1026, zombie.blockPosition(), 0);
			}
		}
	}
}