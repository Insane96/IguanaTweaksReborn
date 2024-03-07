package insane96mcp.iguanatweaksreborn.network;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.FoodRegenSync;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.NoHungerSync;
import insane96mcp.iguanatweaksreborn.module.world.spawners.SpawnerStatusSync;
import insane96mcp.iguanatweaksreborn.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = Integer.toString(3);
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "network_channel"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	private static int index = 0;

	public static void init() {
		CHANNEL.registerMessage(++index, InvulnerableTimeMessageSync.class, InvulnerableTimeMessageSync::encode, InvulnerableTimeMessageSync::decode, InvulnerableTimeMessageSync::handle);
		CHANNEL.registerMessage(++index, AnvilRepairSync.class, AnvilRepairSync::encode, AnvilRepairSync::decode, AnvilRepairSync::handle);
		CHANNEL.registerMessage(++index, ItemStatisticsSync.class, ItemStatisticsSync::encode, ItemStatisticsSync::decode, ItemStatisticsSync::handle);
		CHANNEL.registerMessage(++index, StackSizesSync.class, StackSizesSync::encode, StackSizesSync::decode, StackSizesSync::handle);
		CHANNEL.registerMessage(++index, BackwardsSlowdownUpdate.class, BackwardsSlowdownUpdate::encode, BackwardsSlowdownUpdate::decode, BackwardsSlowdownUpdate::handle);
		CHANNEL.registerMessage(++index, StaminaSync.class, StaminaSync::encode, StaminaSync::decode, StaminaSync::handle);
		CHANNEL.registerMessage(++index, FoodRegenSync.class, FoodRegenSync::encode, FoodRegenSync::decode, FoodRegenSync::handle);
		CHANNEL.registerMessage(++index, ExhaustionSync.class, ExhaustionSync::encode, ExhaustionSync::decode, ExhaustionSync::handle);
		CHANNEL.registerMessage(++index, SaturationSync.class, SaturationSync::encode, SaturationSync::decode, SaturationSync::handle);
		CHANNEL.registerMessage(++index, GlobalHardnessSync.class, GlobalHardnessSync::encode, GlobalHardnessSync::decode, GlobalHardnessSync::handle);
		CHANNEL.registerMessage(++index, SpawnerStatusSync.class, SpawnerStatusSync::encode, SpawnerStatusSync::decode, SpawnerStatusSync::handle);
		CHANNEL.registerMessage(++index, TirednessSync.class, TirednessSync::encode, TirednessSync::decode, TirednessSync::handle);
		CHANNEL.registerMessage(++index, SetITRBeaconEffects.class, SetITRBeaconEffects::encode, SetITRBeaconEffects::decode, SetITRBeaconEffects::handle);
		CHANNEL.registerMessage(++index, RegenAbsorptionSync.class, RegenAbsorptionSync::encode, RegenAbsorptionSync::decode, RegenAbsorptionSync::handle);
		CHANNEL.registerMessage(++index, PlantGrowthMultiplierSync.class, PlantGrowthMultiplierSync::encode, PlantGrowthMultiplierSync::decode, PlantGrowthMultiplierSync::handle);
		CHANNEL.registerMessage(++index, ForgeDataIntSync.class, ForgeDataIntSync::encode, ForgeDataIntSync::decode, ForgeDataIntSync::handle);
		CHANNEL.registerMessage(++index, NoHungerSync.class, NoHungerSync::encode, NoHungerSync::decode, NoHungerSync::handle);
	}
}
