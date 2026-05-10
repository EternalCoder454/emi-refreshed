package dev.emi.emi;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.mixin.accessor.SmithingTransformRecipeAccessor;
import dev.emi.emi.registry.EmiRecipes;

public final class EmiPort {
	private static final net.minecraft.util.RandomSource RANDOM = net.minecraft.util.RandomSource.create();

	public static MutableComponent literal(String s) {
		return Component.literal(s);
	}

	public static MutableComponent literal(String s, ChatFormatting formatting) {
		return Component.literal(s).withStyle(formatting);
	}

	public static MutableComponent literal(String s, ChatFormatting... formatting) {
		return Component.literal(s).withStyle(formatting);
	}

	public static MutableComponent literal(String s, Style style) {
		return Component.literal(s).setStyle(style);
	}
	
	public static MutableComponent translatable(String s) {
		return Component.translatable(s);
	}
	
	public static MutableComponent translatable(String s, ChatFormatting formatting) {
		return Component.translatable(s).withStyle(formatting);
	}
	
	public static MutableComponent translatable(String s, Object... objects) {
		return Component.translatable(s, objects);
	}

	public static MutableComponent append(MutableComponent text, Component appended) {
		return text.append(appended);
	}

	public static FormattedCharSequence ordered(Component text) {
		return text.getVisualOrderText();
	}

	public static Collection<ResourceLocation> findResources(ResourceManager manager, String prefix, Predicate<String> pred) {
		return manager.listResources(prefix, i -> pred.test(i.toString())).keySet();
	}

	public static InputStream getInputStream(Resource resource) {
		try {
			return resource.open();
		} catch (Exception e) {
			return null;
		}
	}

	public static BannerPatternLayers addRandomBanner(BannerPatternLayers patterns, Random random) {
		var bannerRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);
		return new BannerPatternLayers.Builder().addAll(patterns).add(bannerRegistry.get(random.nextInt(bannerRegistry.size())).orElseThrow(),
			DyeColor.values()[random.nextInt(DyeColor.values().length)]).build();
	}

	public static boolean canTallFlowerDuplicate(TallFlowerBlock tallFlowerBlock) {
		try {
			return tallFlowerBlock.isValidBonemealTarget(null, null, null) && tallFlowerBlock.isBonemealSuccess(null, null, null, null);
		} catch(Exception e) {
			return false;
		}
	}

	public static void draw(BufferBuilder bufferBuilder, RenderType renderType) {
		MeshData meshData = bufferBuilder.buildOrThrow();
		renderType.draw(meshData);
	}

	public static int getGuiScale(Minecraft client) {
		return (int) client.getWindow().getGuiScale();
	}

	public static Registry<Item> getItemRegistry() {
		return BuiltInRegistries.ITEM;
	}

	public static Registry<Block> getBlockRegistry() {
		return BuiltInRegistries.BLOCK;
	}

	public static Registry<Fluid> getFluidRegistry() {
		return BuiltInRegistries.FLUID;
	}

	public static Registry<Potion> getPotionRegistry() {
		return BuiltInRegistries.POTION;
	}

	public static Registry<Enchantment> getEnchantmentRegistry() {
		Minecraft client = Minecraft.getInstance();
		return client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
	}

	public static Button newButton(int x, int y, int w, int h, Component name, OnPress action) {
		return Button.builder(name, action).pos(x, y).size(w, h).build();
	}

	public static ItemStack getOutput(Recipe<?> recipe) {
		Minecraft client = Minecraft.getInstance();
		if (recipe instanceof ShapedRecipe shaped) {
			return shaped.assemble(CraftingInput.EMPTY, client.level.registryAccess());
		} else if (recipe instanceof ShapelessRecipe shapeless) {
			return shapeless.assemble(CraftingInput.EMPTY, client.level.registryAccess());
		} else if (recipe instanceof SingleItemRecipe single) {
			return single.assemble(new SingleRecipeInput(ItemStack.EMPTY), client.level.registryAccess());
		} else if (recipe instanceof SmithingTransformRecipe smithing) {
			TransmuteResult result = ((SmithingTransformRecipeAccessor) smithing).getResult();
			return new ItemStack(result.item(), result.count(), result.components());
		}
		return ItemStack.EMPTY;
	}

	public static void focus(EditBox widget, boolean focused) {
		Minecraft client = Minecraft.getInstance();
		if (client != null && client.screen != null) {
			var currentFocus = client.screen.getFocused();
			if (!focused && currentFocus == widget || focused && currentFocus != widget) {
				client.screen.setFocused(null);
			}
		}
		widget.setFocused(focused);
	}

	public static Stream<Item> getDisabledItems() {
		Minecraft client = Minecraft.getInstance();
		FeatureFlagSet fs = client.level.enabledFeatures();
		return getItemRegistry().stream().filter(i -> !i.isEnabled(fs));
	}

	public static ResourceLocation getId(Recipe<?> recipe) {
		return EmiRecipes.recipeIds.get(recipe);
	}

	public static @Nullable RecipeHolder<?> getRecipe(ResourceLocation id) {
		RecipeManager manager = getRecipeManager();
		if (manager != null && id != null) {
			return manager.byKey(ResourceKey.create(Registries.RECIPE, id)).orElse(null);
		}
		return null;
	}

	public static @Nullable RecipeManager getRecipeManager() {
		Minecraft client = Minecraft.getInstance();
		if (client.isSingleplayer() && client.getSingleplayerServer() != null) {
			return client.getSingleplayerServer().getRecipeManager();
		}
		return null;
	}

	public static Comparison compareStrict() {
		return Comparison.compareComponents();
	}

	public static ItemStack setPotion(ItemStack stack, Potion potion) {
		stack.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, getPotionRegistry().wrapAsHolder(potion), PotionContents::withPotion);
		return stack;
	}

	public static DataComponentPatch emptyExtraData() {
		return DataComponentPatch.EMPTY;
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.parse(id);
	}

	public static ResourceLocation id(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

}
