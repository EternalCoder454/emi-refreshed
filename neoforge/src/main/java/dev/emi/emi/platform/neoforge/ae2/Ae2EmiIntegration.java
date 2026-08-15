package dev.emi.emi.platform.neoforge.ae2;

import net.neoforged.fml.ModList;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

/**
 * Built in Applied Energistics 2 support.
 *
 * <p>This class deliberately references nothing from AE2. It is discovered and constructed by
 * annotation scanning whether or not AE2 is installed, so touching an AE2 type here would mean
 * loading it here. Everything that does is behind {@link Ae2Recipes}, which is only ever
 * resolved after the mod loaded check passes.
 */
@EmiEntrypoint
public class Ae2EmiIntegration implements EmiPlugin {
	public static final String AE2 = "ae2";

	@Override
	public void register(EmiRegistry registry) {
		if (!ModList.get().isLoaded(AE2)) {
			return;
		}
		Ae2Recipes.register(registry);
	}
}
