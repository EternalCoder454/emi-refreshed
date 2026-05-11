package dev.emi.emi.screen.tooltip;

import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;

public interface EmiTooltipComponent extends ClientTooltipComponent {

	default void drawTooltip(EmiDrawContext context, TooltipRenderData tooltip) {
	}

	default void drawTooltipText(TextRenderData text) {
	}

	@Override
	default int getWidth(Font font) {
		return 0;
	}

	@Override
	default void renderImage(Font textRenderer, int x, int y, int width, int height, GuiGraphics raw) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		context.push();
		context.matrices().translate(x, y);
		context.setOverlay(true);
		Minecraft client = Minecraft.getInstance();
		drawTooltip(context, new TooltipRenderData(textRenderer, client.getItemRenderer(), x, y));
		context.setOverlay(false);
		context.pop();
	}

	@Override
	default void renderText(GuiGraphics graphics, Font font, int x, int y) {
		drawTooltipText(new TextRenderData(graphics, font, x, y));
	}

	public static class TextRenderData {
		public final GuiGraphics graphics;
		public final Font renderer;
		public final int x, y;
		
		public TextRenderData(GuiGraphics graphics, Font renderer, int x, int y) {
			this.graphics = graphics;
			this.renderer = renderer;
			this.x = x;
			this.y = y;
		}

		public void draw(String text, int x, int y, int color, boolean shadow) {
			draw(EmiPort.literal(text), x, y, color, shadow);
		}

		public void draw(Component text, int x, int y, int color, boolean shadow) {
			graphics.drawString(renderer, text, x + this.x, y + this.y, color | 0xFF000000, shadow);
		}
	}

	public static class TooltipRenderData {
		public final Font text;
		public final ItemRenderer item;
		public final int x, y;

		public TooltipRenderData(Font text, ItemRenderer item, int x, int y) {
			this.text = text;
			this.item = item;
			this.x = x;
			this.y = y;
		}
	}
}
