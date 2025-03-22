package net.invasioncodered.init;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.register.InvasionCodeRedItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 初始化创造模式物品栏
 */
public class CreativeTabInit {
    // 创建注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InvasionCodeRed.MODID);

    // 定义创造模式物品栏
    public static final RegistryObject<CreativeModeTab> INVASION_TAB = CREATIVE_TABS.register("invasion_tab", 
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + InvasionCodeRed.MODID + ".invasion_tab"))
                .icon(() -> new ItemStack(InvasionCodeRedItems.GASHSLIT_SPAWN_EGG.get()))
                .build());
    
    /**
     * 注册创造模式物品栏
     * @param eventBus Forge事件总线
     */
    public static void register(IEventBus eventBus) {
        InvasionCodeRed.LOGGER.info("Registering creative tabs");
        CREATIVE_TABS.register(eventBus);
        
        // 添加监听器来填充物品栏
        eventBus.addListener(CreativeTabInit::buildCreativeTabContents);
    }
    
    /**
     * 在创造模式物品栏中添加模组物品
     * @param event 创造模式物品栏内容构建事件
     */
    private static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == INVASION_TAB.get()) {
            // 添加模组物品到自定义物品栏
            event.accept(InvasionCodeRedItems.GASHSLIT_SPAWN_EGG.get());
        }
    }
} 