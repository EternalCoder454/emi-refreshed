package dev.emi.emi.api.stack;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.ApiStatus;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.StackBatcher.Batchable;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;

@ApiStatus.Internal
public class ItemEmiStack extends EmiStack implements Batchable {
	private static final Minecraft client = Minecraft.getInstance();

	private final Item item;
	private final DataComponentPatch componentChanges;

	private boolean unbatchable;

	public ItemEmiStack(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public ItemEmiStack(ItemStack stack, long amount) {
		this(stack.getItem(), stack.getComponentsPatch(), amount);
	}

	public ItemEmiStack(Item item, DataComponentPatch components, long amount) {
		this.item = item;
		this.componentChanges = components;
		this.amount = amount;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(EmiPort.getItemRegistry().wrapAsHolder(this.item), (int) this.amount, componentChanges);
	}

	@Override
	public EmiStack copy() {
		EmiStack e = new ItemEmiStack(item, componentChanges, amount);
		e.setChance(chance);
		e.setRemainder(getRemainder().copy());
		e.comparison = comparison;
		return e;
	}

	@Override
	public boolean isEmpty() {
		return amount == 0 || item == Items.AIR;
	}

	@Override
	public DataComponentPatch getComponentChanges() {
		return this.componentChanges;
	}

	@Override
	public <T> @Nullable T get(DataComponentType<? extends T> type) {
		// Check the changes first
		var changedOpt = this.componentChanges.get(type);
		//noinspection OptionalAssignedToNull
		if(changedOpt != null) {
			return changedOpt.orElse(null);
		}
		// Check the item's default components
		return this.item.components().get(type);
	}

	@Override
	public Object getKey() {
		return item;
	}

	@Override
	public Identifier getId() {
		return EmiPort.getItemRegistry().getKey(item);
	}

	@Override
	public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
		EmiDrawContext context = EmiDrawContext.wrap(draw);
		ItemStack stack = getItemStack();
		if ((flags & RENDER_ICON) != 0) {
			Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
			draw.renderFakeItem(stack, x, y);
			draw.renderItemDecorations(client.font, stack, x, y, "");
		}
		if ((flags & RENDER_AMOUNT) != 0) {
			String count = "";
			if (amount != 1) {
				count += amount;
			}
			EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal(count));
		}
		if ((flags & RENDER_REMAINDER) != 0) {
			EmiRender.renderRemainderIcon(this, context.raw(), x, y);
		}
	}
	
	@Override
	public boolean isSideLit() {
		ItemStackRenderState state = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, getItemStack(), ItemDisplayContext.GUI, null, null, 0);
		return state.usesBlockLight();
	}
	
	@Override
	public boolean isUnbatchable() {
		ItemStack stack = getItemStack();
		return unbatchable || stack.hasFoil() || stack.isDamaged() || !EmiAgnos.canBatch(stack);
	}
	
	@Override
	public void setUnbatchable() {
		this.unbatchable = true;
	}
	
	@Override
	public void renderForBatch(MultiBufferSource vcp, GuiGraphics draw, int x, int y, int z, float delta) {
		ItemStack stack = getItemStack();
		ItemStackRenderState state = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, null, null, 0);
		PoseStack matrices = new PoseStack();
		matrices.translate(x, y, 100.0f + z + (state.usesBlockLight() ? 50 : 0));
		matrices.translate(8.0, 8.0, 0.0);
		matrices.scale(16.0f, -16.0f, 16.0f);
		MultiBufferSource finalVcp = vcp;
		state.submit(matrices, new SubmitNodeCollector() {
			@Override
			public OrderedSubmitNodeCollector order(int i) {
				return this;
			}

			@Override
			public void submitItem(PoseStack posestack, ItemDisplayContext itemdisplaycontext, int i, int j, int k, int[] aint, List<BakedQuad> list, RenderType rendertype, ItemStackRenderState.FoilType foiltype) {
				ItemRenderer.renderItem(itemdisplaycontext, posestack, finalVcp, i, j, aint, list, rendertype, foiltype);
			}

			@Override
			public void submitCustomGeometry(PoseStack posestack, RenderType rendertype, SubmitNodeCollector.CustomGeometryRenderer customgeometryrenderer) {
				customgeometryrenderer.render(posestack.last(), finalVcp.getBuffer(rendertype));
			}

			@Override
			public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer particlegrouprenderer) {
			}

			@Override
			public void submitBlockModel(PoseStack posestack, RenderType rendertype, BlockStateModel blockstatemodel, float f, float f1, float f2, int i, int j, int k) {
			}

			@Override
			public void submitMovingBlock(PoseStack posestack, MovingBlockRenderState movingblockrenderstate) {
			}

			@Override
			public void submitShadow(PoseStack posestack, float f, List<EntityRenderState.ShadowPiece> list) {
			}

			@Override
			public void submitNameTag(PoseStack posestack, Vec3 vec3, int i, Component component, boolean flag, int j, double d, CameraRenderState camerarenderstate) {
			}

			@Override
			public void submitText(PoseStack posestack, float f, float f1, FormattedCharSequence formattedcharsequence, boolean flag, Font.DisplayMode displaymode, int i, int j, int k, int l) {
			}

			@Override
			public void submitFlame(PoseStack posestack, EntityRenderState entityrenderstate, Quaternionf quaternionf) {
			}

			@Override
			public void submitLeash(PoseStack posestack, EntityRenderState.LeashState leashstate) {
			}

			@Override
			public <S> void submitModel(Model<? super S> model, S s, PoseStack posestack, RenderType rendertype, int i, int j, int k, TextureAtlasSprite textureatlassprite, int l, ModelFeatureRenderer.CrumblingOverlay crumblingoverlay) {
			}

			@Override
			public void submitModelPart(ModelPart modelpart, PoseStack posestack, RenderType rendertype, int i, int j, TextureAtlasSprite textureatlassprite, boolean flag, boolean flag1, int k, ModelFeatureRenderer.CrumblingOverlay crumblingoverlay, int l) {
			}

			@Override
			public void submitBlock(PoseStack posestack, BlockState blockstate, int i, int j, int k) {
			}
		}, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

	@Override
	public List<Component> getTooltipText() {
		if (client.isSameThread()) {
			return getItemStack().getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL);
		} else {
			// Don't provide world or entity as context, as they are not thread safe
			return getItemStack().getTooltipLines(Item.TooltipContext.of(client.level.registryAccess()), null, TooltipFlag.NORMAL);
		}
	}

	@Override
	public List<ClientTooltipComponent> getTooltip() {
		ItemStack stack = getItemStack();
		List<ClientTooltipComponent> list = Lists.newArrayList();
		if (!isEmpty()) {
			list.addAll(EmiAgnos.getItemTooltip(stack));
			if (!list.isEmpty() && list.get(0) instanceof ClientTextTooltip ottc) {
				list.set(0, new EmiTextTooltipWrapper(this, ottc));
			}
			//String namespace = EmiPort.getItemRegistry().getId(stack.getItem()).getNamespace();
			//String mod = EmiUtil.getModName(namespace);
			//list.add(TooltipComponent.of(EmiLang.literal(mod, Formatting.BLUE, Formatting.ITALIC)));
			list.addAll(super.getTooltip());
		}
		return list;
	}

	@Override
	public Component getName() {
		if (isEmpty()) {
			return EmiPort.literal("");
		}
		return getItemStack().getHoverName();
	}

	static class ItemEntry {
	}
}