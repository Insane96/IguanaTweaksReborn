package insane96mcp.survivalreimagined.module.world.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(SerializableTrade.SerializableTradeSerializer.class)
public class SerializableTrade implements VillagerTrades.ItemListing {
	public ItemStack itemA;
	@Nullable
	public ItemStack itemB;
	public ItemStack result;
	private int maxUses;
	private int xp = 0;

	@Nullable
	private EnchantRandomly enchantRandomly;

	private final List<EnchantmentInstance> enchantments = new ArrayList<>();

	public SerializableTrade() {

	}

	public SerializableTrade(ItemStack itemA, ItemStack result, int maxUses) {
		this(itemA, null, result, maxUses, 0);
	}

	public SerializableTrade(ItemStack itemA, @Nullable ItemStack itemB, ItemStack result, int maxUses, int xp) {
		this.itemA = itemA;
		this.itemB = itemB;
		this.result = result;
		this.maxUses = maxUses;
		this.xp = xp;
	}

	public SerializableTrade enchant(Enchantment enchantment, int level) {
		this.enchantments.add(new EnchantmentInstance(enchantment, level));
		return this;
	}

	public SerializableTrade enchantResult(int minLevel, int maxLevel, boolean treasure) {
		this.enchantRandomly = new EnchantRandomly(minLevel, maxLevel, treasure);
		return this;
	}

	@Nullable
	@Override
	public MerchantOffer getOffer(Entity entity, RandomSource random) {
		ItemStack result = this.result.copy();
		if (this.enchantRandomly != null)
			result = EnchantmentHelper.enchantItem(random, result, random.nextInt(this.enchantRandomly.minLevel, this.enchantRandomly.maxLevel + 1), this.enchantRandomly.treasure);
		for (EnchantmentInstance enchantmentInstance : this.enchantments) {
			result.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
		}
		return new MerchantOffer(this.itemA, this.itemB == null ? ItemStack.EMPTY : this.itemB, result, this.maxUses, this.xp, 1f);
	}

	public static final Type SERIALIZABLE_TRADE_LIST_TYPE = new TypeToken<ArrayList<SerializableTrade>>(){}.getType();

	public static class SerializableTradeSerializer implements JsonDeserializer<SerializableTrade>, JsonSerializer<SerializableTrade> {
		@Override
		public SerializableTrade deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			SerializableTrade serializableTrade = new SerializableTrade();

			String sItemA = GsonHelper.getAsString(json.getAsJsonObject(), "item_a");
			int itemACount = GsonHelper.getAsInt(json.getAsJsonObject(), "item_a_count", 1);
			ResourceLocation itemA = new ResourceLocation(sItemA);
			serializableTrade.itemA = new ItemStack(ForgeRegistries.ITEMS.getValue(itemA), itemACount);

			String sItemB = GsonHelper.getAsString(json.getAsJsonObject(), "item_b", "");
			if (!sItemB.isEmpty()) {
				int itemBCount = GsonHelper.getAsInt(json.getAsJsonObject(), "item_b_count", 1);
				ResourceLocation itemB = new ResourceLocation(sItemB);
				serializableTrade.itemB = new ItemStack(ForgeRegistries.ITEMS.getValue(itemB), itemBCount);
			}
			String sItemResult = GsonHelper.getAsString(json.getAsJsonObject(), "item_result");
			int itemResultCount = GsonHelper.getAsInt(json.getAsJsonObject(), "item_result_count", 1);
			String itemResultTag = GsonHelper.getAsString(json.getAsJsonObject(), "item_result_tag", "{}");
			ResourceLocation itemResult = new ResourceLocation(sItemResult);
			CompoundTag tag;
			try {
				tag = TagParser.parseTag(itemResultTag);
			} catch (CommandSyntaxException e) {
				tag = null;
			}
			serializableTrade.result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemResult), itemResultCount, tag);
			JsonObject enchantRandomly = GsonHelper.getAsJsonObject(json.getAsJsonObject(), "enchant_randomly", null);
			if (enchantRandomly != null) {
				serializableTrade.enchantRandomly = new EnchantRandomly(GsonHelper.getAsInt(enchantRandomly, "min_levels"), GsonHelper.getAsInt(enchantRandomly, "max_levels"), GsonHelper.getAsBoolean(enchantRandomly, "treasure"));
			}
			JsonArray enchantments = GsonHelper.getAsJsonArray(json.getAsJsonObject(), "enchantments", null);
			if (enchantments != null) {
				enchantments.asList().forEach(jsonElement -> {
					String id = GsonHelper.getAsString(jsonElement.getAsJsonObject(), "id");
					int level = GsonHelper.getAsInt(jsonElement.getAsJsonObject(), "level", 1);
					serializableTrade.enchantments.add(new EnchantmentInstance(ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(id)), level));
				});
			}

			serializableTrade.maxUses = GsonHelper.getAsInt(json.getAsJsonObject(), "max_uses");
			serializableTrade.xp = GsonHelper.getAsInt(json.getAsJsonObject(), "xp", 0);

			return serializableTrade;
		}

		@Override
		public JsonElement serialize(SerializableTrade src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item_a", ForgeRegistries.ITEMS.getKey(src.itemA.getItem()).toString());
			jsonObject.addProperty("item_a_count", src.itemA.getCount());
			if (src.itemB != null) {
				jsonObject.addProperty("item_b", ForgeRegistries.ITEMS.getKey(src.itemB.getItem()).toString());
				jsonObject.addProperty("item_b_count", src.itemB.getCount());
			}
			jsonObject.addProperty("item_result", ForgeRegistries.ITEMS.getKey(src.result.getItem()).toString());
			jsonObject.addProperty("item_result_count", src.result.getCount());
			if (src.result.getTag() != null)
				jsonObject.addProperty("item_result_tag", src.result.getTag().toString());
			if (src.enchantRandomly != null) {
				JsonObject enchantRandomly = new JsonObject();
				enchantRandomly.addProperty("min_levels", src.enchantRandomly.minLevel);
				enchantRandomly.addProperty("max_levels", src.enchantRandomly.maxLevel);
				enchantRandomly.addProperty("treasure", src.enchantRandomly.treasure);
				jsonObject.add("enchant_randomly", enchantRandomly);
			}
			if (!src.enchantments.isEmpty()) {
				JsonArray jsonArray = new JsonArray();
				src.enchantments.forEach(enchantmentInstance -> {
					JsonObject enchantmentsObject = new JsonObject();
					enchantmentsObject.addProperty("id", ForgeRegistries.ENCHANTMENTS.getKey(enchantmentInstance.enchantment).toString());
					if (enchantmentInstance.level > 1)
						enchantmentsObject.addProperty("level", enchantmentInstance.level);
					jsonArray.add(enchantmentsObject);
				});
				jsonObject.add("enchantments", jsonArray);
			}
			jsonObject.addProperty("max_uses", src.maxUses);
			jsonObject.addProperty("xp", src.xp);
			return jsonObject;
		}
	}

	private record EnchantRandomly(int minLevel, int maxLevel, boolean treasure) {
	}
}
