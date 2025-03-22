package net.invasioncodered.client;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.register.InvasionCodeRedEntities;
import net.invasioncodered.render.GashslitDragonRenderer;
import net.invasioncodered.render.GashslitRenderer;
import net.invasioncodered.render.RangeSlashRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 处理客户端侧的实体渲染器注册
 */
@Mod.EventBusSubscriber(modid = InvasionCodeRed.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRendererRegistry {
    
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        InvasionCodeRed.LOGGER.info("Registering entity renderers for Invasion Code Red");
        
        // 注册实体渲染器
        event.registerEntityRenderer(InvasionCodeRedEntities.GASHSLIT.get(), GashslitRenderer::new);
        event.registerEntityRenderer(InvasionCodeRedEntities.GASHSLIT_DRAGON.get(), GashslitDragonRenderer::new);
        event.registerEntityRenderer(InvasionCodeRedEntities.RANGE_SLASH.get(), RangeSlashRenderer::new);
    }
} 