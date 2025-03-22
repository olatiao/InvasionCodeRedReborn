package net.invasioncodered.render;

import java.util.Optional;

import net.invasioncodered.entity.EntityGashslit;
import net.invasioncodered.model.ModelGashslit;
import net.invasioncodered.register.InvasionCodeRedParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3d;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class GashslitRenderer extends GeoEntityRenderer<EntityGashslit> {
	public GashslitRenderer(Context renderManager) {
		super(renderManager, new ModelGashslit());
		this.addRenderLayer(new GashslitSusanooRenderer(this));
		this.addRenderLayer(new GashslitEnchantedLayer(this));
	}

	@Override
	public void render(EntityGashslit animatable, float entityYaw, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight) {
		Level level = animatable.level();
		if (level == null) {
			super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
			return;
		}
		
        @SuppressWarnings("removal")
		BakedGeoModel model = this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(animatable));
		
		// 处理骨骼locator的粒子效果
		renderBoneParticles(model, "locator", level, animatable);
		
		// 处理骨骼locator2的粒子效果
		renderBoneParticles(model, "locator2", level, animatable);

		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
	
	private void renderBoneParticles(BakedGeoModel model, String boneName, Level level, EntityGashslit animatable) {
		Optional<GeoBone> bone = Optional.ofNullable(model.getBone(boneName).orElse(null));
		if (!bone.isPresent()) return;
		
		int skinID = animatable.getSkinID();
		boolean shouldRender = false;
		
		if ("locator".equals(boneName)) {
			shouldRender = skinID == 3 || skinID == 2 || skinID == 9 || skinID == 5;
		} else if ("locator2".equals(boneName)) {
			shouldRender = skinID == 3 || skinID == 1 || skinID == 9;
		}
		
		if (shouldRender) {
			float radius = 0.25F;
			Vector3d pos = new Vector3d(bone.get().getWorldPosition().x(), bone.get().getWorldPosition().y(),
					bone.get().getWorldPosition().z());
					
			for (float i = 0; i < radius; i++) {
				float angle = ((float) Math.PI * 2) / radius * (i + level.getRandom().nextFloat());
				double x = pos.x();
				double z = pos.z();
				double xPos = x + radius * Mth.sin(angle);
				double zPos = z + radius * Mth.cos(angle);
				level.addParticle(InvasionCodeRedParticles.SLASH_HIT.get(), xPos, pos.y(), zPos, 0, 0, 0.05);
			}
		}
	}
}
