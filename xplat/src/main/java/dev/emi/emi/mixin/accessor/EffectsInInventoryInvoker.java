package dev.emi.emi.mixin.accessor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EffectsInInventory.class)
public interface EffectsInInventoryInvoker {

	@Invoker("renderBackground")
	int emi$invokeRenderBackground(GuiGraphics graphics, Font font, Component effectName, Component duration, int x0, int y0, boolean isAmbient, int maxTextureWidth);

	@Invoker("renderText")
	void emi$invokeRenderText(GuiGraphics graphics, Component effectText, Component duration, Font font, int x0, int y0, int textureWidth, int yStep, int mouseX, int mouseY);
}
