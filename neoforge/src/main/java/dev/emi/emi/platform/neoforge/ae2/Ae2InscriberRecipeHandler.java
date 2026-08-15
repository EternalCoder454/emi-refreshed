package dev.emi.emi.platform.neoforge.ae2;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import appeng.menu.SlotSemantics;
import appeng.menu.implementations.InscriberMenu;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;

/**
 * Lets the fill button move an inscriber recipe into an open inscriber.
 *
 * <p>AE2 does not ship this for JEI either, where inscriber recipes are display only, so this is
 * new behaviour rather than parity with the JEI integration.
 *
 * <p>Slots are looked up by AE2's own slot semantics rather than by index. The menu adds them in
 * the order top, bottom, middle, after however many slots the upgradeable menu superclass has
 * already added, so absolute indices are neither stable nor obvious.
 */
public class Ae2InscriberRecipeHandler implements StandardRecipeHandler<InscriberMenu> {

	@Override
	public List<Slot> getInputSources(InscriberMenu menu) {
		List<Slot> slots = Lists.newArrayList();
		for (Slot slot : menu.slots) {
			if (slot.container instanceof Inventory) {
				slots.add(slot);
			}
		}
		slots.addAll(getCraftingSlots(menu));
		return slots;
	}

	@Override
	public List<Slot> getCraftingSlots(InscriberMenu menu) {
		List<Slot> slots = Lists.newArrayList();
		slots.addAll(menu.getSlots(SlotSemantics.INSCRIBER_PLATE_TOP));
		slots.addAll(menu.getSlots(SlotSemantics.MACHINE_INPUT));
		slots.addAll(menu.getSlots(SlotSemantics.INSCRIBER_PLATE_BOTTOM));
		return slots;
	}

	/**
	 * {@link Ae2InscriberEmiRecipe#getInputs()} reports only the slots the recipe actually uses,
	 * so the crafting slots have to be narrowed the same way or a recipe without a top press
	 * would have its middle ingredient placed into the top plate slot.
	 */
	@Override
	public List<Slot> getCraftingSlots(EmiRecipe recipe, InscriberMenu menu) {
		List<Slot> all = getCraftingSlots(menu);
		if (all.size() != 3 || !(recipe instanceof Ae2InscriberEmiRecipe inscriber)) {
			return all;
		}
		List<Slot> slots = Lists.newArrayList();
		if (inscriber.hasTop()) {
			slots.add(all.get(0));
		}
		slots.add(all.get(1));
		if (inscriber.hasBottom()) {
			slots.add(all.get(2));
		}
		return slots;
	}

	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe instanceof Ae2InscriberEmiRecipe;
	}
}
