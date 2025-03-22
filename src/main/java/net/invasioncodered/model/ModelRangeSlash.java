package net.invasioncodered.model;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.entity.EntityRangeSlash;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModelRangeSlash extends GeoModel<EntityRangeSlash> {
    @Override
    public ResourceLocation getAnimationResource(EntityRangeSlash animatable) {
        return new ResourceLocation(InvasionCodeRed.MODID, "");
    }

    @Override
    public ResourceLocation getModelResource(EntityRangeSlash object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "geo/rangeslash.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityRangeSlash object) {
        return new ResourceLocation(InvasionCodeRed.MODID, "textures/entity/rangeslash.png");
    }
}
