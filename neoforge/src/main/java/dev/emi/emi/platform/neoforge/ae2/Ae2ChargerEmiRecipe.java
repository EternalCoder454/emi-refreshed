package dev.emi.emi.platform.neoforge.ae2;

import java.util.List;

import net.minecraft.resources.Identifier;

import appeng.recipes.handlers.ChargerRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

/**
 * One item in, one charged item out.
 */
public class Ae2ChargerEmiRecipe implements EmiRecipe {
	private final Identifier id;
	private final EmiIngredient input;
	private final EmiStack output;

	public Ae2ChargerEmiRecipe(Identifier id, ChargerRecipe recipe) {
		this.id = id;
		this.input = EmiIngredient.of(recipe.ingredient());
		this.output = EmiStack.of(recipe.result().create());
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return Ae2Categories.CHARGER;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 76;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 1);
		widgets.addSlot(input, 0, 0);
		widgets.addSlot(output, 58, 0).recipeContext(this);
	}
}
