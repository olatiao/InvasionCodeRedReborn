package net.invasioncodered.render;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.config.RenderTypeConfig;
import net.invasioncodered.entity.EntityRangeSlash;
import net.invasioncodered.model.ModelRangeSlash;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class RangeSlashRenderer extends GeoEntityRenderer<EntityRangeSlash>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/rangeslash.png");
	private static final ResourceLocation TEXTURE_BIG = new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/rangeslash_big.png");
	
	public RangeSlashRenderer(Context context) 
	{
		super(context, new ModelRangeSlash());
	}
	
	@SuppressWarnings("resource")
	@Override
	public void render(EntityRangeSlash animatable, float entityYaw, float partialTick, PoseStack poseStack, 
	                   MultiBufferSource bufferSource, int packedLight)
	{
		poseStack.pushPose();
		poseStack.mulPose(new Quaternionf().rotationY((Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 180F) * ((float)Math.PI / 180F)));
		poseStack.mulPose(new Quaternionf().rotationZ(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) * ((float)Math.PI / 180F)));
		
		// 获取模型
        @SuppressWarnings("removal")
		BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable));
		
		if(animatable.isBig())
		{
			poseStack.scale(1.5F, 1.5F, 1.5F);
		}
		else
		{
			poseStack.scale(0.75F, 0.75F, 0.75F);
		}
        poseStack.translate(0, -0.5f, 0);
        
        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
        RenderType renderType = animatable.isBig() ? RenderType.eyes(getTextureLocation(animatable))
                : RenderTypeConfig.getGlowingEffect(getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) 
		{
			// 使用新的渲染方法
			this.actuallyRender(
			    poseStack,
			    animatable,
			    model,
			    renderType,
			    bufferSource,
			    bufferSource.getBuffer(renderType),
			    true,
			    partialTick,
			    packedLight,
			    OverlayTexture.NO_OVERLAY,
			    1.0F, 1.0F, 1.0F, 1.0F
			);
		}
		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityRangeSlash p_114482_) 
	{
		return p_114482_.isBig() ? TEXTURE_BIG : TEXTURE;
	}
}
