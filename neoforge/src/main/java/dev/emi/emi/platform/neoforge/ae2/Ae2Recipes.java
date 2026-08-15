package dev.emi.emi.platform.neoforge.ae2;

import java.util.function.BiFunction;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiReloadLog;

/**
 * Everything here touches AE2 directly, so this class must only be resolved once AE2 is known to
 * be loaded. See {@link Ae2EmiIntegration}.
 */
public class Ae2Recipes {

	public static void register(EmiRegistry registry) {
		registry.addCategory(Ae2Categories.INSCRIBER);
		registry.addWorkstation(Ae2Categories.INSCRIBER, EmiStack.of(AEBlocks.INSCRIBER.stack()));
		add(registry, AERecipeTypes.INSCRIBER, Ae2Categories.INSCRIBER, Ae2InscriberEmiRecipe::new);

		registry.addCategory(Ae2Categories.CHARGER);
		registry.addWorkstation(Ae2Categories.CHARGER, EmiStack.of(AEBlocks.CHARGER.stack()));
		add(registry, AERecipeTypes.CHARGER, Ae2Categories.CHARGER, Ae2ChargerEmiRecipe::new);
	}

	/**
	 * A single bad recipe should cost that recipe and nothing else, so each conversion is
	 * isolated. EMI already contains a throwing plugin, but that would drop every remaining AE2
	 * category with it.
	 */
	private static <I extends RecipeInput, T extends Recipe<I>> void add(EmiRegistry registry,
			RecipeType<T> type, EmiRecipeCategory category, BiFunction<Identifier, T, EmiRecipe> factory) {
		var map = registry.getRecipeMap();
		if (map == null) {
			return;
		}
		for (var entry : map.byType(type)) {
			Identifier id = entry.id().identifier();
			try {
				registry.addRecipe(factory.apply(id, entry.value()));
			} catch (Throwable t) {
				EmiReloadLog.warn("Exception parsing AE2 recipe " + id
					+ " for category " + category.getId(), t);
			}
		}
	}
}
