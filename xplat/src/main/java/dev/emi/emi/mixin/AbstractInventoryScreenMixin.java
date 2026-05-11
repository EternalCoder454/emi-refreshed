package dev.emi.emi.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Ordering;

import dev.emi.emi.config.EffectLocation;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.mixin.accessor.HandledScreenAccessor;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;

@Mixin(EffectsInInventory.class)
public abstract class AbstractInventoryScreenMixin {
	@Unique
	private static boolean hasInventoryTabs = EmiAgnos.isModLoaded("inventorytabs");

	@Shadow
	private AbstractContainerScreen<?> screen;

	@Shadow
	private Minecraft minecraft;

	@Shadow
	private Component getEffectName(MobEffectInstance effect) {
		throw new UnsupportedOperationException();
	}

	@Shadow
	private void renderBackgrounds(GuiGraphics draw, int x, int height, Iterable<MobEffectInstance> statusEffects, boolean wide) {
		throw new UnsupportedOperationException();
	}

	@Shadow
	private void renderIcons(GuiGraphics draw, int x, int height, Iterable<MobEffectInstance> statusEffects, boolean wide) {
		throw new UnsupportedOperationException();
	}

	@Shadow
	private void renderLabels(GuiGraphics draw, int x, int height, Iterable<MobEffectInstance> statusEffects) {
		throw new UnsupportedOperationException();
	}

	@Inject(at = @At(value = "INVOKE",
			target = "net/minecraft/client/gui/screens/inventory/EffectsInInventory.renderBackgrounds(Lnet/minecraft/client/gui/GuiGraphics;IILjava/lang/Iterable;Z)V"),
			method = "renderEffects")
	private void drawStatusEffects(GuiGraphics draw, int mouseX, int mouseY, CallbackInfo info) {
		if (EmiConfig.effectLocation == EffectLocation.TOP) {
			emi$drawCenteredEffects(draw, mouseX, mouseY);
		}
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "java/util/Collection.size()I", ordinal = 0),
			method = "renderEffects", ordinal = 0)
	private Collection<MobEffectInstance> drawStatusEffects(Collection<MobEffectInstance> original) {
		if (EmiConfig.effectLocation == EffectLocation.TOP || EmiConfig.effectLocation == EffectLocation.HIDDEN) {
			return List.of();
		}
		return original;
	}

	private void emi$drawCenteredEffects(GuiGraphics raw, int mouseX, int mouseY) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		context.resetColor();
		Collection<MobEffectInstance> effects = Ordering.natural().sortedCopy(this.minecraft.player.getActiveEffects());
		int size = effects.size();
		if (size == 0) {
			return;
		}
		boolean wide = size == 1;
		HandledScreenAccessor acc = (HandledScreenAccessor) this.screen;
		int y = acc.getY() - 34;
		if (this.screen instanceof CreativeModeInventoryScreen || hasInventoryTabs) {
			y -= 28;
			if (this.screen instanceof CreativeModeInventoryScreen && EmiAgnos.isForge()) {
				y -= 22;
			}
		}
		int xOff = 34;
		if (wide) {
			xOff = 122;
		} else if (size > 5) {
			xOff = (acc.getBackgroundWidth() - 32) / (size - 1);
		}
		int width = (size - 1) * xOff + (wide ? 120 : 32);
		int x = acc.getX() + (acc.getBackgroundWidth() - width) / 2;
		MobEffectInstance hovered = null;
		int restoreY = acc.getY();
		try {
			acc.setY(y);
			for (MobEffectInstance inst : effects) {
				int ew = wide ? 120 : 32;
				List<MobEffectInstance> single = List.of(inst);
				this.renderBackgrounds(context.raw(), x, 32, single, wide);
				this.renderIcons(context.raw(), x, 32, single, wide);
				if (wide) {
					this.renderLabels(context.raw(), x, 32, single);
				}
				if (mouseX >= x && mouseX < x + ew && mouseY >= y && mouseY < y + 32) {
					hovered = inst;
				}
				x += xOff;
			}
		} finally {
			acc.setY(restoreY);
		}
		if (hovered != null && size > 1) {
			List<Component> list = List.of(this.getEffectName(hovered), MobEffectUtil.formatDuration(hovered, 1.0f, minecraft.level.tickRateManager().tickrate()));
			List<ClientTooltipComponent> components = list.stream()
					.map(Component::getVisualOrderText)
					.map(ClientTooltipComponent::create)
					.toList();
			context.deferTooltip(() -> context.raw().renderTooltip(minecraft.font, components, mouseX, Math.max(mouseY, 16), DefaultTooltipPositioner.INSTANCE, null));
		}
	}

	@ModifyVariable(at = @At(value = "STORE", ordinal = 0),
			method = "renderEffects", ordinal = 0)
	private boolean squishEffects(boolean original) {
		return !EmiConfig.effectLocation.compressed;
	}

	@ModifyVariable(at = @At(value = "STORE", ordinal = 0),
			method = "renderEffects", ordinal = 2)
	private int changeEffectSpace(int original) {
		return switch (EmiConfig.effectLocation) {
			case RIGHT, RIGHT_COMPRESSED, HIDDEN -> original;
			case TOP -> ((HandledScreenAccessor) this.screen).getX();
			case LEFT_COMPRESSED -> ((HandledScreenAccessor) this.screen).getX() - 2 - 32;
			case LEFT -> ((HandledScreenAccessor) this.screen).getX() - 2 - 120;
		};
	}
}
