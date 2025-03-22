package net.invasioncodered.render;

import net.invasioncodered.entity.EntityGashslitDragon;
import net.invasioncodered.model.ModelGashslitDragon;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GashslitDragonRenderer extends GeoEntityRenderer<EntityGashslitDragon>
{
	public GashslitDragonRenderer(Context renderManager) 
	{
		super(renderManager, new ModelGashslitDragon());
	}
}
