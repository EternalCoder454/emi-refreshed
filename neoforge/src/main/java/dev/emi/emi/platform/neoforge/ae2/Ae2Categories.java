package dev.emi.emi.platform.neoforge.ae2;

import net.minecraft.resources.Identifier;

import appeng.core.definitions.AEBlocks;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;

/**
 * Categories for AE2's own machines. Loaded only when AE2 is present.
 */
public class Ae2Categories {
	public static final EmiRecipeCategory INSCRIBER = new EmiRecipeCategory(
		Identifier.fromNamespaceAndPath(Ae2EmiIntegration.AE2, "inscriber"),
		EmiStack.of(AEBlocks.INSCRIBER.stack()));

	public static final EmiRecipeCategory CHARGER = new EmiRecipeCategory(
		Identifier.fromNamespaceAndPath(Ae2EmiIntegration.AE2, "charger"),
		EmiStack.of(AEBlocks.CHARGER.stack()));
}
