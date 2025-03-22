package net.invasioncodered.model;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.entity.EntityGashslit;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModelGashslit extends GeoModel<EntityGashslit> {
    @Override
    public ResourceLocation getAnimationResource(EntityGashslit animatable) {
        return new ResourceLocation(InvasionCodeRed.MODID, "animations/gashslit.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(EntityGashslit object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "geo/gashslit.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityGashslit object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/gashslit.png");
    }
}
