package insane96mcp.iguanatweaksreborn.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class ITEffect extends Effect {

	boolean canBeCured;

	public ITEffect(EffectType typeIn, int liquidColorIn) {
		this(typeIn, liquidColorIn, true);
	}

	public ITEffect(EffectType typeIn, int liquidColorIn, boolean canBeCured) {
		super(typeIn, liquidColorIn);
		this.canBeCured = canBeCured;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		if (!canBeCured)
			return new ArrayList<>();
		else
			return super.getCurativeItems();
	}
}
