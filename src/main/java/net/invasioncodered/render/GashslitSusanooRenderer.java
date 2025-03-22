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

public class GashslitSusanooRenderer extends GeoRenderLayer<EntityGashslit>
{
	private static final ResourceLocation CHARGED = new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/rage_glint4.png");
    private static final ResourceLocation MODEL = new ResourceLocation(InvasionCodeRed.MODID, "geo/susano.geo.json");
	
	public GashslitSusanooRenderer(GeoRenderer<EntityGashslit> entityRendererIn) 
	{
		super(entityRendererIn);
	}

	@Override
	public void render(PoseStack poseStack, EntityGashslit animatable, BakedGeoModel bakedModel, 
                     RenderType renderType, MultiBufferSource bufferSource, 
                     VertexConsumer buffer, float partialTick, int packedLight, 
                     int packedOverlay)
	{
		if(animatable.getHealth() <= 300 && animatable.isAlive())
		{
	        float f = (float)animatable.tickCount + partialTick;
	        RenderType type = RenderType.energySwirl(CHARGED, f * 0.01F, f * 0.01F);
	        poseStack.pushPose();
	        poseStack.scale(1.0F, 1.0F, 1.0F);
	        poseStack.translate(0.0D, 0.0D, 0.0D);
	        
	        // 获取模型并渲染
            BakedGeoModel susanooModel = this.getRenderer().getGeoModel().getBakedModel(MODEL);
            this.getRenderer().actuallyRender(
                poseStack, 
                animatable, 
                susanooModel,
                renderType,
                bufferSource, 
                bufferSource.getBuffer(type), 
                true, 
                partialTick, 
                packedLight,
                OverlayTexture.NO_OVERLAY, 
                1.0F, 1.0F, 1.0F, 1.0F);
                
	        poseStack.popPose();
		}
	}
}
