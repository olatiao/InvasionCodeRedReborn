package net.invasioncodered.model;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.entity.EntityGashslitDragon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModelGashslitDragon extends GeoModel<EntityGashslitDragon> {
    @Override
    public ResourceLocation getAnimationResource(EntityGashslitDragon animatable) {
        return new ResourceLocation(InvasionCodeRed.MODID, "animations/gashslitdragon.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(EntityGashslitDragon object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "geo/gashslitdragon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityGashslitDragon object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/gashslitdragon.png");
    }
}
