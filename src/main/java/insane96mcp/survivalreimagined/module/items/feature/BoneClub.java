package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.item.GenericAttackItem;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Bone Club", description = "Add a new early game weapon")
@LoadFeature(module = Modules.Ids.ITEMS)
public class BoneClub extends Feature {
	private static final ILItemTier BONE_TIER = new ILItemTier(1, 66, 2f, 1f, 9, () -> Ingredient.of(Items.BONE));

	public static final RegistryObject<Item> BONE_CLUB = SRItems.ITEMS.register("bone_club", () -> new GenericAttackItem(BONE_TIER, 4, -2.7F, new Item.Properties()));

	public BoneClub(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}