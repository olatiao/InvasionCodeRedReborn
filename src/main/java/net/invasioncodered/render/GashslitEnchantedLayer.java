package net.invasioncodered.render;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.entity.EntityGashslit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class GashslitEnchantedLayer extends GeoRenderLayer<EntityGashslit> {
	private static final ResourceLocation ENCHANTED = new ResourceLocation(InvasionCodeRed.MODID,
			"textures/entity/rage_glint.png");
	private static final ResourceLocation MODEL = new ResourceLocation(InvasionCodeRed.MODID, "geo/gashslit.geo.json");

	public GashslitEnchantedLayer(GeoRenderer<EntityGashslit> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void render(PoseStack poseStack, EntityGashslit animatable, BakedGeoModel bakedModel,
			RenderType renderType, MultiBufferSource bufferSource,
			VertexConsumer buffer, float partialTick, int packedLight,
			int packedOverlay) {
		if (animatable.getHealth() <= 300 && animatable.isAlive()) {
			float f = (float) animatable.tickCount + partialTick;
			RenderType type = RenderType.energySwirl(ENCHANTED, f * -0.02F % 1.0F, f * 0.03F % 1.0F);

			poseStack.pushPose();
			poseStack.scale(1.0F, 1.0F, 1.0F);
			poseStack.translate(0.0D, 0.0D, 0.0D);

			// 获取模型并渲染
			BakedGeoModel gashslitModel = this.getRenderer().getGeoModel().getBakedModel(MODEL);
			this.getRenderer().actuallyRender(
					poseStack,
					animatable,
					gashslitModel,
					renderType,
					bufferSource,
					bufferSource.getBuffer(type),
					true,
					partialTick,
					packedLight,
					OverlayTexture.NO_OVERLAY,
					0.5F, 1.0F, 1.0F, 1.0F);

			poseStack.popPose();
		}
	}
}
