package dev.emi.emi.platform.neoforge.ae2;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.resources.Identifier;

import appeng.recipes.handlers.InscriberRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

/**
 * The inscriber takes a required middle input plus optional top and bottom presses, laid out
 * vertically the way the machine's own screen presents them.
 */
public class Ae2InscriberEmiRecipe implements EmiRecipe {
	private final Identifier id;
	private final EmiIngredient top;
	private final EmiIngredient middle;
	private final EmiIngredient bottom;
	private final List<EmiIngredient> inputs;
	private final EmiStack output;

	public Ae2InscriberEmiRecipe(Identifier id, InscriberRecipe recipe) {
		this.id = id;
		this.top = recipe.getTopOptional().map(EmiIngredient::of).orElse(EmiStack.EMPTY);
		this.middle = EmiIngredient.of(recipe.getMiddleInput());
		this.bottom = recipe.getBottomOptional().map(EmiIngredient::of).orElse(EmiStack.EMPTY);
		this.output = EmiStack.of(recipe.result().create());

		// Only the slots that actually carry an ingredient count as inputs, otherwise the empty
		// press slots would show up as blanks in recipe trees and fill handling.
		List<EmiIngredient> inputs = Lists.newArrayList();
		if (!this.top.isEmpty()) {
			inputs.add(this.top);
		}
		inputs.add(this.middle);
		if (!this.bottom.isEmpty()) {
			inputs.add(this.bottom);
		}
		this.inputs = List.copyOf(inputs);
	}

	/**
	 * Which press slots this recipe actually uses. The handler needs this to line the crafting
	 * slots up with {@link #getInputs()}, which omits the empty ones.
	 */
	public boolean hasTop() {
		return !top.isEmpty();
	}

	public boolean hasBottom() {
		return !bottom.isEmpty();
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return Ae2Categories.INSCRIBER;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 98;
	}

	@Override
	public int getDisplayHeight() {
		return 54;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 44, 19);
		widgets.addSlot(top, 0, 0);
		widgets.addSlot(middle, 0, 18);
		widgets.addSlot(bottom, 0, 36);
		widgets.addSlot(output, 76, 18).recipeContext(this);
	}
}
