package dev.emi.emi.registry;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dev.emi.emi.api.EmiExclusionArea;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;

public class EmiExclusionAreas {
	public static Map<Class<?>, List<EmiExclusionArea<?>>> fromClass = Maps.newHashMap();
	public static List<EmiExclusionArea<?>> generic = Lists.newArrayList();

	public static void clear() {
		fromClass.clear();
		generic.clear();
	}
	
	public static List<Bounds> getExclusion(EmiScreenBase base) {
		List<Bounds> list = Lists.newArrayList();
		getExclusion(base, list);
		return list;
	}

	/**
	 * Fills {@code out} with the current exclusion areas, replacing whatever was in it.
	 * <p>
	 * This runs from recalculate, which runs from both drawBackground and render, so twice a frame
	 * on a container screen. The result is almost always compared, found identical to the last one
	 * and thrown away, so the caller passes in a list it can keep rather than making this allocate
	 * a fresh one each time.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void getExclusion(EmiScreenBase base, List<Bounds> out) {
		Screen screen = base.screen();
		out.clear();
		Bounds baseBounds = base.bounds();
		out.add(baseBounds);
		// EMI buttons
		out.add(new Bounds(0, screen.height - 22, baseBounds.left(), 22));
		// Search bar
		if (EmiScreenManager.search.isVisible()) {
			out.add(new Bounds(EmiScreenManager.search.x - 1, EmiScreenManager.search.y - 1, EmiScreenManager.search.getWidth() + 2, EmiScreenManager.search.getHeight() + 2));
		}
		try {
			// One consumer for the whole call. addBounds captures the list, so calling it inside the
			// loops built a fresh lambda for every registered provider, every frame, twice.
			Consumer<Bounds> sink = addBounds(out);
			// One lookup, not a containsKey followed by a get.
			List<EmiExclusionArea<?>> forScreen = fromClass.get(screen.getClass());
			if (forScreen != null) {
				for (EmiExclusionArea exclusion : forScreen) {
					exclusion.addExclusionArea(screen, sink);
				}
			}
			for (EmiExclusionArea exclusion : generic) {
				exclusion.addExclusionArea(screen, sink);
			}
		} catch (Exception e) {
			EmiLog.error("Exception thrown when adding exclusion areas", e);
		}
	}

	private static Consumer<Bounds> addBounds(List<Bounds> list) {
		return rect -> {
			// Impossible sizing, or integer overflow
			if (rect.empty() || rect.right() <= rect.x() || rect.bottom() <= rect.y()) {
				return;
			}
			list.add(rect);
		};
	}
}
