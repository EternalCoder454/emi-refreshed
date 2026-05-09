package dev.emi.emi.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface HandledScreenAccessor {
	
	@Accessor("focusedSlot")
	Slot getFocusedSlot();

	@Accessor("x")
	int getX();

	@Accessor("y")
	int getY();

	@Accessor("backgroundWidth")
	int getBackgroundWidth();

	@Accessor("backgroundHeight")
	int getBackgroundHeight();

	@Invoker("getSlotAt")
	Slot invokeGetSlotAt(double x, double y);
}
