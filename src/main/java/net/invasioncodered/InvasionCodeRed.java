package net.invasioncodered;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.resources.ResourceLocation;
import net.invasioncodered.register.*;
import net.invasioncodered.init.CreativeTabInit;
import net.invasioncodered.config.ConfigGuiFactory;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(InvasionCodeRed.MODID)
public class InvasionCodeRed {
    public static final String MODID = "invasioncodered";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public InvasionCodeRed() {
        @SuppressWarnings("removal")
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        LOGGER.info("Initializing InvasionCodeRed Mod");

        // 注册配置
        InvasionCodeRedConfig.register();
        
        // 注册内容 - 确保实体在物品之前注册
        InvasionCodeRedSounds.SOUND_EVENTS.register(bus);
        InvasionCodeRedParticles.PARTICLES.register(bus);
        InvasionCodeRedEntities.ENTITY_TYPES.register(bus);
        InvasionCodeRedItems.ITEMS.register(bus);
        
        // 注册网络
        InvasionCodeRedNetwork.registerMessages();
        
        // 注册创造模式物品栏
        CreativeTabInit.register(bus);
        
        // 注册配置GUI (只在客户端环境)
        if (FMLEnvironment.dist.isClient()) {
            ConfigGuiFactory.registerConfigScreen();
        }
        
        LOGGER.info("InvasionCodeRed Mod initialized successfully");
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static String stringID(String name) {
        return MODID + ":" + name;
    }
}
